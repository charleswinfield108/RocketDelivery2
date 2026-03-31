# 🤖 AI_FEATURE_Product Orders SQL

## 🎯 Feature Identity

- **Feature Name:** Product Orders SQL - Batch Deletion by Order
- **Related Area:** Backend / API / Database

---

## 🎪 Feature Goal

Enable deletion of all product_order associations for a specific order using a native SQL DELETE query. This is a critical cascade operation that removes all products linked to an order when the order itself is deleted, ensuring referential integrity and preventing orphaned product_order records.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL DELETE query with parameterized bindings for removing all product_order entries by order ID
- Query parameter parsing (order ID from query string)
- Request validation (ensure valid numeric order ID)
- Response object indicating success or failure
- Error handling for invalid queries, non-existent orders, or database errors
- Service layer logic to delegate deletion from controller
- Integration with ProductOrderRepository using native SQL
- Part of the cascade deletion workflow when an order is deleted

### ❌ Out of Scope (Excluded)

- Modifying the ProductOrder entity model
- Delete by product ID (only by order ID)
- Delete by restaurant or customer (only by order ID)
- Retrieving/listing product_order records
- Frontend UI or form validation
- Individual product_order deletion (only batch by order)
- Authentication/authorization checks for this operation

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL DELETE Query:** Write parameterized DELETE query in ProductOrderRepository to remove all product_order entries for a specific order ID
- **Query Parameter Parsing:** Parse `order` ID from query string parameter
- **Service Logic:** Implement method in ProductOrderService to handle batch deletion by order ID
- **Controller Endpoint:** Implement DELETE method in controller to receive order ID and delegate to service
- **Error Handling:** Handle invalid order ID, non-existent order, validation errors, and database errors
- **Response Format:** Use ResponseBuilder to construct consistent API response

---

## 👥 User Flow / Logic (High Level)

1. System initiates deletion of an order via DELETE /api/order/{orderId}
2. Order controller verifies the order exists
3. Before deleting the order, controller calls DELETE /api/product_orders?order={orderId} (or service handles this internally)
4. ProductOrder controller/service receives order ID and validates it
5. Service delegates to ProductOrderRepository.deleteByOrderId(orderId)
6. Repository executes parameterized SQL DELETE query: `DELETE FROM product_order WHERE order_id = ?1`
7. Database removes all product_order rows matching that order ID
8. Database returns number of rows deleted
9. Service returns confirmation to caller
10. Caller then proceeds to delete the order itself
11. Controller returns HTTP 200 with success message
12. If order ID not found in product_order table → return 200 (nothing to delete, not an error)
13. If database error → return 500 Internal Server Error

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### DELETE /api/product_orders (Batch by Order)
- **Request Parameter:**
  - `order` (Long, required) — Order ID whose product_order entries should be deleted
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (Object or null) — Optional deletion count or confirmation
- **HTTP Status Codes:**
  - 200 OK — product_order entries successfully deleted (or none existed for order)
  - 400 Bad Request — Invalid or missing order ID parameter
  - 500 Internal Server Error — Database or server error

---

## 📊 Data Used or Modified

### DELETE Product Orders Input
- `order` (Long) — Order ID whose product_order entries to delete
- **Validations:**
  - `order` must be a positive integer > 0
  - `order` must be a valid order ID (should exist in orders table)

### Data Modified
- **product_order table:** All rows with matching `order_id` are deleted
- No other tables are affected
- Referenced product and order records are preserved

### Expected Behavior
- If 0 product_order records exist for the order → return 200 OK (success, nothing to delete)
- If N product_order records exist → delete all N rows, return 200 OK
- Return count of deleted rows in response (optional but helpful)

---

## 🔒 Tech Constraints (Feature-Level)

- **SQL Only:** Use native SQL DELETE query with parameterized bindings (no JPA methods)
- **Parameterized Binding:** Use `?1` notation or named parameters `@Param("orderId")`
- **No Hibernate Delete:** Do not use Hibernate's `.deleteAll()` or similar methods — use native SQL
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Response Builder:** All API responses must use ResponseBuilder utility
- **Query Parameter Validation:** Validate `order` ID in controller or service before querying database
- **Service Pattern:** Controller → Service → Repository (business logic in service layer)
- **Batch Operation:** This query should delete ALL product_order entries for an order in a single operation

---

## ✅ Acceptance Criteria

- [ ] ProductOrderRepository contains parameterized SQL DELETE query for batch deletion by order ID
- [ ] SQL query uses parameter bindings (no string concatenation)
- [ ] ProductOrderService.deleteByOrderId(orderId) method implemented
- [ ] Controller DELETE /api/product_orders endpoint with order query parameter
- [ ] DELETE request with valid order ID deletes all related product_order entries
- [ ] DELETE request with order ID that has no product_orders returns 200 status
- [ ] DELETE request with invalid order ID format returns 400 status
- [ ] DELETE request with missing order parameter returns 400 status
- [ ] Response uses ResponseBuilder for consistent format
- [ ] Response includes count of deleted rows (optional)
- [ ] No errors occur when deleting large numbers of product_order entries
- [ ] Database remains consistent after deletion
- [ ] Unit tests pass for service layer
- [ ] Integration tests pass for controller endpoint
- [ ] TDD workflow enforced (tests written before implementation)

---

## 📝 Notes for the AI

- **Native SQL is critical** — Use `@Query` with DELETE statement, do not use Hibernate `.deleteAll()` or repository method chaining
- **Parameter binding example:** `@Query("DELETE FROM product_order WHERE order_id = ?1")`
- **Batch deletion:** This is a batch operation — all product_order records for an order are deleted in one SQL statement, not iteratively
- **No record found is not an error:** If the order exists but has no product_order entries, return 200 OK (this is expected behavior for orders with no items)
- **Service layer logic:** The service should handle parameter validation before calling the repository
- **Return value:** The repository can return the number of deleted rows (helpful for logging)
- **Cascade pattern:** This method is typically called as part of the order deletion cascade — structure it to be reusable
- **Write tests first (TDD):** Test both scenarios: order with products (deletion count > 0) and order without products (deletion count = 0)
- **Integration point:** This endpoint may be called internally by the order deletion service or externally by clients
