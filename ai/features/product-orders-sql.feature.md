# 🤖 AI_FEATURE_Product Orders SQL

## 🎯 Feature Identity

- **Feature Name:** Product Orders SQL - Retrieval & Deletion with Native Queries
- **Related Area:** Backend / API / Database
- **Dependencies:** Orders SQL feature (products are items within orders)

---

## 🎪 Feature Goal

Enable retrieval of product orders (items in orders) filtered by order ID or product ID using native SQL queries, and allow deletion of individual product order entries and batch deletion by order ID using native SQL DELETE. The API must provide granular control over product order management while maintaining data integrity through parameterized queries and proper cascade handling.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL SELECT query with parameterized bindings for retrieving all product orders by order ID
- Native SQL SELECT query with parameterized bindings for retrieving all product orders by product ID
- Native SQL DELETE query with parameterized bindings for deleting a specific product order by ID
- Native SQL DELETE query with parameterized bindings for batch deleting all product orders by order ID (cascade cleanup)
- Query parameter parsing (`filter_type`: order_id or product_id; `id`: numeric value)
- Query parameter parsing for batch delete (`order`: order ID)
- Request validation (ensure valid filter type and numeric IDs)
- Response object containing list of product orders or success/error message
- Proper error handling for invalid queries, missing records, or database errors
- Service layer logic to delegate business logic from controller
- Integration with ProductOrderRepository using native SQL
- Support for product order metadata (quantity, price, special instructions/notes)
- Cascade deletion when order is deleted (removes orphaned product_order records)

### ❌ Out of Scope (Excluded)

- Modifying the ProductOrder entity model or creating new fields
- Authentication/authorization checks (assume user is already authenticated)
- Soft delete or archiving of product orders
- Complex filtering by date range, price category, or status
- Frontend UI or form validation
- Batch update of product order quantities or prices
- Creating new product orders (handled by order creation flow)
- Historical auditing or change tracking

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL SELECT by Order ID:** Write parameterized SELECT query in ProductOrderRepository to fetch all product_orders for a specific order ID
- **SQL SELECT by Product ID:** Write parameterized SELECT query in ProductOrderRepository to fetch all product_orders containing a specific product ID
- **SQL DELETE by Product Order ID:** Write parameterized DELETE query in ProductOrderRepository to remove a specific product order entry
- **SQL DELETE by Order ID (Cascade):** Write parameterized DELETE query in ProductOrderRepository to bulk delete all product_orders associated with an order ID
- **Filter Parameter Parsing:** Parse `filter_type` (order_id/product_id) and `id` from request to determine which query to execute
- **Batch Delete Parameter Parsing:** Parse `order` ID from query string for cascade deletion
- **Service Logic:** Implement methods in ProductOrderService to handle retrieval and deletion operations
- **Controller Endpoints:** Implement GET method with query parameters and DELETE methods in controller  
- **Error Handling:** Handle invalid filter type, missing records, validation errors, and database errors
- **Response Format:** Use ResponseBuilder to construct consistent API responses
- **Data Validation:** Ensure IDs are positive integers; validate entity existence before returning results

---

## 👥 User Flow / Logic (High Level)

### GET Product Orders by Order ID Flow
1. Client (restaurant owner, courier, customer) sends GET request to `/api/product_orders?filter_type=order_id&id=42`
2. Controller receives query parameters and validates `filter_type` is one of: order_id, product_id
3. Controller validates `id` is a valid positive numeric value
4. Controller verifies the order exists (returns 404 if not)
5. Controller delegates to ProductOrderService.getProductOrdersByFilter(filterType, id)
6. Service calls ProductOrderRepository.findProductOrdersByOrderId(id)
7. Repository executes parameterized SQL SELECT query on product_orders table WHERE order_id = ?
8. Database returns list of ProductOrder records with product details
9. Service converts entities to ApiProductOrderDTO objects
10. Service returns list to controller
11. Controller returns HTTP 200 with list of product orders in response body
12. If no product orders found → return empty list with 200 status (valid scenario - order might be empty)
13. If invalid filter_type → return 400 Bad Request with error message
14. If order ID not found → return 404 Not Found

### GET Product Orders by Product ID Flow
1. Client sends GET request to `/api/product_orders?filter_type=product_id&id=7`
2. Controller receives query parameters and validates `filter_type`
3. Controller validates `id` is a valid positive numeric value
4. Controller verifies the product exists (returns 404 if not)
5. Controller delegates to ProductOrderService.getProductOrdersByFilter(filterType, id)
6. Service calls ProductOrderRepository.findProductOrdersByProductId(id)
7. Repository executes parameterized SQL SELECT query WHERE product_id = ?
8. Database returns list of ProductOrder records with related order info
9. Service converts entities to ApiProductOrderDTO objects
10. Controller returns HTTP 200 with list of product orders

### DELETE Product Order by ID Flow
1. Client sends DELETE request to `/api/product_orders/{id}` with specific product_order_id
2. Controller receives and validates `id` is valid positive numeric value
3. Controller verifies the product order exists (returns 404 if not)
4. Controller delegates to ProductOrderService.deleteProductOrder(id)
5. Service calls ProductOrderRepository.deleteById(id)
6. Repository executes parameterized SQL DELETE query WHERE id = ?
7. Database removes the product order entry
8. Service logs successful deletion
9. Controller returns HTTP 200 with success message
10. If ID not found → return 404 Not Found
11. If invalid format → return 400 Bad Request

### DELETE Product Orders by Order ID Flow (Cascade Cleanup)
1. Client sends DELETE request to `/api/product_orders?order={orderId}` OR OrderService internally calls method
2. When an order is being deleted, the OrderService calls ProductOrderService.deleteProductOrdersByOrderId(orderId)
3. Service calls ProductOrderRepository.deleteProductOrdersByOrderId(orderId)
4. Repository executes parameterized SQL DELETE query WHERE order_id = ?
5. All product orders associated with the order are removed in a single atomic operation
6. If order exists but has no product_orders → return 200 status, deletion count = 0 (not an error)
7. Transaction ensures all-or-nothing semantics
8. No response sent to client for internal operations; HTTP 200 for direct API calls

---

## 🖥️ Interfaces (Endpoints & Components)

### API Endpoints

#### GET /api/product_orders
- **Query Parameters:**
  - `filter_type` (required, String): "order_id" or "product_id" (case-insensitive)
  - `id` (required, Integer): Positive integer ID value
- **Success Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": [
      {
        "id": 101,
        "order_id": 42,
        "product_id": 7,
        "quantity": 2,
        "price": 9.99,
        "special_instructions": "Extra cheese"
      },
      {
        "id": 102,
        "order_id": 42,
        "product_id": 12,
        "quantity": 1,
        "price": 15.50,
        "special_instructions": null
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
    "error": "Invalid filter_type. Must be 'order_id' or 'product_id'"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Order with ID 999 not found"
  }
  ```

#### DELETE /api/product_orders/{id}
- **Path Parameter:**
  - `id` (required, Integer): Product order ID (positive integer)
- **Success Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": "Product order deleted successfully",
    "error": null
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "ID must be a valid integer greater than 0"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Product order with ID 999 not found"
  }
  ```

#### DELETE /api/product_orders (Batch by Order)
- **Query Parameter:**
  - `order` (required, Integer): Order ID whose product_orders should be deleted
- **Success Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": "Deleted 3 product orders for order 42",
    "error": null
  }
  ```
- **Empty Deletion Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": "Deleted 0 product orders for order 42",
    "error": null
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Order ID must be a valid integer greater than 0"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "message": null,
    "data": null,
    "error": "Order with ID 999 not found"
  }
  ```

### Database Queries (Native SQL)

#### SELECT Product Orders by Order ID
```sql
SELECT po.id, po.order_id, po.product_id, po.quantity, po.price, po.special_instructions
FROM product_orders po
WHERE po.order_id = ?1
ORDER BY po.id ASC
```

#### SELECT Product Orders by Product ID
```sql
SELECT po.id, po.order_id, po.product_id, po.quantity, po.price, po.special_instructions
FROM product_orders po
WHERE po.product_id = ?1
ORDER BY po.id ASC
```

#### DELETE Product Order by ID
```sql
DELETE FROM product_orders WHERE id = ?1
```

#### DELETE Product Orders by Order ID (Cascade - CRITICAL FOR DATA INTEGRITY)
```sql
DELETE FROM product_orders WHERE order_id = ?1
```

### Service Layer

**ProductOrderService**
- `getProductOrdersByFilter(String filterType, int id) → List<ApiProductOrderDTO>`
  - Validates filter_type (order_id/product_id)
  - Validates id > 0
  - Determines which repository method to call
  - Converts Order entities to DTO
  - Returns list (empty list if no results)
  - Throws IllegalArgumentException for invalid filter_type
  - Throws ResourceNotFoundException if entity doesn't exist

- `deleteProductOrder(int productOrderId) → void`
  - Validates id > 0
  - Checks if product order exists
  - Deletes the product order
  - Logs successful deletion
  - Throws ResourceNotFoundException if not found

- `deleteProductOrdersByOrderId(int orderId) → int`
  - Internal service method called by OrderService
  - Validates id > 0
  - Checks if order exists
  - Deletes all product_orders for that order
  - Returns count of deleted rows
  - Throws ResourceNotFoundException if order not found

### Repository Layer

**ProductOrderRepository** (extends JpaRepository<ProductOrder, Integer>)
- `findProductOrdersByOrderId(@Param("orderId") int orderId) → List<ProductOrder>`
  - Native SQL SELECT query
  - Parameterized binding for safety

- `findProductOrdersByProductId(@Param("productId") int productId) → List<ProductOrder>`
  - Native SQL SELECT query
  - Parameterized binding for safety

- `deleteById(Integer id) → void`
  - Inherited JPA method
  - Deletes single product order by ID

- `deleteProductOrdersByOrderId(@Param("orderId") int orderId) → int`
  - Native SQL DELETE query
  - Parameterized binding for safety
  - Returns number of deleted rows

### Controller Layer

**ProductOrdersApiController**
- `GET /api/product_orders` - Retrieve product orders by filter
  - Validates query parameters
  - Calls service
  - Returns 200/400/404 with ApiResponseDTO

- `DELETE /api/product_orders/{id}` - Delete a specific product order
  - Validates path parameter
  - Calls service
  - Returns 200/400/404 with ApiResponseDTO

- `DELETE /api/product_orders` (batch) - Delete all product orders for an order
  - Validates query parameter
  - Calls service
  - Returns 200/400/404 with ApiResponseDTO and deletion count

---

## 📊 Data & Validations

### Input Validation

| Field | Type | Constraints | Error Message |
|-------|------|-------------|---------------|
| `filter_type` | String | Required, must be "order_id" or "product_id" (case-insensitive) | "Invalid filter_type. Must be 'order_id' or 'product_id'" |
| `id` (GET query) | Integer | Required, must be > 0 | "ID must be a valid integer greater than 0" |
| `id` (DELETE path) | Integer | Required, must be > 0 | "ID must be a valid integer greater than 0" |
| `order` (DELETE query) | Integer | Required, must be > 0 | "Order ID must be a valid integer greater than 0" |

### Database Constraints

- `id` is primary key (auto-increment, positive integer)
- `order_id` is foreign key referencing orders.id
- `product_id` is foreign key referencing products.id
- `quantity` must be positive integer (> 0)
- `price` must be non-negative decimal (>= 0)
- `special_instructions` is optional nullable text field (up to 500 chars)

### Entity Relationships

```
ProductOrder
├── order_id (FK) → Order
├── product_id (FK) → Product
└── metadata (quantity, price, special_instructions)
```

---

## ✅ Acceptance Criteria

### Functional Requirements

- [ ] **GET endpoint returns 200** when requesting with valid filter_type and existing entity ID
- [ ] **GET returns correct data structure** with `message: "Success"`, `data` array, `error: null`
- [ ] **GET returns empty array (200)** when entity exists but has no product orders (not an error)
- [ ] **GET returns 400** when filter_type is invalid or missing
- [ ] **GET returns 400** when id parameter is missing or not positive integer
- [ ] **GET returns 404** when order_id doesn't exist
- [ ] **GET returns 404** when product_id doesn't exist
- [ ] **GET filter_type is case-insensitive** (ORDER_ID, order_id, Order_Id all work)
- [ ] **GET returns correct data fields** (id, order_id, product_id, quantity, price, special_instructions)
- [ ] **DELETE by ID returns 200** when deleting valid product order
- [ ] **DELETE by ID returns 400** when id is invalid format or not positive
- [ ] **DELETE by ID returns 404** when product order doesn't exist
- [ ] **DELETE by order_id returns 200** with deletion count (0 or more)
- [ ] **DELETE by order_id returns 400** when order param invalid or missing
- [ ] **DELETE by order_id returns 404** when order doesn't exist
- [ ] **DELETE cascades correctly** when order is deleted via OrderService
- [ ] **Response structure is consistent** across all endpoints

### Non-Functional Requirements

- [ ] **All queries use parameterized bindings** (no string concatenation, prevent SQL injection)
- [ ] **Service validates entity existence** before returning errors (not database errors)
- [ ] **Cascade delete is atomic** (all-or-nothing transaction)
- [ ] **Logging includes DEBUG for requests** and INFO for successful operations
- [ ] **Test coverage includes 30+ test cases** covering:
  - Valid GET queries for both filter types
  - Invalid/missing filter_type scenarios
  - Invalid/missing ID parameters
  - Non-existent entity references
  - Successful DELETE operations
  - DELETE verification (re-query confirms deletion)
  - Cascade delete verification
  - Case insensitivity testing
  - Response format validation
  - Edge cases (negative IDs, zero IDs, invalid formats)
  - Empty result scenarios (entity exists, no products)
  - Batch delete with 0 and N products

### Code Quality

- [ ] **All public methods have JavaDoc** with @param, @return, @throws
- [ ] **DRY principle applied** - extract validation/conversion helpers
- [ ] **Consistent error response format** across all endpoints
- [ ] **Proper exception handling** with meaningful error messages
- [ ] **No hardcoded strings** - use constants/enums where appropriate
- [ ] **Native SQL queries only** - no JPA method chaining for deletes

### Test Verification

- [ ] **All 30+ tests passing** in ProductOrdersApiControllerTest
- [ ] **Integration tests** verify actual database operations
- [ ] **Error paths tested** with specific assertions
- [ ] **Tests follow AAA pattern** (Arrange, Act, Assert)
- [ ] **Cascade delete tested** in OrdersApiControllerTest extension

---

## 🚀 Implementation Plan

### Red Phase (TDD)
1. Create ProductOrdersApiControllerTest.java with 30+ test cases
2. Write comprehensive test scenarios (GET filters, GET validation, DELETE variants, cascade)
3. Run tests → expect all failures (0 passing)

### Green Phase
1. Create ProductOrdersApiController.java with endpoint implementations
2. Implement ProductOrderService.java with business logic
3. Update ProductOrderRepository.java with native SQL queries
4. Add public endpoints to SecurityConfig.java (permitAll)
5. Update ResponseBuilder.java if needed
6. Run tests → achieve 100% pass rate (30+/30+)

### Refactor Phase
1. Extract validation helper methods (isValidFilterType, parseAndValidateId)
2. Extract conversion helpers (entityToDTO)
3. Add DEBUG/INFO logging throughout
4. Add comprehensive JavaDoc on all public methods
5. Verify all tests still passing

### Validation Phase
1. Manual testing with curl/Postman for all endpoints
2. Verify cascade delete behavior in full order deletion flow
3. Check error responses match specification exactly
4. Confirm response format consistency
5. Test with large numbers of product orders

---

## 📝 Critical Notes

- **NATIVE SQL ONLY:** All DELETE/SELECT queries MUST use @Query with native SQL, NOT JPA methods
- **PARAMETERIZED BINDINGS:** All queries MUST use ?1, ?2, etc. to prevent SQL injection
- **EMPTY = SUCCESS:** Empty product order list for an entity is valid (200 response, not 404)
- **CASCADE SAFETY:** The deleteProductOrdersByOrderId() method is CRITICAL - it must be atomic and reliable
- **Error Precedence:** 400 validation errors checked before 404 not found errors
- **Cascade Integration:** OrderService.deleteOrder() must call ProductOrderService.deleteProductOrdersByOrderId() FIRST, then delete the order
- **Transaction Safety:** All delete operations must be @Transactional to ensure consistency

---

## 🔗 Related Features

- **Orders SQL** - Parent feature; product orders are items within orders
- **Products SQL** - Will provide product details for display
- **Restaurants SQL** - Related resource filtering

---

**Last Updated:** 2026-03-30  
**Status:** Ready for Implementation  
**Priority:** High (immediately after OrdersSQL completion)  
**Estimated Effort:** 2-4 hours (TDD cycle)

