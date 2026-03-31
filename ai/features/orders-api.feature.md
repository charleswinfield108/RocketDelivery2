# 🤖🛠️ AI Feature Specification - Orders API

**Feature ID:** ORDERS-API  
**Priority:** High  
**Status:** In Development  
**Release Version:** Module 12  
**Last Updated:** March 31, 2026

---

## 📋 1. Feature Goal & Scope

### Feature Goal
Implement a comprehensive REST API for managing orders in the Rocket Food Delivery application. This API provides complete CRUD (Create, Read, Update, Delete) functionality for order management, enabling customers to place orders, retrieve order history, and track order information while enabling restaurants and couriers to manage order fulfillment.

### In-Scope
- ✅ **GET /api/orders** - List all orders with optional filtering by restaurant, customer, or courier
- ✅ **GET /api/orders/{id}** - Retrieve a specific order by ID
- ✅ **POST /api/orders** - Create a new order (requires authentication)
- ✅ **PUT /api/orders/{id}** - Update an existing order (e.g., status changes)
- ✅ **DELETE /api/orders/{id}** - Delete an order (requires authentication)
- ✅ **POST /api/order/{order_id}/status** - Update order status (simplified status endpoint)
- ✅ Request validation (required fields, format validation, business rule validation)
- ✅ Error handling (400, 401, 403, 404, 500 status codes)
- ✅ Response standardization (ApiResponseDTO wrapper for all endpoints)
- ✅ Data persistence (SQL database with JPA/Hibernate)
- ✅ Relationship handling (Products, Restaurants, Customers, Couriers, Order Status)
- ✅ Authorization checks (customer can view own orders, restaurant can view restaurant orders)
- ✅ Cascade deletion of related entities (ProductOrders)
- ✅ Order status transitions (validation of valid status changes)

### Out-of-Scope
- ❌ Payment processing
- ❌ Delivery address validation (address lookup/geocoding)
- ❌ Real-time order tracking with WebSockets
- ❌ Order recommendations/personalization
- ❌ Advanced analytics
- ❌ Bulk operations (cancel multiple orders)
- ❌ Order history retention policies
- ❌ Modifying Order entity model structure
- ❌ Complex filtering (distance, time range, price range filters)
- ❌ Courier assignment (assumes courier is assigned by admin)

## 🔄 2. Requirements Breakdown & User Flow

### Functional Requirements

#### FR1: List Orders
- User can fetch a list of all orders
- User can filter orders by type and ID:
  - `type=restaurant&id={id}` — Get all orders for a specific restaurant
  - `type=customer&id={id}` — Get all orders for a specific customer
  - `type=courier&id={id}` — Get all orders assigned to a specific courier
- User can fetch all orders without filters
- System validates filter parameters (valid type, numeric ID)
- Response includes pagination metadata (total count, page number, size, total pages)
- Response is sorted by order creation date in descending order (newest first)
- Empty results return HTTP 200 with empty array (not an error)

#### FR2: Get Order Details
- User can fetch detailed information about a specific order
- System validates order existence (returns 404 if not found)
- Response includes all order information and relationships:
  - Order ID, customer, restaurant, items, total price, status, timestamps
  - Associated ProductOrder details (products in the order)
- Order must be retrievable regardless of status

#### FR3: Create Order
- Authenticated customer can create a new order
- All required fields must be provided:
  - `customerId` (Long) — Customer placing the order
  - `restaurantId` (Long) — Restaurant fulfilling the order
  - `deliveryAddress` (String) — Where to deliver
  - `items` (List of ProductOrder) — Products being ordered with quantities
- System validates field formats and business rules:
  - Customer must exist
  - Restaurant must exist
  - At least one item must be included in order
  - All order items must belong to the specified restaurant
  - Quantity must be positive for each item
- New order is created with PENDING status initially
- Response includes the created order with generated ID
- Order total price calculated from product prices and quantities

#### FR4: Update Order Status
- Authenticated user can update an order's status
- User must be restaurant owner or system admin (authorization check)
- System validates status transition rules:
  - PENDING → CONFIRMED (acceptable)
  - PENDING → CANCELLED (acceptable)
  - CONFIRMED → READY (acceptable)
  - CONFIRMED → CANCELLED (acceptable)
  - READY → PICKED_UP (acceptable)
  - PICKED_UP → DELIVERED (acceptable)
  - DELIVERED → (no further changes)
  - CANCELLED → (no further changes)
- Response includes updated order with new status and timestamp

#### FR5: Update Order (General)
- Authenticated user can update order fields
- User must be the order customer or restaurant owner (authorization check)
- Partial updates allowed (not all fields required)
- System validates updated field formats and business rules
- Response includes updated order data
- Cannot update order ID or createdAt timestamp

#### FR6: Delete Order
- Authenticated user can delete an order
- User must be the order customer or restaurant owner (authorization check)
- System validates order existence before deletion
- Cascade delete related data (ProductOrders associated with this order)
- Response confirms successful deletion
- Deleted order cannot be retrieved
- System prevents deletion of delivered or already-deleted orders

### User Flows

#### Flow 1: Browse Orders (Public List)
```
1. Customer logs in
2. System calls GET /api/orders?type=customer&id={customerId}
3. API returns list of customer's orders with pagination
4. Customer views order history, sorted by date newest first
5. Customer selects an order from the list
6. Customer's browser shows GET /api/orders/{id}
7. API returns detailed order information
8. Customer views order status, items, total, and timestamps
```

#### Flow 2: Create Order (Authenticated Customer)
```
1. Customer logs in to mobile app
2. Customer browses restaurant menu
3. Customer adds items to cart (multiple products)
4. Customer clicks "Place Order"
5. Customer enters delivery address
6. Customer submits order with POST /api/orders
7. API validates:
   - Customer exists and is authenticated
   - Restaurant exists
   - All items belong to restaurant
   - Quantity for each item is positive
   - At least one item is included
8. API creates order with PENDING status
9. API calculates total price from items
10. API returns 201 Created with new order data including ID
11. Order is now visible in customer's order history
12. Restaurant receives notification of new order
```

#### Flow 3: Restaurant Updates Order Status (Authenticated)
```
1. Restaurant employee logs in to manager dashboard
2. Employee views pending orders for their restaurant
3. Employee clicks "Confirm Order" on PENDING order
4. System sends POST /api/order/{id}/status with status=CONFIRMED
5. API verifies employee is authorized (restaurant owner/manager)
6. API validates status transition (PENDING → CONFIRMED is valid)
7. API updates order status to CONFIRMED with current timestamp
8. API returns 200 OK with updated order
9. Customer receives notification of status change
10. Order now shows as CONFIRMED in customer's view
```

#### Flow 4: Track Order Status Progression
```
1. Customer views order details (GET /api/orders/{id})
2. Order shows status: PENDING
3. Restaurant confirms order → status becomes CONFIRMED
4. Restaurant prepares food → status becomes READY
5. Courier accepts delivery → status becomes PICKED_UP
6. Courier delivers → status becomes DELIVERED
7. Customer can view status at each step via GET /api/orders/{id}
```

#### Flow 5: Delete Order (Before Delivery)
```
1. Customer logs in and views order
2. Order status is PENDING or CONFIRMED
3. Customer clicks "Cancel Order"
4. System shows confirmation dialog
5. Customer confirms cancellation
6. Browser sends DELETE /api/orders/{id}
7. API verifies customer authorization
8. API checks if order can be deleted (not delivered/cancelled)
9. API performs cascade delete of ProductOrders
10. API returns 200 OK with success message
11. Order is removed from customer's order history
12. Restaurant receives cancellation notification
```

## 🖥️ 3. Interfaces Involved (Endpoints & DTOs)

#### GET /api/orders (List Orders)
- **Request Parameters:** (Optional)
  - `type` (String, optional) — Filter type: "restaurant", "customer", "courier"
  - `id` (Long, optional) — ID of the entity (restaurant ID, customer ID, or courier ID)
  - `page` (Integer, optional) — Page number for pagination (0-based)
  - `size` (Integer, optional) — Number of orders per page
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (List<ApiOrderDTO>) — Array of order objects
- **Response Fields per Order:**
  - `id` (Long) — Order ID
  - `customerId` (Long) — Customer ID
  - `restaurantId` (Long) — Restaurant ID
  - `courierId` (Long, nullable) — Courier ID (null if not assigned)
  - `deliveryAddress` (String) — Delivery address
  - `status` (String) — Current order status (PENDING, CONFIRMED, READY, PICKED_UP, DELIVERED, CANCELLED)
  - `totalPrice` (BigDecimal) — Total order price
  - `createdAt` (LocalDateTime) — Creation timestamp
  - `updatedAt` (LocalDateTime) — Last update timestamp
- **HTTP Status Codes:**
  - 200 OK — Orders retrieved successfully
  - 400 Bad Request — Invalid type parameter or ID format
  - 404 Not Found — No entity found with given type and ID
  - 500 Internal Server Error — Database error

#### GET /api/orders/{id} (Retrieve One Order)
- **Path Parameter:**
  - `id` (Long, required) — Order ID
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiOrderDTO) — Single order object with all fields
- **Response Fields:**
  - `id` (Long) — Order ID
  - `customerId` (Long) — Customer ID
  - `restaurantId` (Long) — Restaurant ID
  - `courierId` (Long, nullable) — Courier ID
  - `deliveryAddress` (String) — Delivery address
  - `status` (String) — Order status
  - `totalPrice` (BigDecimal) — Total price
  - `items` (List<ProductOrderDTO>) — Ordered items with product details and quantities
  - `createdAt` (LocalDateTime) — Creation timestamp
  - `updatedAt` (LocalDateTime) — Last update timestamp
- **HTTP Status Codes:**
  - 200 OK — Order retrieved
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Order does not exist
  - 500 Internal Server Error — Database error

#### POST /api/orders (Create Order)
- **Request Body:** ApiCreateOrderDTO
  - `customerId` (Long, required) — Customer ID
  - `restaurantId` (Long, required) — Restaurant ID
  - `deliveryAddress` (String, required) — Delivery address (5-255 characters)
  - `items` (List<OrderItemDTO>, required) — Array of items to order
    - `productId` (Long, required) — Product ID
    - `quantity` (Integer, required) — Quantity (minimum 1)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 201
  - `message` (String) — Success message
  - `data` (ApiOrderDTO) — Newly created order with assigned ID
- **Response Fields:**
  - All fields from GET /api/orders/{id} response
  - `status` (String) — Will be "PENDING" for new orders
  - `totalPrice` (BigDecimal) — Calculated from items
- **HTTP Status Codes:**
  - 201 Created — Order successfully created
  - 400 Bad Request — Validation failed (missing/invalid fields, no items, invalid quantities)
  - 404 Not Found — Customer, restaurant, or product does not exist
  - 500 Internal Server Error — Database error

#### POST /api/order/{order_id}/status (Update Order Status)
- **Path Parameter:**
  - `order_id` (Long, required) — Order ID to update
- **Request Body:** ApiUpdateOrderStatusDTO
  - `status` (String, required) — New status value
- **Valid Status Values:**
  - PENDING, CONFIRMED, READY, PICKED_UP, DELIVERED, CANCELLED
- **Valid Transitions:**
  - PENDING → CONFIRMED or CANCELLED
  - CONFIRMED → READY or CANCELLED
  - READY → PICKED_UP
  - PICKED_UP → DELIVERED
  - DELIVERED → (no further transitions)
  - CANCELLED → (no further transitions)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiOrderDTO) — Updated order object with new status
- **HTTP Status Codes:**
  - 200 OK — Order status successfully updated
  - 400 Bad Request — Invalid status or invalid transition
  - 404 Not Found — Order does not exist
  - 403 Forbidden — User not authorized to update this order
  - 500 Internal Server Error — Database error

#### PUT /api/orders/{id} (Update Order)
- **Path Parameter:**
  - `id` (Long, required) — Order ID to update
- **Request Body:** ApiUpdateOrderDTO (partial update)
  - `deliveryAddress` (String, optional) — Updated delivery address
  - `courierId` (Long, optional) — Assign courier to order
  - `status` (String, optional) — Update status (prefer /api/order/{id}/status endpoint)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiOrderDTO) — Updated order object
- **HTTP Status Codes:**
  - 200 OK — Order successfully updated
  - 400 Bad Request — Validation failed or invalid ID
  - 404 Not Found — Order does not exist
  - 403 Forbidden — User not authorized to update this order
  - 500 Internal Server Error — Database error

#### DELETE /api/orders/{id} (Delete Order)
- **Path Parameter:**
  - `id` (Long, required) — Order ID to delete
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200 or 204
  - `message` (String) — Success message
  - `data` (null or deletion confirmation)
- **HTTP Status Codes:**
  - 200 OK — Order successfully deleted
  - 204 No Content — Order successfully deleted (no body)
  - 400 Bad Request — Invalid ID format or order cannot be deleted (delivered/cancelled)
  - 404 Not Found — Order does not exist
  - 403 Forbidden — User not authorized to delete this order
  - 500 Internal Server Error — Database error

## 📊 Data Used or Modified

### Request Data (Inbound)

#### ApiCreateOrderDTO (POST /api/orders)
- `customerId` (Long) — Customer ID
  - Validation: @NotNull, must be positive
- `restaurantId` (Long) — Restaurant ID
  - Validation: @NotNull, must be positive
- `deliveryAddress` (String) — Delivery address
  - Validation: @NotBlank, @Size(min=5, max=255)
- `items` (List<OrderItemDTO>) — Order items
  - Validation: @NotEmpty (at least one item required)

#### OrderItemDTO (within CreateOrderDTO)
- `productId` (Long) — Product ID
  - Validation: @NotNull, must be positive
- `quantity` (Integer) — Quantity
  - Validation: @NotNull, @Positive (must be >= 1)

#### ApiUpdateOrderStatusDTO (POST /api/order/{id}/status)
- `status` (String) — New status value
  - Validation: @NotBlank, must be one of: PENDING, CONFIRMED, READY, PICKED_UP, DELIVERED, CANCELLED

#### ApiUpdateOrderDTO (PUT /api/orders/{id})
- `deliveryAddress` (String, optional) — Updated delivery address
  - Validation: @Size(min=5, max=255) if provided
- `courierId` (Long, optional) — Courier to assign
  - Validation: must be positive if provided
- `status` (String, optional) — New status (prefer dedicated endpoint)
  - Validation: must be valid status if provided

### Response Data (Outbound)

#### ApiOrderDTO (GET/POST/PUT Responses)
- `id` (Long) — Database-generated order ID
- `customerId` (Long) — Associated customer ID
- `restaurantId` (Long) — Associated restaurant ID
- `courierId` (Long, nullable) — Assigned courier (null if not assigned)
- `deliveryAddress` (String) — Delivery address
- `status` (String) — Current status (PENDING, CONFIRMED, READY, PICKED_UP, DELIVERED, CANCELLED)
- `totalPrice` (BigDecimal) — Total order amount
- `items` (List<ProductOrderDTO>, for detail view) — Products in order
- `createdAt` (LocalDateTime) — ISO 8601 timestamp
- `updatedAt` (LocalDateTime) — ISO 8601 timestamp

#### ProductOrderDTO (Item Details)
- `id` (Long) — ProductOrder record ID
- `productId` (Long) — Product ID
- `productName` (String) — Product name
- `quantity` (Integer) — Quantity ordered
- `unitPrice` (BigDecimal) — Price per unit
- `subtotal` (BigDecimal) — Quantity × Unit Price

#### ApiResponseDTO (All Responses)
- `statusCode` (int) — HTTP status code (200, 201, 400, 403, 404, 500)
- `message` (String) — Human-readable message
- `data` (Object) — Response data (LIST, SINGLE, or null)
- `timestamp` (LocalDateTime) — When response was generated

### Data Constraints
- `customerId`: Must reference existing Customer, must be positive
- `restaurantId`: Must reference existing Restaurant, must be positive
- `courierId`: Optional, must reference existing Courier if provided, must be positive
- `deliveryAddress`: 5-255 characters
- `status`: One of: PENDING, CONFIRMED, READY, PICKED_UP, DELIVERED, CANCELLED
- `totalPrice`: Calculated from items, not user-provided
- `quantity`: Minimum 1 per item
- `id`: Generated by database, positive integer
- Product availability: All items must be from specified restaurant

## 🔒 Tech Constraints (Feature-Level)

- **Controller Pattern:** Thin controllers that parse requests and delegate to services
- **Service Pattern:** Service layer contains all business logic and validation
- **Response Format:** ALL responses must use ResponseBuilder utility
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException) with GlobalExceptionHandler
- **Validation:** Use Jakarta validation annotations (@NotBlank, @NotNull, @Size, @Positive, @Pattern)
- **REST Principles:** Follow REST conventions for HTTP methods and status codes
- **DTO Usage:** Use proper DTOs for request/response serialization (do not expose entities directly)
- **Service Calls:** Controllers only call service methods, never directly call repositories
- **Status Codes:** Must return correct codes (200 OK, 201 Created, 400 Bad Request, 403 Forbidden, 404 Not Found, 500 Error)
- **Status Transitions:** Service layer validates valid status transitions
- **Price Calculation:** Automatically calculate order total from items (not user input)
- **Authorization:** Service layer must check user permissions (customer can only access own orders, etc.)
- **Cascade Delete:** Delete must cascade to ProductOrder entries
- **Pagination:** Support page and size parameters with sensible defaults
- **Query Filtering:** Support type/id filtering for multi-view scenarios (restaurant, customer, courier perspectives)

## ✅ Acceptance Criteria

### GET All Orders Tests
- [ ] GET /api/orders endpoint exists and responds
- [ ] GET request returns HTTP 200 status
- [ ] Response includes statusCode, message, data fields
- [ ] Response data is array (even if empty)
- [ ] Each order in array includes: id, customerId, restaurantId, deliveryAddress, status, totalPrice, createdAt, updatedAt
- [ ] All orders from database are returned
- [ ] Empty database returns 200 with empty array (not error)
- [ ] GET with type=customer&id=X returns only that customer's orders
- [ ] GET with type=restaurant&id=X returns only that restaurant's orders
- [ ] GET with type=courier&id=X returns only that courier's orders
- [ ] GET with invalid type returns HTTP 400
- [ ] GET with non-existent type ID returns HTTP 404 (or 200 with empty array)
- [ ] GET with non-numeric ID returns HTTP 400
- [ ] Response uses ResponseBuilder format
- [ ] Response Content-Type is application/json
- [ ] Results are sorted by createdAt descending (newest first)

### GET Single Order Tests
- [ ] GET /api/orders/{id} endpoint exists
- [ ] GET with valid ID returns HTTP 200
- [ ] Response includes complete order data
- [ ] Response includes all required fields (id, customerId, restaurantId, deliveryAddress, status, totalPrice, timestamps)
- [ ] Response includes items array with product details
- [ ] Each item includes: productId, productName, quantity, unitPrice, subtotal
- [ ] GET with non-existent ID returns HTTP 404
- [ ] GET with invalid ID format (non-numeric) returns HTTP 400
- [ ] GET with ID = 0 or negative returns HTTP 400
- [ ] Response uses ResponseBuilder format
- [ ] Order can be retrieved regardless of status

### POST Create Order Tests
- [ ] POST /api/orders endpoint exists
- [ ] POST with valid data returns HTTP 201 Created status
- [ ] Response includes newly created order object
- [ ] Created order has auto-generated ID
- [ ] Created order has PENDING status initially
- [ ] Created order persists to database
- [ ] Total price is calculated correctly from items
- [ ] POST with missing customerId returns HTTP 400
- [ ] POST with missing restaurantId returns HTTP 400
- [ ] POST with missing deliveryAddress returns HTTP 400
- [ ] POST with missing items array returns HTTP 400
- [ ] POST with empty items array returns HTTP 400
- [ ] POST with items from different restaurants returns HTTP 400
- [ ] POST with invalid quantity (0 or negative) returns HTTP 400
- [ ] POST with empty deliveryAddress returns HTTP 400
- [ ] POST with deliveryAddress < 5 characters returns HTTP 400
- [ ] POST with non-existent customer returns HTTP 404
- [ ] POST with non-existent restaurant returns HTTP 404
- [ ] POST with non-existent product returns HTTP 404
- [ ] Response includes all fields from GET response
- [ ] Response uses ResponseBuilder format
- [ ] New order is queryable via GET /api/orders/{id}
- [ ] New order appears in customer's order history

### POST Update Order Status Tests
- [ ] POST /api/order/{id}/status endpoint exists
- [ ] POST with valid transition returns HTTP 200
- [ ] POST updates order status in database
- [ ] Response includes updated order with new status
- [ ] PENDING → CONFIRMED transition succeeds
- [ ] PENDING → CANCELLED transition succeeds
- [ ] CONFIRMED → READY transition succeeds
- [ ] CONFIRMED → CANCELLED transition succeeds
- [ ] READY → PICKED_UP transition succeeds
- [ ] PICKED_UP → DELIVERED transition succeeds
- [ ] DELIVERED order cannot be changed (returns HTTP 400)
- [ ] CANCELLED order cannot be changed (returns HTTP 400)
- [ ] Invalid transitions return HTTP 400 (e.g., PENDING → READY)
- [ ] POST with non-existent order returns HTTP 404
- [ ] POST with invalid status value returns HTTP 400
- [ ] Updated timestamp reflects the update time
- [ ] Order ID does not change after status update
- [ ] Response uses ResponseBuilder format

### PUT Update Order Tests
- [ ] PUT /api/orders/{id} endpoint exists
- [ ] PUT with valid data returns HTTP 200
- [ ] PUT updates order in database
- [ ] Response includes updated order object
- [ ] PUT with non-existent ID returns HTTP 404
- [ ] PUT with invalid ID format returns HTTP 400
- [ ] PUT with invalid field data returns HTTP 400
- [ ] PUT can update deliveryAddress
- [ ] PUT can assign courierId
- [ ] PUT with only deliveryAddress updates only that field
- [ ] PUT with empty deliveryAddress returns HTTP 400
- [ ] PUT does not lose other fields (idempotent)
- [ ] Updated timestamp reflects the update time
- [ ] Order ID does not change after update
- [ ] Response uses ResponseBuilder format
- [ ] Updated order is immediately queryable

### DELETE Order Tests
- [ ] DELETE /api/orders/{id} endpoint exists
- [ ] DELETE with valid ID returns HTTP 200 or 204
- [ ] DELETE removes order from database
- [ ] DELETE with non-existent ID returns HTTP 404
- [ ] DELETE with invalid ID format returns HTTP 400
- [ ] DELETE cascades to remove ProductOrders
- [ ] DELETE of delivered order returns HTTP 400 (prevent deletion)
- [ ] DELETE of cancelled order returns HTTP 400 (prevent deletion)
- [ ] DELETE of pending/confirmed order succeeds
- [ ] Deleted order cannot be retrieved via GET
- [ ] Response uses ResponseBuilder format
- [ ] Deleted order does not appear in customer's order history

### Integration Tests
- [ ] All 6 endpoints use same response format (ResponseBuilder)
- [ ] Error messages are consistent across endpoints
- [ ] Status codes follow REST conventions
- [ ] All endpoints return application/json Content-Type
- [ ] GET /api/orders filtering works with database queries
- [ ] POST /api/orders calculates total price correctly
- [ ] POST /api/order/{id}/status validates transitions
- [ ] DELETE cascades properly to related records
- [ ] Controller does not directly access repository (only through service)
- [ ] Service layer is called by controller, not repository
- [ ] All validation annotations work correctly
- [ ] GlobalExceptionHandler catches all exceptions and formats responses
- [ ] Orders from different customers/restaurants are properly isolated

## 📝 Notes for the AI

- **Pre-written Tests:** Check if there are any pre-written tests for this controller. Implementation must pass these tests without modification. Examine test file to understand expected behavior.
- **Related Features:** This Orders API depends on:
  - `orders-sql.feature.md` — SQL queries for filtering and deletion
  - `products-sql.feature.md` — Product data for order items
  - `restaurants-sql.feature.md` — Restaurant validation
  - `auth-api.feature.md` — Customer/user authentication context
  - `order-status-api.feature.md` — Order status management (related feature)
- **Controller Responsibilities:** Controllers should:
  - Receive HTTP request
  - Parse path parameters and request body
  - Validate HTTP-level inputs
  - Delegate to service layer for business logic
  - Return response using ResponseBuilder
  - Handle exceptions from service layer
- **Service Responsibilities:** Services should:
  - Contain all business logic (validation, calculations, transitions)
  - Call repository methods for data access
  - Handle data transformation (entity↔DTO)
  - Execute cascade operations (cascade delete ProductOrders)
  - Validate authorization (customer can only access own orders)
  - Validate status transitions (PENDING→CONFIRMED is valid, PENDING→READY is not)
  - Throw appropriate exceptions
- **Thin Controllers:** Controllers should have minimal business logic — validation and service delegation only
- **Price Calculation:** When creating an order, calculate total price in service layer:
  - Fetch product prices from repository
  - Multiply by quantity for each item
  - Sum all subtotals
  - Never trust user-provided totals
- **Status Transitions:** Maintain a map or validation logic in service for valid transitions:
  ```
  PENDING: can go to CONFIRMED, CANCELLED
  CONFIRMED: can go to READY, CANCELLED
  READY: can go to PICKED_UP
  PICKED_UP: can go to DELIVERED
  DELIVERED: no transitions (final state)
  CANCELLED: no transitions (final state)
  ```
- **Filtering Logic:** GET /api/orders should support:
  - No parameters: return all orders
  - type=customer&id=5: return orders where customerId = 5
  - type=restaurant&id=3: return orders where restaurantId = 3
  - type=courier&id=7: return orders where courierId = 7
  - Invalid type parameter: return 400 Bad Request
  - Valid type but non-existent ID: return 404 Not Found or 200 with empty array (decide based on requirements)
- **Authorization:** Consider implementing permission checks:
  - Customer can only view/update/delete their own orders
  - Restaurant can view orders for their restaurant
  - Courier can view assigned orders
  - Admin/system can view all orders
  - For MVP, may be simplified (assume all API calls are authorized)
- **Cascade Delete Complexity:** When DELETE is called:
  - Fetch order by ID
  - Verify order exists (404 if not)
  - Verify order is not DELIVERED or CANCELLED (400 if immutable)
  - Delete all ProductOrder entries for this order
  - Delete the Order record itself
  - Confirm deletion to user
- **Response Consistency:** Every endpoint uses ResponseBuilder — examine GlobalExceptionHandler and ResponseBuilder to understand format:
  - Success: `{ statusCode: 200, message: "...", data: {...}, timestamp: "..." }`
  - Error: `{ statusCode: 400, message: "...", data: null, timestamp: "..." }`
- **HTTP Status Codes Matter:** Mobile apps depend on correct status codes:
  - 200 OK — General success
  - 201 Created — Resource successfully created
  - 400 Bad Request — Validation error or bad state (immutable status)
  - 403 Forbidden — User not authorized
  - 404 Not Found — Resource not found
  - 500 Error — Unexpected server error
- **Idempotence:** GET and DELETE should be safe/idempotent where appropriate:
  - GET requests have no side effects
  - Multiple DELETEs of same order: first succeeds, subsequent return 404 (acceptable pattern)
  - PUT should be idempotent (sending same data twice produces same result)
- **Test Coverage:** Write tests for all happy paths and error cases:
  - Valid requests with all success statuses
  - Invalid data (validation errors)
  - Non-existent resources (404)
  - Invalid ID formats (400)
  - Invalid status transitions (400)
  - Authorization failures (403)
  - Database errors (500)
  - Filtering with type/id parameters
  - Cascade deletion verification
- **Integration with SQL Feature:** Each filtering endpoint corresponds to SQL queries in orders-sql.feature.md:
  - GET /api/orders?type=restaurant&id=X → OrderRepository.findOrdersByRestaurantId(X)
  - GET /api/orders?type=customer&id=X → OrderRepository.findOrdersByCustomerId(X)
  - GET /api/orders?type=courier&id=X → OrderRepository.findOrdersByCourierId(X)
  - DELETE /api/orders/{id} → OrderRepository.deleteOrderById(id) with cascade
- **Related Endpoints:** Note the two distinct endpoints for order management:
  - POST /api/orders — Create full new order
  - POST /api/order/{id}/status — Update only the status (simplified)
  - PUT /api/orders/{id} — General update (delivery address, courier, status)
  - Use /order/{id}/status for status transitions (preferred)
  - Use PUT for other field updates
