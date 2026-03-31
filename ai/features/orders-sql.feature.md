# 🤖 AI_FEATURE_Orders SQL

## 🎯 Feature Identity

- **Feature Name:** Orders SQL - Multi-Filter Retrieval & Deletion
- **Related Area:** Backend / API / Database

---

## 🎪 Feature Goal

Enable retrieval of orders filtered by restaurant, customer, or courier using native SQL queries, and allow deletion of orders using native SQL DELETE. The API must securely retrieve orders based on context (which type of entity is requesting) and support cascade deletion of associated data when an order is deleted.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL SELECT query with parameterized bindings for retrieving orders by restaurant ID
- Native SQL SELECT query with parameterized bindings for retrieving orders by customer ID
- Native SQL SELECT query with parameterized bindings for retrieving orders by courier ID
- Native SQL DELETE query with parameterized bindings for deleting an order by ID
- Query parameter parsing (`type` and `id`) to determine filter context
- Request validation (ensure valid type parameter and numeric ID)
- Response object containing list of orders or success/error message
- Proper error handling for invalid queries, missing records, or database errors
- Cascade deletion handling (remove related ProductOrder entries when order is deleted)
- Service layer logic to delegate business logic from controller
- Integration with OrderRepository using native SQL

### ❌ Out of Scope (Excluded)

- Modifying the Order entity model or ProductOrder model
- Authentication/authorization checks (assume user is already authenticated)
- Soft delete or archiving of orders
- Complex filtering by date range, price, or status (scope is type-based filtering only)
- Frontend UI or form validation
- Batch deletion of multiple orders
- Update or edit order endpoints

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL SELECT by Restaurant:** Write parameterized SELECT query in OrderRepository to fetch all orders for a specific restaurant ID
- **SQL SELECT by Customer:** Write parameterized SELECT query in OrderRepository to fetch all orders for a specific customer ID
- **SQL SELECT by Courier:** Write parameterized SELECT query in OrderRepository to fetch all orders for a specific courier ID
- **SQL DELETE Order:** Write parameterized DELETE query in OrderRepository to remove an order and its related ProductOrder entries (cascade delete)
- **Query Parameter Parsing:** Parse `type` (restaurant/customer/courier) and `id` from request to determine which query to execute
- **Service Logic:** Implement methods in OrderService to handle each query type and deletion
- **Controller Endpoint:** Implement GET method with query parameters and DELETE method in controller
- **Error Handling:** Handle invalid type parameter, missing records, validation errors, and database errors
- **Response Format:** Use ResponseBuilder to construct consistent API responses

---

## 👥 User Flow / Logic (High Level)

### GET Orders Flow
1. Mobile app sends GET request to `/api/orders?type=restaurant&id=5` (or customer or courier)
2. Controller receives query parameters and validates `type` is one of: restaurant, customer, courier
3. Controller validates `id` is a valid positive numeric value
4. Controller delegates to OrderService.getOrdersByType(type, id)
5. Service determines which method to call based on type:
   - If "restaurant" → OrderRepository.findOrdersByRestaurantId(id)
   - If "customer" → OrderRepository.findOrdersByCustomerId(id)
   - If "courier" → OrderRepository.findOrdersByCourierId(id)
6. Repository executes appropriate parameterized SQL SELECT query
7. Database returns list of orders
8. Service returns list of orders to controller
9. Controller returns HTTP 200 with list of orders in response body
10. If no orders found → return empty list with 200 status (not an error)
11. If invalid type → return 400 Bad Request
12. If ID not found → return 404 Not Found

### DELETE Order Flow
1. Mobile app sends DELETE request to `/api/order/{id}` with order ID
2. Controller receives and validates `id` is valid positive numeric value
3. Controller verifies order exists before deletion (fetch first)
4. Controller delegates to OrderService.deleteOrder(id)
5. Service calls OrderRepository.deleteOrderById(id)
6. Repository executes parameterized SQL DELETE query
7. DELETE statement cascades to remove all related ProductOrder entries
8. Database confirms deletion
9. Service returns confirmation to controller
10. Controller returns HTTP 204 (No Content) or 200 with success message
11. If order not found → return 404 Not Found
12. If database error → return 500 Internal Server Error

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### GET /api/orders (Multi-Filter)
- **Request Parameters:**
  - `type` (String, required) — One of: "restaurant", "customer", "courier"
  - `id` (Long, required) — ID of the entity (restaurant ID, customer ID, or courier ID)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (List<ApiOrderDTO>) — List of orders matching the filter
- **HTTP Status Codes:**
  - 200 OK — Orders retrieved successfully (may be empty list)
  - 400 Bad Request — Invalid type parameter or missing/invalid id
  - 404 Not Found — Entity ID not found in database
  - 500 Internal Server Error — Database or server error

#### DELETE /api/order/{id}
- **Path Parameter:**
  - `id` (Long, required) — Order ID to delete
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (null or confirmation object)
- **HTTP Status Codes:**
  - 200 OK — Order successfully deleted
  - 204 No Content — Order successfully deleted (no response body)
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Order ID does not exist
  - 500 Internal Server Error — Database or server error

---

## 📊 Data Used or Modified

### GET Orders Input Parameters
- `type` (String) — Filter type: "restaurant", "customer", or "courier"
- `id` (Long) — ID of restaurant, customer, or courier
- **Validations:**
  - `type` must be exactly one of: "restaurant", "customer", "courier" (case-insensitive recommended)
  - `id` must be a positive integer > 0
  - `id` must exist in the database for the given type

### GET Orders Output Data (List of ApiOrderDTO)
- `id` (Long) — Order ID
- `customerId` (Long) — Customer who placed the order
- `restaurantId` (Long) — Restaurant fulfilling the order
- `courierId` (Long) — Courier delivering the order (may be null if not assigned)
- `totalPrice` (BigDecimal) — Total order amount
- `status` (String) — Current order status (PENDING, ACCEPTED, IN_DELIVERY, DELIVERED, CANCELED)
- `createdAt` (LocalDateTime) — When order was created
- `updatedAt` (LocalDateTime) — When order was last updated
- `products` (List<ApiProductForOrderApiDTO>) — Products in the order

### DELETE Order Input
- `id` (Long) — Order ID to delete
- **Validations:**
  - `id` must be a positive integer > 0
  - `id` must exist in orders table
  - All related ProductOrder records must also be deleted (cascade)

### Data Modified
- **orders table:** Row with matching ID is deleted
- **product_order table:** All rows with matching order_id are deleted (cascade)
- Other tables remain unchanged (customer, restaurant, courier records are preserved)

---

## 🔒 Tech Constraints (Feature-Level)

- **SQL Only:** Use native SQL queries with parameterized bindings (no string concatenation, no JPA methods for these operations)
- **Parameterized Binding:** Use `?1, ?2, ?3` notation or named parameters `@Param("field")`
- **Cascade Delete:** When deleting an order, related ProductOrder entries must also be deleted (handle in SQL or service layer)
- **No Hibernate Save/Delete:** Do not use Hibernate's `.delete()` or `.save()` methods — use native SQL
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Response Builder:** All API responses must use ResponseBuilder utility
- **Query Parameter Validation:** Validate `type` and `id` in controller or service before querying database
- **Service Pattern:** Controller → Service → Repository (business logic in service layer)
- **Case Insensitivity:** Recommend handling type parameter case-insensitively (convert to lowercase)

---

## ✅ Acceptance Criteria

- [ ] OrderRepository contains parameterized SQL SELECT query for restaurant orders
- [ ] OrderRepository contains parameterized SQL SELECT query for customer orders
- [ ] OrderRepository contains parameterized SQL SELECT query for courier orders
- [ ] All SELECT queries use parameter bindings (no string concatenation)
- [ ] OrderRepository contains parameterized SQL DELETE query with cascade delete
- [ ] DELETE query properly removes related ProductOrder entries
- [ ] OrderService methods implemented for each query type
- [ ] OrderService.deleteOrder(id) method implemented
- [ ] Controller GET /api/orders endpoint with query parameter parsing
- [ ] Controller DELETE /api/order/{id} endpoint implemented
- [ ] GET request with valid restaurant type returns matching orders
- [ ] GET request with valid customer type returns matching orders
- [ ] GET request with valid courier type returns matching orders
- [ ] GET request with invalid type parameter returns 400 status
- [ ] GET request with non-existent ID returns 404 status
- [ ] GET request with missing id parameter returns 400 status
- [ ] DELETE request with valid order ID deletes order and related product orders
- [ ] DELETE request with non-existent ID returns 404 status
- [ ] DELETE request with invalid ID format returns 400 status
- [ ] Response uses ResponseBuilder for consistent format
- [ ] Orders are successfully persisted and deleted from database
- [ ] Unit tests pass for service layer methods
- [ ] Integration tests pass for controller endpoints
- [ ] TDD workflow enforced (tests written before implementation)

---

## 📝 Notes for the AI

- **Native SQL is critical** — do not use Hibernate `.findAll()`, `.findById()`, or `.delete()` methods. Use `@Query` for SELECT and DELETE.
- **Parameter bindings are essential** — example: `@Query("SELECT * FROM orders WHERE restaurant_id = ?1")` or `@Query("SELECT * FROM orders WHERE customer_id = :customerId")`
- **Case handling for type:** Consider converting the `type` parameter to lowercase in the controller to handle "Restaurant", "RESTAURANT", "restaurant" uniformly
- **Cascade delete complexity:** When deleting an order, ensure ProductOrder entries are also deleted. This can be done either:
  1. In the DELETE SQL statement with a subquery, or
  2. In the service layer by deleting ProductOrder records first, then deleting the Order
- **Service layer validation:** The service layer should contain the logic to map type → method call, so controller stays thin
- **Empty results are not errors:** If no orders are found, return 200 with an empty list, not 404
- **Write tests first (TDD):** Write test cases for all three filter types and the delete operation before implementing the SQL queries
- **Order existence check before delete:** Consider fetching the order first to verify it exists and return 404 if not found (prevents silent failures)
