# 🤖 AI_FEATURE_Orders API

## 🎯 Feature Identity

- **Feature Name:** Orders API - Order Management REST Endpoints
- **Related Area:** Backend / API / Controller

---

## 🎪 Feature Goal

Provide a complete REST API interface for order management that allows mobile apps and clients to retrieve existing orders, create new orders, and update order status. The API must return properly formatted order data with associated products, handle complex order creation with multiple products, support status transitions (PENDING → ACCEPTED → IN_DELIVERY → DELIVERED), and maintain consistency across all endpoints using the ResponseBuilder utility.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- GET /api/orders endpoint to retrieve all orders or filtered orders
- POST /api/orders endpoint to create a new order with multiple products
- POST /api/order/{order_id}/status endpoint to update an order's status
- Request validation for order data (customer ID, restaurant ID, products, status)
- Response consistency using ResponseBuilder utility
- Proper HTTP status codes (200, 201, 400, 404, 500)
- Error handling for invalid queries, non-existent orders, validation errors, and database errors
- Order creation with product association (junction table management)
- Status transition validation (only allowed transitions)
- OrderApiController implementation
- Integration with OrderService and OrderRepository

### ❌ Out of Scope (Excluded)

- Modifying Order or ProductOrder entity models
- Complex filtering by date range, price range, or delivery time
- Pagination (simple queries only)
- Order cancellation logic (only status updates)
- Refund or payment processing
- Automated courier assignment
- Real-time order tracking WebSocket
- Batch order operations
- Order history or archive
- Authentication/authorization (assume pre-authenticated)

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **GET Orders:** Implement endpoint to retrieve orders (all, or filtered by query parameters)
- **CREATE Order:** Implement endpoint to accept order data with multiple products, validate, and persist
- **UPDATE Order Status:** Implement endpoint to change order status with validation of allowed transitions
- **Order Products:** Handle creating associative records in product_order junction table during order creation
- **Request Validation:** Validate order data (customer ID, restaurant ID, total price, products array)
- **Service Logic:** Implement order creation workflow with product association
- **Status Validation:** Verify only allowed status transitions
- **Response Formatting:** Use ResponseBuilder for all order responses
- **Error Handling:** Return appropriate errors for validation failures and edge cases
- **Status Codes:** Return correct HTTP codes (200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Error)

---

## 👥 User Flow / Logic (High Level)

### CREATE Order Flow
1. Mobile app displays checkout screen with products selected
2. User confirms order, app sends POST /api/orders
3. Request body includes:
   - `customerId` (Long) — Authenticated customer ID
   - `restaurantId` (Long) — Restaurant ordering from
   - `products` (Array) — Products with quantities
   - `totalPrice` (BigDecimal) — Sum of products
4. Controller receives ApiOrderRequestDTO
5. Controller validates all required fields present
6. Controller delegates to OrderService.createOrder(dto)
7. Service validates:
   - Customer exists
   - Restaurant exists
   - All products belong to chosen restaurant
   - Products exist and prices are valid
   - Total price matches sum of product prices
8. Service creates Order entity with status = PENDING
9. Service saves order using OrderRepository (native SQL INSERT)
10. Service creates ProductOrder junction records for each product
11. Service returns created order with ID to controller
12. Controller returns HTTP 201 Created with new order data and ID
13. Response includes order ID, products, total price, status, timestamps

### GET Orders Flow
1. Mobile app requests GET /api/orders (or with filter parameters)
2. Controller receives request (optional: customer_id, restaurant_id query params)
3. Controller validates any filter parameters if provided
4. Controller delegates to OrderService.getOrders(filters) or plain getAll()
5. Service calls OrderRepository to fetch orders
6. Repository executes SQL SELECT query
7. Database returns orders (with associated products)
8. Service returns orders to controller
9. Controller constructs response using ResponseBuilder
10. Controller returns HTTP 200 with orders array (may be empty)
11. Each order includes: id, customerId, restaurantId, products, status, totalPrice, timestamps

### UPDATE Order Status Flow
1. Courier app updates delivery status for order 42
2. App sends POST /api/order/42/status with new status
3. Request body includes:
   - `status` (String) — New status (ACCEPTED, IN_DELIVERY, DELIVERED, CANCELED)
4. Controller receives order ID and new status
5. Controller validates order ID is numeric
6. Controller delegates to OrderService.updateOrderStatus(orderId, newStatus)
7. Service fetches current order
8. Service validates status transition is allowed:
   - PENDING → ACCEPTED, CANCELED
   - ACCEPTED → IN_DELIVERY, CANCELED
   - IN_DELIVERY → DELIVERED, CANCELED
   - DELIVERED → no transitions (final state)
9. Service updates order status and timestamp
10. Service saves updated order using OrderRepository (native SQL UPDATE)
11. Service returns updated order to controller
12. Controller returns HTTP 200 with updated order
13. If transition not allowed → return 400 Bad Request
14. If order not found → return 404 Not Found

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### GET /api/orders (List Orders)
- **Request Parameters:** (Optional)
  - `customer` (Long, optional) — Filter by customer ID
  - `restaurant` (Long, optional) — Filter by restaurant ID
  - `status` (String, optional) — Filter by order status
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (List<ApiOrderDTO>) — Array of order objects
- **Response Fields per Order:**
  - `id` (Long) — Order ID
  - `customerId` (Long) — Customer who placed order
  - `restaurantId` (Long) — Restaurant fulfilling order
  - `courierId` (Long, nullable) — Courier assigned (if any)
  - `totalPrice` (BigDecimal) — Total order amount
  - `status` (String) — Current status (PENDING, ACCEPTED, IN_DELIVERY, DELIVERED, CANCELED)
  - `products` (List<ApiProductForOrderApiDTO>) — Products in order
  - `createdAt` (LocalDateTime) — Order creation time
  - `updatedAt` (LocalDateTime) — Last update time
- **HTTP Status Codes:**
  - 200 OK — Orders retrieved successfully
  - 400 Bad Request — Invalid filter parameters
  - 500 Internal Server Error — Database error

#### POST /api/orders (Create Order)
- **Request Body:** ApiOrderRequestDTO
  - `customerId` (Long, required) — Customer placing order
  - `restaurantId` (Long, required) — Restaurant for order
  - `products` (Array, required) — Products with quantities
    - `productId` (Long, required) — Product ID
    - `quantity` (Integer, required) — Quantity ordered (minimum 1)
  - `totalPrice` (BigDecimal, required) — Total amount
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 201
  - `message` (String) — Success message
  - `data` (ApiOrderDTO) — Newly created order
- **Response includes:**
  - Order ID (auto-generated)
  - Status = PENDING
  - All products with quantities
  - Timestamps (created/updated)
- **HTTP Status Codes:**
  - 201 Created — Order successfully created
  - 400 Bad Request — Validation failed (missing fields, invalid data)
  - 404 Not Found — Customer, restaurant, or product not found
  - 500 Internal Server Error — Database error

#### POST /api/order/{id}/status (Update Status)
- **Path Parameter:**
  - `id` (Long, required) — Order ID to update
- **Request Body:** ApiOrderStatusDTO
  - `status` (String, required) — New status enum (ACCEPTED, IN_DELIVERY, DELIVERED, CANCELED)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiOrderDTO) — Updated order with new status
- **HTTP Status Codes:**
  - 200 OK — Status successfully updated
  - 400 Bad Request — Invalid status or invalid transition
  - 404 Not Found — Order does not exist
  - 500 Internal Server Error — Database error

---

## 📊 Data Used or Modified

### CREATE Order Request (ApiOrderRequestDTO)
- `customerId` (Long) — Customer placing order
  - Validation: @NotNull, must exist in database
- `restaurantId` (Long) — Restaurant for order
  - Validation: @NotNull, must exist in database
- `products` (List<OrderProductRequest>) — Products to include
  - `productId` (Long) — Product ID
    - Validation: @NotNull
  - `quantity` (Integer) — Quantity
    - Validation: @NotNull, @Min(1)
- `totalPrice` (BigDecimal) — Total order amount
  - Validation: @NotNull, @DecimalMin("0.01")

### CREATE Order Response / GET Order (ApiOrderDTO)
- `id` (Long) — Auto-generated order ID
- `customerId` (Long) — Customer reference
- `restaurantId` (Long) — Restaurant reference
- `courierId` (Long) — Courier reference (nullable, assigned later)
- `status` (String) — Order status enum:
  - PENDING (initial state)
  - ACCEPTED (restaurant accepted)
  - IN_DELIVERY (courier assigned, delivering)
  - DELIVERED (completed)
  - CANCELED (customer or restaurant canceled)
- `totalPrice` (BigDecimal) — Total amount with 2 decimal places
- `products` (List<ApiProductForOrderApiDTO>) — Products in order:
  - `productId` (Long)
  - `productName` (String)
  - `price` (BigDecimal)
  - `quantity` (Integer)
  - `subtotal` (BigDecimal) — price × quantity
- `createdAt` (LocalDateTime) — ISO 8601 timestamp
- `updatedAt` (LocalDateTime) — ISO 8601 timestamp

### UPDATE Order Status Request (ApiOrderStatusDTO)
- `status` (String) — New status
  - Validation: @NotNull, must be valid enum value

### UPDATE Order Status Response
- Same as ApiOrderDTO with updated status and updatedAt timestamp

### Data Constraints
- `customerId`: Must be valid customer ID
- `restaurantId`: Must be valid restaurant ID
- `productId`: Must be valid product ID and belong to restaurant
- `quantity`: Minimum 1, typically maximum 999 per product
- `totalPrice`: Must match sum of (product.price × quantity) for each product
- `status`: Only specific enum values allowed

### Status Transition Rules
```
PENDING    → {ACCEPTED, CANCELED}
ACCEPTED   → {IN_DELIVERY, CANCELED}
IN_DELIVERY → {DELIVERED, CANCELED} (delivery only)
DELIVERED  → {} (terminal state, no transitions)
CANCELED   → {} (terminal state, no transitions)
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Controller Pattern:** Thin controllers that parse requests and delegate to services
- **Service Pattern:** Service layer contains all business logic (validation, status transitions, product association)
- **Response Format:** ALL responses must use ResponseBuilder utility
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Validation:** Use Jakarta validation annotations (@NotNull, @Min, @DecimalMin, @Size)
- **REST Principles:** Follow REST conventions for HTTP methods and status codes
- **DTO Usage:** Use proper DTOs for request/response (ApiOrderRequestDTO, ApiOrderDTO, ApiOrderStatusDTO)
- **Service Calls:** Controllers only call service methods, never directly call repositories
- **Order Creation:** Service must handle creating both Order and ProductOrder records (junction table)
- **Status Validation:** Service must enforce allowed status transitions
- **SQL Integration:** Service uses OrderRepository with native parameterized SQL queries
- **Cascade Delete:** When order is deleted, related ProductOrder records must also be deleted
- **Price Verification:** Service should validate totalPrice matches sum of products × quantities

---

## ✅ Acceptance Criteria

### GET Orders Tests
- [ ] GET /api/orders endpoint exists
- [ ] GET request returns HTTP 200
- [ ] Response includes statusCode, message, data fields
- [ ] Response data is array (even if empty)
- [ ] Each order includes: id, customerId, restaurantId, status, totalPrice, products, timestamps
- [ ] Empty database returns 200 with empty array (not error)
- [ ] Optional filter parameters work (customer, restaurant, status)
- [ ] Filter by customer returns only that customer's orders
- [ ] Filter by restaurant returns only that restaurant's orders
- [ ] Filter by status returns only orders with that status
- [ ] Invalid filter parameter returns 400
- [ ] Response uses ResponseBuilder format
- [ ] Response Content-Type is application/json

### POST Create Order Tests
- [ ] POST /api/orders endpoint exists
- [ ] POST with valid data returns HTTP 201 Created
- [ ] Response includes newly created order with ID
- [ ] Created order has status = PENDING
- [ ] Created order persists to database
- [ ] ProductOrder junction records are created for products
- [ ] POST with missing customerId returns 400
- [ ] POST with missing restaurantId returns 400
- [ ] POST with missing products array returns 400
- [ ] POST with empty products array returns 400
- [ ] POST with product quantity < 1 returns 400
- [ ] POST with missing totalPrice returns 400
- [ ] POST with totalPrice that doesn't match products returns 400
- [ ] POST with non-existent customerId returns 404
- [ ] POST with non-existent restaurantId returns 404
- [ ] POST with product not from restaurant returns 400
- [ ] Response includes all products with quantities
- [ ] Response includes createdAt timestamp
- [ ] Response uses ResponseBuilder format
- [ ] New order is queryable via GET /api/orders

### POST Update Status Tests
- [ ] POST /api/order/{id}/status endpoint exists
- [ ] POST with valid ID and valid transition returns 200
- [ ] Order status is updated in database
- [ ] Response includes updated order with new status
- [ ] PENDING → ACCEPTED transition works
- [ ] PENDING → CANCELED transition works
- [ ] ACCEPTED → IN_DELIVERY transition works
- [ ] ACCEPTED → CANCELED transition works
- [ ] IN_DELIVERY → DELIVERED transition works
- [ ] IN_DELIVERY → CANCELED transition works
- [ ] PENDING → IN_DELIVERY (invalid) returns 400
- [ ] DELIVERED → any status (invalid) returns 400
- [ ] CANCELED → any status (invalid) returns 400
- [ ] POST with non-existent order ID returns 404
- [ ] POST with invalid order ID format returns 400
- [ ] POST with invalid status value returns 400
- [ ] POST with missing status field returns 400
- [ ] Response includes new status
- [ ] Response includes updated timestamp
- [ ] Response uses ResponseBuilder format

### Integration Tests
- [ ] Create order then update status (full workflow)
- [ ] Create order then retrieve via GET
- [ ] Multiple products in single order are all created
- [ ] All endpoints use same response format (ResponseBuilder)
- [ ] Error messages are consistent across endpoints
- [ ] Controller does not directly access repository
- [ ] Service layer is called by controller
- [ ] Status transitions are enforced (invalid transitions rejected)
- [ ] ProductOrder records properly associate products with orders

---

## 📝 Notes for the AI

- **Complex Order Creation:** Creating an order involves:
  1. Validating all input data (IDs exist, totals match, statuses valid)
  2. Creating Order entity with status = PENDING
  3. Saving Order (getting generated ID)
  4. Creating ProductOrder records linking each product to the order
  5. This is a multi-step service operation with potential for partial failures
- **Price Validation:** The service should validate:
  - totalPrice matches SUM(product.price × quantity)
  - All products exist and have correct prices
  - This prevents clients from underpaying
- **Status Transition Machine:** Implement state validation:
  - Create mapping of valid transitions
  - Check transition before updating
  - Return 400 if transition invalid
  - Example: DELIVERED has no valid next states
- **ProductOrder Junction Table:** When creating an order:
  - Must create separate ProductOrder record for EACH product
  - Handle bulk insertion or loop-based insertion
  - Ensure all succeed before returning (atomic operation)
- **SQL Integration:** 
  - Order creation uses native SQL INSERT (from orders-sql.feature.md)
  - Product association uses ProductOrder operations (from product-orders-sql.feature.md)
  - Status update uses native SQL UPDATE
- **Service Layer Responsibilities:** Order service should handle:
  - Bulk validation of all input data
  - Status transition checking
  - Orchestrating multiple database operations
  - Returning consistent response objects
- **Optional Fields:** Some fields like courierId are nullable (courier assigned later)
- **Empty Results:** GET with no matching orders returns 200 with empty array (not error)
- **Write Tests First (TDD):** Test scenarios:
  1. Create simple order with 1 product
  2. Create order with multiple products
  3. Verify totalPrice matches
  4. Verify ProductOrder records created
  5. Status transitions (valid and invalid)
  6. Missing/invalid fields
  7. Non-existent references
- **Controller Thinness:** Controller should only:
  - Extract path/query parameters and request body
  - Delegate to service
  - Return response with ResponseBuilder
  - Not perform validation or business logic
- **Response Consistency:** All order responses should follow same format with same fields
- **Error Messages:** When validation fails, provide clear message indicating which field/reason
