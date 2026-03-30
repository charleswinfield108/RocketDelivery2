# 🤖 AI_FEATURE_Order Status Tests - POST

## 🎯 Feature Identity

- **Feature Name:** Order Status Tests - POST Endpoint Coverage (Extra Miles)
- **Related Area:** Backend / Testing / Quality Assurance

---

## 🎪 Feature Goal

Establish comprehensive test coverage for order status update endpoint (POST /api/order/{id}/status) using JUnit 5 and MockMvc. The tests must validate state machine transitions (PENDING → ACCEPTED → IN_DELIVERY → DELIVERED), enforce valid/invalid transitions, verify status persistence, handle edge cases, and ensure proper error handling for invalid operations.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Unit tests for OrderService status update method
- Integration tests for OrderApiController POST /api/order/{id}/status endpoint
- State machine transition validation (valid vs invalid)
- Valid state transitions: PENDING→ACCEPTED, PENDING→CANCELED, ACCEPTED→IN_DELIVERY, ACCEPTED→CANCELED, IN_DELIVERY→DELIVERED, IN_DELIVERY→DELIVERED, others as defined
- Invalid transition prevention (e.g., DELIVERED→PENDING should fail)
- HTTP status code validation (200 OK, 400 Bad Request, 404 Not Found, 409 Conflict, 500 Error)
- Database persistence verification (status updated in database)
- Request body validation (required fields, format)
- Non-existent order returns 404
- Response includes updated order with new status
- MockMvc setup and JSON path assertions
- Test fixtures and data builders
- Edge cases (null status, empty string, invalid enum values)
- Concurrent update handling (optimistic locking if applicable)
- Timestamp updates (updatedAt should change)
- Status history tracking (if applicable)

### ❌ Out of Scope (Excluded)

- Authentication/authorization on status updates
- Email notifications on status change
- UI or frontend for status updates
- Performance or load testing
- Bulk status updates
- Status history API
- Automatic status transitions (time-based)
- Delivery tracking beyond status
- Customer notifications

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Valid Transitions:** Test all valid state machine paths
- **Invalid Transitions:** Test rejection of impossible transitions
- **State Machine Diagram:** Document all valid transitions
- **Request Validation:** Test missing/invalid status values
- **Non-existent Orders:** Test 404 behavior
- **Database Persistence:** Verify status saved correctly
- **Timestamp Management:** Verify updatedAt changes
- **Response Validation:** Verify updated order returned
- **Error Messages:** Verify appropriate error descriptions
- **Edge Cases:** Null status, invalid enum, empty values

---

## 👥 User Flow / Logic (High Level)

### Valid Status Transition Flow
1. Test setup creates order with status PENDING
2. Test sends POST request to /api/order/{id}/status with body: { "status": "ACCEPTED" }
3. MockMvc captures response
4. Service validates PENDING→ACCEPTED is valid transition
5. Service updates order status in database
6. Service updates updatedAt timestamp
7. Test verifies 200 status code
8. Test verifies response includes updated order
9. Test verifies new status in response
10. Test verifies status persisted in database

### Invalid Status Transition Flow
1. Test setup creates order with status DELIVERED
2. Test sends POST request to /api/order/{id}/status with body: { "status": "PENDING" }
3. MockMvc captures response
4. Service validates DELIVERED→PENDING is INVALID
5. Service returns 409 Conflict
6. Service does NOT update database
7. Test verifies 409 status code
8. Test verifies error message about invalid transition
9. Test verifies order status unchanged in database

### Non-existent Order Flow
1. Test sends POST request to /api/order/{invalidId}/status with valid status
2. MockMvc captures response
3. Service queries database for order
4. Order not found
5. Service returns 404 Not Found
6. Test verifies 404 status code
7. Test verifies appropriate error message

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Testing Interfaces (JUnit 5 + MockMvc)

#### POST /api/order/{id}/status Test Cases

**Valid Transition Tests:**
- `testUpdateOrderStatusPendingToAccepted()` — PENDING→ACCEPTED transition
- `testUpdateOrderStatusPendingToCanceled()` — PENDING→CANCELED transition
- `testUpdateOrderStatusAcceptedToInDelivery()` — ACCEPTED→IN_DELIVERY transition
- `testUpdateOrderStatusAcceptedToCanceled()` — ACCEPTED→CANCELED transition
- `testUpdateOrderStatusInDeliveryToDelivered()` — IN_DELIVERY→DELIVERED transition
- `testUpdateOrderStatusResponseFormat()` — Response includes complete order
- `testUpdateOrderStatusPersisted()` — Status saved to database
- `testUpdateOrderStatusTimestampUpdated()` — UpdatedAt timestamp changes
- `testUpdateOrderStatusStatusFields()` — All order fields in response

**Invalid Transition Tests:**
- `testUpdateOrderStatusDeliveredToPending()` — DELIVERED→PENDING rejected
- `testUpdateOrderStatusDeliveredToAccepted()` — DELIVERED→ACCEPTED rejected
- `testUpdateOrderStatusCanceledToAccepted()` — CANCELED→ACCEPTED rejected
- `testUpdateOrderStatusInDeliveryToPending()` — IN_DELIVERY→PENDING rejected
- `testUpdateOrderStatusSelfTransition()` — PENDING→PENDING handling

**Error Handling Tests:**
- `testUpdateOrderStatusNonexistentOrder()` — Non-existent ID returns 404
- `testUpdateOrderStatusInvalidOrderId()` — Invalid ID format returns 400
- `testUpdateOrderStatusNegativeId()` — Negative ID returns 400
- `testUpdateOrderStatusZeroId()` — Zero ID returns 400
- `testUpdateOrderStatusMissingStatus()` — Missing status in request returns 400
- `testUpdateOrderStatusNullStatus()` — Null status returns 400
- `testUpdateOrderStatusEmptyStatus()` — Empty string status returns 400
- `testUpdateOrderStatusInvalidEnumValue()` — Invalid status value returns 400
- `testUpdateOrderStatusInvalidStatusCase()` — Case sensitivity validation
- `testUpdateOrderStatusInvalidTransition()` — State machine enforced returns 409

**Data Validation Tests:**
- `testUpdateOrderStatusConflictMessage()` — Error message describes conflict
- `testUpdateOrderStatusNoPartialUpdate()` — Atomic update (all or nothing)
- `testUpdateOrderStatusResponseContainsUpdatedStatus()` — Response shows new status
- `testUpdateOrderStatusNotModifiedIfError()` — Database unchanged on error

---

## 📊 Data Used or Modified

### Test Data Structure (Valid Transitions)

#### Complete Order with Status
```java
Restaurant restaurant = createRestaurant("Test Restaurant", "100 Test St", "5551234567");

Customer customer = createCustomer("John Doe", "john@example.com", "password123");

Product product1 = createProduct(restaurant, "Burger", "Tasty burger", 8.99, 50);
Product product2 = createProduct(restaurant, "Fries", "Golden fries", 3.99, 100);

Order order = new Order(
    id: generatedId,
    customer: customer,
    restaurant: restaurant,
    status: OrderStatus.PENDING,
    totalPrice: 12.98,
    createdAt: now,
    updatedAt: now
);

ProductOrder po1 = new ProductOrder(order, product1, 1, 8.99);
ProductOrder po2 = new ProductOrder(order, product2, 1, 3.99);

// Save to database
```

#### Status Update Request Body
```json
{
    "status": "ACCEPTED"
}
```

#### Test Assertions
```java
mockMvc.perform(post("/api/order/" + orderId + "/status")
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"status\": \"ACCEPTED\"}"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.data.id").value(orderId))
    .andExpect(jsonPath("$.data.status").value("ACCEPTED"))
    .andExpect(jsonPath("$.data.updatedAt").exists());

// Verify in database
Order updated = orderRepository.findById(orderId).get();
assertEquals(OrderStatus.ACCEPTED, updated.getStatus());
assertTrue(updated.getUpdatedAt().isAfter(originalUpdatedAt));
```

### Test Data Structure (Invalid Transitions)

#### Orders with Different Initial Statuses
```java
Order pendingOrder = createOrder(..., OrderStatus.PENDING);
Order acceptedOrder = createOrder(..., OrderStatus.ACCEPTED);
Order inDeliveryOrder = createOrder(..., OrderStatus.IN_DELIVERY);
Order completedOrder = createOrder(..., OrderStatus.DELIVERED);
Order cancelledOrder = createOrder(..., OrderStatus.CANCELED);
```

### State Machine Definition

#### Valid State Transitions
```
PENDING → [ACCEPTED, CANCELED]
ACCEPTED → [IN_DELIVERY, CANCELED]
IN_DELIVERY → [DELIVERED]
DELIVERED → (terminal state, no transitions)
CANCELED → (terminal state, no transitions)
```

#### HTTP Response Codes by Scenario
```
Valid Transition: 200 OK
Invalid Transition: 409 Conflict
Non-existent Order: 404 Not Found
Missing/Invalid Status: 400 Bad Request
Invalid Order ID: 400 Bad Request
Server Error: 500 Internal Server Error
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Testing Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito for dependencies
- **Integration Testing:** MockMvc for HTTP layer
- **Assertions:** AssertJ or JUnit 5 native with JsonPath
- **Test Scope:** Controller through service to repository
- **State Machine:** Enum with defined transitions
- **Data Cleanup:** @DirtiesContext or @Transactional for isolation
- **Test Independence:** Each test independent and repeatable
- **Database Verification:** Verify both response and database state
- **Request Body:** JSON with status field
- **Response Format:** ApiResponseDTO with complete updated order

---

## ✅ Acceptance Criteria

### Valid Transition Tests
- [ ] PENDING→ACCEPTED test exists and passes
- [ ] PENDING→CANCELED test exists and passes
- [ ] ACCEPTED→IN_DELIVERY test exists and passes
- [ ] ACCEPTED→CANCELED test exists and passes
- [ ] IN_DELIVERY→DELIVERED test exists and passes
- [ ] Status updated returns 200 OK
- [ ] Response includes complete order
- [ ] Response includes new status
- [ ] Status persisted in database
- [ ] UpdatedAt timestamp changes
- [ ] All other order fields remain unchanged

### Invalid Transition Tests
- [ ] DELIVERED→PENDING test exists and returns 409
- [ ] DELIVERED→ACCEPTED test exists and returns 409
- [ ] CANCELED→ACCEPTED test exists and returns 409
- [ ] Invalid transitions return 409 Conflict
- [ ] Error message explains invalid transition
- [ ] Database status unchanged after invalid transition
- [ ] Self-transitions handled correctly

### Error Handling Tests
- [ ] Non-existent order returns 404
- [ ] Invalid ID format returns 400
- [ ] Negative ID returns 400
- [ ] Zero ID returns 400
- [ ] Missing status field returns 400
- [ ] Null status returns 400
- [ ] Empty status returns 400
- [ ] Invalid status value returns 400
- [ ] Error messages are descriptive

### Response Validation Tests
- [ ] Response includes complete order
- [ ] Response format matches ApiResponseDTO
- [ ] All order fields present (id, customer, restaurant, status, totalPrice, etc.)
- [ ] Status field updated
- [ ] UpdatedAt timestamp updated
- [ ] CreatedAt timestamp unchanged
- [ ] Database state matches response

---

## 📝 Notes for the AI

- **State Machine:** Implement as Java enum with valid transition logic, not if-else chains
- **409 Conflict:** Use HTTP 409 specifically for invalid state transitions (not 400)
- **Idempotent Status:** Consider if updating to same status is allowed (usually yes, returns 200)
- **Atomic Updates:** If any update fails, entire operation fails (database consistency)
- **Timestamp Precision:** UpdatedAt should change on successful update (compare before/after)
- **Concurrent Updates:** Consider optimistic locking if multiple updates possible simultaneously
- **Request Body:** Expect JSON: { "status": "ACCEPTED" } (not form data)
- **Response:** Include complete updated order, not just the status
- **Test Isolation:** Use @DirtiesContext or @Transactional to ensure clean database between tests
- **State History:** If tracking state changes, log before/after states for audit trail
