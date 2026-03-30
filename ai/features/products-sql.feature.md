# 🤖 AI_FEATURE_Products SQL

## 🎯 Feature Identity

- **Feature Name:** Products SQL - Retrieval & Deletion by Restaurant
- **Related Area:** Backend / API / Database

---

## 🎪 Feature Goal

Enable retrieval of all products for a specific restaurant using native SQL queries, and support deletion of all products linked to a restaurant when the restaurant is deleted. The API must efficiently fetch products by restaurant context and support cascade deletion to maintain referential integrity when a restaurant is removed.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL SELECT query with parameterized bindings for retrieving all products by restaurant ID
- Native SQL DELETE query with parameterized bindings for deleting all products by restaurant ID
- Query parameter parsing (restaurant ID from query string)
- Request validation (ensure valid numeric restaurant ID)
- Response object containing list of products or success/error message
- Error handling for invalid queries, non-existent restaurants, or database errors
- Cascade deletion handling (product_order records must also be deleted when products are deleted)
- Service layer logic to delegate business logic from controller
- Integration with ProductRepository using native SQL

### ❌ Out of Scope (Excluded)

- Modifying the Product entity model
- Delete by product category or price range
- Complex filtering by availability or ratings
- Product update or edit endpoints
- Frontend UI or form validation
- Retrieving products is for a specific order (that's order-specific product retrieval)
- Authentication/authorization checks (assume user is authenticated)
- Batch creation of products

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL SELECT by Restaurant:** Write parameterized SELECT query in ProductRepository to fetch all products for a specific restaurant ID
- **SQL DELETE by Restaurant:** Write parameterized DELETE query in ProductRepository to remove all products for a specific restaurant ID
- **Cascade Delete Logic:** Handle deletion of related product_order entries when products are deleted
- **Query Parameter Parsing:** Parse `restaurant` ID from query string parameter
- **Service Logic:** Implement methods in ProductService to handle retrieval and deletion
- **Controller Endpoint:** Implement GET and DELETE methods in controller
- **Error Handling:** Handle invalid restaurant ID, missing restaurants, validation errors, and database errors
- **Response Format:** Use ResponseBuilder to construct consistent API responses

---

## 👥 User Flow / Logic (High Level)

### GET Products by Restaurant Flow
1. Mobile app sends GET request to `/api/products?restaurant=3`
2. Controller receives restaurant ID and validates it's a valid positive number
3. Controller delegates to ProductService.getProductsByRestaurant(restaurantId)
4. Service calls ProductRepository.findProductsByRestaurantId(restaurantId)
5. Repository executes parameterized SQL SELECT query
6. Database returns list of products for that restaurant
7. Service returns list of products to controller
8. Controller returns HTTP 200 with list of products in response body
9. If no products found → return empty list with 200 status (not an error)
10. If restaurant ID not found → return 404 Not Found (optional, or return empty list)
11. If invalid ID format → return 400 Bad Request

### DELETE Products by Restaurant Flow
1. System initiates deletion of a restaurant via DELETE /api/restaurants/{restaurantId}
2. Restaurant controller verifies the restaurant exists
3. Before deleting the restaurant, initiates cascade deletion of products
4. Controller calls DELETE /api/products?restaurant={restaurantId} (or service handles internally)
5. ProductService receives restaurant ID and validates it
6. Service must first delete all related product_order entries:
   - Query: DELETE FROM product_order WHERE product_id IN (SELECT id FROM product WHERE restaurant_id = ?1)
7. Then delete all products: DELETE FROM product WHERE restaurant_id = ?1
8. Service returns confirmation to caller
9. Caller then proceeds to delete the restaurant itself
10. Controller returns HTTP 200 with success message
11. If no products exist for restaurant → return 200 (nothing to delete, not an error)
12. If database error → return 500 Internal Server Error

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### GET /api/products (Filter by Restaurant)
- **Request Parameter:**
  - `restaurant` (Long, required) — Restaurant ID to fetch products for
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (List<ApiProductDTO>) — List of products for the restaurant
- **HTTP Status Codes:**
  - 200 OK — Products retrieved successfully (may be empty list)
  - 400 Bad Request — Invalid or missing restaurant ID parameter
  - 404 Not Found — Restaurant ID does not exist (optional behavior)
  - 500 Internal Server Error — Database or server error

#### DELETE /api/products (Batch by Restaurant)
- **Request Parameter:**
  - `restaurant` (Long, required) — Restaurant ID whose products should be deleted
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (Object or null) — Optional deletion count confirmation
- **HTTP Status Codes:**
  - 200 OK — Products successfully deleted (or none existed)
  - 400 Bad Request — Invalid or missing restaurant ID parameter
  - 404 Not Found — Restaurant ID does not exist (optional)
  - 500 Internal Server Error — Database or server error

---

## 📊 Data Used or Modified

### GET Products Input
- `restaurant` (Long) — Restaurant ID to fetch products for
- **Validations:**
  - `restaurant` must be a positive integer > 0
  - Restaurant should exist in restaurants table

### GET Products Output (List of ApiProductDTO)
- `id` (Long) — Product ID
- `name` (String) — Product name
- `description` (String) — Product description
- `price` (BigDecimal) — Product price
- `restaurantId` (Long) — Associated restaurant ID
- `category` (String) — Product category (optional)
- `createdAt` (LocalDateTime) — When product was created
- `updatedAt` (LocalDateTime) — When product was last updated

### DELETE Products Input
- `restaurant` (Long) — Restaurant ID whose products to delete
- **Validations:**
  - `restaurant` must be a positive integer > 0
  - Restaurant must exist in restaurants table

### Data Modified
- **product table:** All rows with matching `restaurant_id` are deleted
- **product_order table:** All rows where `product_id` matches a deleted product are also deleted (cascade)
- Restaurant record itself remains intact (restaurant is deleted separately)

### Expected Behavior
- GET with no products → return 200 with empty list
- GET with products → return 200 with list of products
- DELETE with no products → return 200 (nothing to delete)
- DELETE with products → delete all products and related product_orders, return 200

---

## 🔒 Tech Constraints (Feature-Level)

- **SQL Only:** Use native SQL queries with parameterized bindings (no Hibernate `.findAll()` or `.deleteAll()`)
- **Parameterized Binding:** Use `?1` notation or named parameters `@Param("restaurantId")`
- **Cascade Delete Complexity:** When deleting products:
  1. First delete all product_order entries: `DELETE FROM product_order WHERE product_id IN (SELECT id FROM product WHERE restaurant_id = ?1)`
  2. Then delete all products: `DELETE FROM product WHERE restaurant_id = ?1`
  3. Or use SQL with JOIN in single DELETE statement if database supports it
- **No Hibernate Methods:** Do not use `.delete()`, `.deleteAll()`, or repository chaining
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Response Builder:** All API responses must use ResponseBuilder utility
- **Query Parameter Validation:** Validate `restaurant` ID in controller or service
- **Service Pattern:** Controller → Service → Repository (business logic in service layer)

---

## ✅ Acceptance Criteria

- [ ] ProductRepository contains parameterized SQL SELECT query for products by restaurant ID
- [ ] ProductRepository contains parameterized SQL DELETE query for products by restaurant ID
- [ ] All queries use parameter bindings (no string concatenation)
- [ ] ProductService.getProductsByRestaurant(restaurantId) method implemented
- [ ] ProductService.deleteByRestaurant(restaurantId) method implemented
- [ ] Delete method handles cascade deletion of product_order entries
- [ ] Controller GET /api/products endpoint with restaurant query parameter
- [ ] Controller DELETE /api/products endpoint with restaurant query parameter
- [ ] GET request with valid restaurant ID returns matching products
- [ ] GET request with restaurant ID that has no products returns empty list with 200 status
- [ ] GET request with invalid restaurant ID format returns 400 status
- [ ] GET request with missing restaurant parameter returns 400 status
- [ ] DELETE request with valid restaurant ID deletes all products and related product_orders
- [ ] DELETE request with restaurant ID that has no products returns 200 status
- [ ] DELETE request with invalid restaurant ID format returns 400 status
- [ ] DELETE request with missing restaurant parameter returns 400 status
- [ ] Response uses ResponseBuilder for consistent format
- [ ] Products are successfully persisted and deleted from database
- [ ] Product_order cascade deletion works correctly
- [ ] Unit tests pass for service layer methods
- [ ] Integration tests pass for controller endpoints
- [ ] TDD workflow enforced (tests written before implementation)

---

## 📝 Notes for the AI

- **Native SQL is critical** — Use `@Query` annotations, do not use Hibernate `.findByRestaurantId()` or `.deleteByRestaurantId()` convenience methods
- **SELECT parameter binding example:** `@Query("SELECT * FROM product WHERE restaurant_id = ?1")`
- **DELETE needs cascade handling:** The delete operation is complex because products can have related product_order entries. Consider two approaches:
  1. **Two-step delete:** Delete product_orders first (WHERE product_id IN (SELECT...)), then delete products
  2. **Single subquery delete:** DELETE FROM product WHERE restaurant_id = ?1 AND id NOT IN (SELECT product_id FROM product_order) (only if constraint allows)
  3. Most reliable is the two-step approach handled in the service layer
- **Empty results are not errors:** If no products exist for a restaurant, return 200 OK with empty list
- **Service layer responsibility:** The service should handle both the complex cascade delete logic and parameter validation
- **Reusable methods:** Both these methods may be called as part of larger delete workflows (cascade when deleting a restaurant)
- **Write tests first (TDD):** Test scenarios include:
  1. GET products for restaurant with products
  2. GET products for restaurant with no products
  3. DELETE products for restaurant with products (verify cascade)
  4. DELETE products for restaurant with no products
  5. Invalid restaurant ID for both GET and DELETE
- **Integration point:** These endpoints are critical for the cascade deletion workflow when a restaurant is deleted
