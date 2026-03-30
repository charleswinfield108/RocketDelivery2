# 🤖 AI_FEATURE_Products SQL

## 🎯 Feature Identity

- **Feature Name:** Products SQL - Retrieval & Deletion by Restaurant
- **Related Area:** Backend / API / Database
- **Dependencies:** Restaurants SQL feature (products belong to restaurants)

---

## 🎪 Feature Goal

Enable retrieval of all products for a specific restaurant using native SQL queries, and allow deletion of all products linked to a given restaurant using native SQL DELETE. The API must provide efficient product discovery by restaurant while maintaining data integrity through parameterized queries and proper cascade handling when restaurants are deleted.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL SELECT query with parameterized bindings for retrieving all products by restaurant ID
- Native SQL DELETE query with parameterized bindings for deleting all products by restaurant ID (cascade cleanup)
- Query parameter parsing (`restaurant`: numeric restaurant ID value)
- Request validation (ensure valid numeric restaurant ID)
- Response object containing list of products or success/error message
- Proper error handling for invalid queries, missing restaurants, or database errors
- Service layer logic to delegate business logic from controller
- Integration with ProductRepository using native SQL
- Support for product metadata (name, description, price, category, image URL, availability)
- Cascade deletion when restaurant is deleted (removes all products of that restaurant)
- Sorting/ordering of results (by product ID, name, or price)
- Optional filtering by product availability status

### ❌ Out of Scope (Excluded)

- Modifying the Product entity model or creating new fields
- Authentication/authorization checks (assume user is already authenticated)
- Pagination of product results (return all or use simple limit)
- Search by product name or partial text matching
- Filtering by product category or price range
- Frontend UI or form validation
- Creating/updating individual products (only retrieval and bulk deletion by restaurant)
- Historical auditing or soft deletes
- Filtering by product visibility or rating

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL SELECT by Restaurant ID:** Write parameterized SELECT query in ProductRepository to fetch all products for a specific restaurant ID
- **SQL DELETE by Restaurant ID (Cascade):** Write parameterized DELETE query in ProductRepository to remove all products associated with a restaurant ID
- **Restaurant ID Parameter Parsing:** Parse `restaurant` ID from query string for both GET and DELETE operations
- **Service Logic:** Implement methods in ProductService to handle retrieval and deletion operations
- **Controller Endpoints:** Implement GET and DELETE methods in ProductsApiController with query parameter
- **Error Handling:** Handle invalid restaurant ID, missing restaurants, validation errors, and database errors
- **Response Format:** Use ResponseBuilder to construct consistent API responses for both operations
- **Data Validation:** Ensure restaurant ID is a positive integer; validate entity existence before returning results
- **Result Ordering:** Return products ordered by ID or name for consistent results

---

## 👥 User Flow / Logic (High Level)

### GET Products by Restaurant ID Flow
1. Client (restaurant owner, customer, delivery person) sends GET request to `/api/products?restaurant=5`
2. Controller receives query parameter and validates `restaurant` is a valid positive numeric value
3. Controller verifies the restaurant exists (returns 404 if not)
4. Controller delegates to ProductService.getProductsByRestaurant(restaurantId)
5. Service calls ProductRepository.findProductsByRestaurantId(restaurantId)
6. Repository executes parameterized SQL SELECT query on products table WHERE restaurant_id = ?
7. Database returns list of Product records with all product details
8. Service converts entities to ApiProductDTO objects
9. Service returns list to controller
10. Controller returns HTTP 200 with list of products in response body
11. If no products found → return empty list with 200 status (valid scenario - restaurant might have no products yet)
12. If invalid restaurant ID → return 400 Bad Request with error message
13. If restaurant doesn't exist → return 404 Not Found

### Retrieve Product Details
- Each product in response includes: id, name, description, price, category, image_url, availability_status
- Products ordered by id ascending for consistency
- Null/default values handled gracefully in DTO conversion

### DELETE Products by Restaurant ID Flow (Cascade Cleanup)
1. When a restaurant is being deleted via DELETE /api/restaurants/{id}, the system triggers cascade cleanup
2. RestaurantService calls ProductService.deleteProductsByRestaurant(restaurantId)
3. Service calls ProductRepository.deleteProductsByRestaurantId(restaurantId)
4. Repository executes parameterized SQL DELETE query WHERE restaurant_id = ?
5. All products associated with the restaurant are removed in a single atomic operation
6. Database returns number of rows deleted
7. Service returns deletion count to caller
8. Transaction ensures all-or-nothing semantics
9. If restaurant exists but has no products → return deletion count = 0 (not an error)
10. Cascade continues: ProductOrderService then cleans up product_orders referring to deleted products

---

## 🖥️ Interfaces (Endpoints & Components)

### API Endpoints

#### GET /api/products
- **Query Parameters:**
  - `restaurant` (required, Integer): Positive integer restaurant ID
- **Success Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": [
      {
        "id": 1,
        "restaurant_id": 5,
        "name": "Margherita Pizza",
        "description": "Classic pizza with tomato, mozzarella, and basil",
        "price": 12.99,
        "category": "Pizza",
        "image_url": "https://api.example.com/images/pizza-1.jpg",
        "availability_status": "AVAILABLE"
      },
      {
        "id": 2,
        "restaurant_id": 5,
        "name": "Caesar Salad",
        "description": "Fresh romaine lettuce with Caesar dressing and parmesan",
        "price": 8.99,
        "category": "Salad",
        "image_url": "https://api.example.com/images/salad-1.jpg",
        "availability_status": "AVAILABLE"
      },
      {
        "id": 3,
        "restaurant_id": 5,
        "name": "Tiramisu",
        "description": "Italian dessert with mascarpone and espresso",
        "price": 6.50,
        "category": "Dessert",
        "image_url": "https://api.example.com/images/tiramisu-1.jpg",
        "availability_status": "OUT_OF_STOCK"
      }
    ],
    "error": null
  }
  ```
- **Empty Result Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": [],
    "error": null
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Restaurant ID must be a valid integer greater than 0"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Restaurant with ID 999 not found"
  }
  ```

#### DELETE /api/products
- **Query Parameter:**
  - `restaurant` (required, Integer): Restaurant ID whose products should be deleted
- **Success Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": "Deleted 15 products for restaurant 5",
    "error": null
  }
  ```
- **Empty Deletion Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": "Deleted 0 products for restaurant 5",
    "error": null
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Restaurant ID must be a valid integer greater than 0"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Restaurant with ID 999 not found"
  }
  ```

### Database Queries (Native SQL)

#### SELECT All Products by Restaurant ID
```sql
SELECT p.id, p.restaurant_id, p.name, p.description, p.price, p.category, p.image_url, p.availability_status
FROM products p
WHERE p.restaurant_id = ?1
ORDER BY p.id ASC
```

#### DELETE All Products by Restaurant ID (Cascade - CRITICAL FOR DATA INTEGRITY)
```sql
DELETE FROM products WHERE restaurant_id = ?1
```

### Service Layer

**ProductService**
- `getProductsByRestaurant(int restaurantId) → List<ApiProductDTO>`
  - Validates restaurantId > 0
  - Checks if restaurant exists
  - Retrieves all products for the restaurant
  - Converts Product entities to ApiProductDTO objects
  - Returns list (empty list if no products)
  - Throws IllegalArgumentException for invalid IDs
  - Throws ResourceNotFoundException if restaurant doesn't exist

- `deleteProductsByRestaurant(int restaurantId) → int`
  - Internal service method called by RestaurantService during cascade delete
  - Validates id > 0
  - Checks if restaurant exists
  - Deletes all products for that restaurant
  - Returns count of deleted rows
  - Throws ResourceNotFoundException if restaurant not found
  - Must be @Transactional for atomic operation

### Repository Layer

**ProductRepository** (extends JpaRepository<Product, Integer>)
- `findProductsByRestaurantId(@Param("restaurantId") int restaurantId) → List<Product>`
  - Native SQL SELECT query with parameterized binding
  - Returns all products for a specific restaurant
  - Ordered by product ID ascending

- `deleteProductsByRestaurantId(@Param("restaurantId") int restaurantId) → int`
  - Native SQL DELETE query with parameterized binding
  - Deletes all products associated with a restaurant
  - Returns number of deleted rows
  - CRITICAL: Used in cascade deletion during restaurant deletion

### Controller Layer

**ProductsApiController**
- `GET /api/products` - Retrieve all products for a restaurant
  - Validates restaurant query parameter
  - Calls service
  - Returns 200/400/404 with ApiResponseDTO
  - Returns empty list if restaurant has no products (HTTP 200)

- `DELETE /api/products` (cascade) - Delete all products for a restaurant
  - Validates restaurant query parameter
  - Calls service
  - Returns 200/400/404 with ApiResponseDTO and deletion count
  - Called internally by RestaurantService before deleting restaurant

---

## 📊 Data & Validations

### Input Validation

| Field | Type | Constraints | Error Message |
|-------|------|-------------|---------------|
| `restaurant` (GET) | Integer | Required, must be > 0 | "Restaurant ID must be a valid integer greater than 0" |
| `restaurant` (DELETE) | Integer | Required, must be > 0 | "Restaurant ID must be a valid integer greater than 0" |

### Database Constraints

- `id` is primary key (auto-increment, positive integer)
- `restaurant_id` is foreign key referencing restaurants.id
- `name` is required non-null text (typically 1-200 characters)
- `description` is optional nullable text (up to 1000 chars)
- `price` must be non-negative decimal (>= 0.00)
- `category` is optional text field (Pizza, Salad, Dessert, Beverage, etc.)
- `image_url` is optional URL field
- `availability_status` enum field: AVAILABLE, OUT_OF_STOCK, DISCONTINUED

### Entity Relationships

```
Product
├── restaurant_id (FK) → Restaurant
├── product_orders via ProductOrder (one-to-many)
└── metadata (name, description, price, category, image_url, availability_status)
```

### Product Availability States

| Status | Meaning | Expected Behavior |
|--------|---------|------------------|
| AVAILABLE | Product can be ordered | Show in menu, allow ordering |
| OUT_OF_STOCK | Product temporarily unavailable | Show in menu but disable ordering |
| DISCONTINUED | Product no longer offered | Can remain in historical orders but not available for new orders |

---

## ✅ Acceptance Criteria

### Functional Requirements - GET Products

- [ ] **GET endpoint returns 200** when requesting with valid restaurant ID that exists
- [ ] **GET returns correct data structure** with `message: "Success"`, `data` array, `error: null`
- [ ] **GET returns empty array (200)** when restaurant exists but has no products (not an error)
- [ ] **GET returns 400** when restaurant parameter is missing
- [ ] **GET returns 400** when restaurant parameter is not a positive integer
- [ ] **GET returns 404** when restaurant ID doesn't exist
- [ ] **GET returns correct product fields** (id, restaurant_id, name, description, price, category, image_url, availability_status)
- [ ] **GET products are ordered** by ID in ascending order
- [ ] **GET handles null/empty fields** gracefully in responses (nullable description, etc.)
- [ ] **GET response includes all product metadata** for each product

### Functional Requirements - DELETE Products

- [ ] **DELETE endpoint returns 200** when deleting products for valid restaurant
- [ ] **DELETE returns 400** when restaurant param is missing or invalid
- [ ] **DELETE returns 400** when restaurant is not a positive integer
- [ ] **DELETE returns 404** when restaurant doesn't exist
- [ ] **DELETE successfully removes all products** for a restaurant (verified by re-query returns empty list)
- [ ] **DELETE with empty restaurant (0 products) returns 200** with deletion count = 0
- [ ] **DELETE with N products returns 200** with deletion count = N
- [ ] **DELETE response includes deletion count** in message or data field
- [ ] **DELETE operation is atomic** (all products deleted or none deleted, no partial state)
- [ ] **Cascade delete works correctly** when RestaurantService calls deleteProductsByRestaurant()

### Non-Functional Requirements

- [ ] **All queries use parameterized bindings** (no string concatenation, prevent SQL injection)
- [ ] **Service validates restaurant existence** before returning errors (not database errors)
- [ ] **Cascade delete is atomic** (all-or-nothing transaction)
- [ ] **Logging includes DEBUG for requests** and INFO for successful operations
- [ ] **Test coverage includes 25+ test cases** covering:
  - Valid GET queries with products
  - Valid GET queries with empty product list
  - GET with invalid/missing restaurant parameter
  - GET with non-existent restaurant
  - Successful DELETE operations
  - DELETE verification (re-query confirms deletion)
  - DELETE with 0 and N products
  - Cascade delete verification (when restaurant deleted)
  - Invalid parameter formats
  - Edge cases (negative IDs, zero IDs, invalid formats)
  - Response format validation
  - Empty result scenarios

### Code Quality

- [ ] **All public methods have JavaDoc** with @param, @return, @throws
- [ ] **DRY principle applied** - extract validation/conversion helpers
- [ ] **Consistent error response format** across all endpoints
- [ ] **Proper exception handling** with meaningful error messages
- [ ] **No hardcoded strings** - use constants/enums where appropriate
- [ ] **Native SQL queries only** - no JPA method chaining for deletes
- [ ] **Service layer handles validation** before repo calls

### Test Verification

- [ ] **All 25+ tests passing** in ProductsApiControllerTest
- [ ] **Integration tests** verify actual database operations
- [ ] **Error paths tested** with specific assertions
- [ ] **Tests follow AAA pattern** (Arrange, Act, Assert)
- [ ] **Cascade delete tested** in RestaurantApiControllerTest extension
- [ ] **Database state verified after operations** (re-query to confirm changes)

---

## 🚀 Implementation Plan

### Red Phase (TDD)
1. Create ProductsApiControllerTest.java with 25+ test cases
2. Write comprehensive test scenarios (GET with/without products, GET validation, DELETE variants, cascade)
3. Run tests → expect all failures (0 passing)

### Green Phase
1. Create ProductsApiController.java with endpoint implementations
2. Implement ProductService.java with business logic
3. Update ProductRepository.java with native SQL queries
4. Add public endpoints to SecurityConfig.java (permitAll for GET, restrict DELETE if needed)
5. Update ResponseBuilder.java if needed
6. Run tests → achieve 100% pass rate (25+/25+)

### Refactor Phase
1. Extract validation helper methods (parseAndValidateRestaurantId, validateRestaurantExists)
2. Extract conversion helpers (entityToDTO for Product objects)
3. Add DEBUG/INFO logging throughout
4. Add comprehensive JavaDoc on all public methods
5. Verify all tests still passing
6. Review error messages for consistency and clarity

### Validation Phase
1. Manual testing with curl/Postman for all endpoints
2. Verify cascade delete behavior in full restaurant deletion flow
3. Check error responses match specification exactly
4. Confirm response format consistency across endpoints
5. Test with restaurants having 0, 1, and many products
6. Verify product ordering is consistent

---

## 📝 Critical Notes

- **NATIVE SQL ONLY:** All DELETE/SELECT queries MUST use @Query with native SQL, NOT JPA methods like `.deleteAll()`
- **PARAMETERIZED BINDINGS:** All queries MUST use ?1, ?2, etc. to prevent SQL injection
- **EMPTY = SUCCESS:** Empty product list for a restaurant is valid (200 response, not 404)
- **CASCADE SAFETY:** The deleteProductsByRestaurantId() method is CRITICAL - must be atomic and called BEFORE restaurant deletion
- **Error Precedence:** 400 validation errors checked before 404 not found errors
- **Cascade Integration:** RestaurantService.deleteRestaurant() must call ProductService.deleteProductsByRestaurant() FIRST, then delete restaurant, then cascade to ProductOrderService
- **Transaction Safety:** Delete operations must be @Transactional to ensure consistency
- **Ordering Matters:** Always order results by ID for predictable, testable results
- **Response Consistency:** Both GET and DELETE use ResponseBuilder with same error/success structure

---

## 🔗 Related Features

- **Restaurants SQL** - Parent feature; products belong to restaurants
- **Product Orders SQL** - Dependent feature; contains products from multiple restaurants
- **Addresses SQL** - Related resource (restaurant addresses)

---

**Last Updated:** 2026-03-30  
**Status:** Ready for Implementation  
**Priority:** High (immediately after OrdersSQL and before ProductOrdersSQL)  
**Estimated Effort:** 2-3 hours (TDD cycle)
