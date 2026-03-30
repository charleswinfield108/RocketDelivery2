# 🤖 AI_FEATURE_Order Status Update API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Order Status Update API - Change Order Status
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a POST endpoint that updates the status of an existing order. This allows the system to track order progress through various states (pending, in progress, delivered, etc.).

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Update order status by order ID
- Accept status string in request body
- HTTP status codes (200 OK, 400 Bad Request, 404 Not Found)
- Path parameter validation
- Request body validation
- Return updated order status in response
- Status as lowercase string

### ❌ Out of Scope (Excluded)

- State machine validation (no transition rules)
- Authorization checks
- Timestamp updates
- Status history tracking
- Notification triggers

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Path Parameter:** Required order ID
- **Request Body:** Status field (lowercase string)
- **ID Validation:** Must be numeric, positive
- **Status Validation:** Must be non-empty string
- **Database Update:** Parameterized SQL update
- **Response Format:** Updated status field
- **Error Handling:** 404 if not found, 400 for validation errors

---

## 👥 User Flow / Logic (High Level)

### Successful Status Update
1. User sends POST /api/order/{id}/status with status data
2. Controller validates path parameter (ID)
3. Service queries for order by ID
4. Order found
5. Service validates status field in request
6. Validation passes
7. Service updates order status in database
8. Response: 200 OK with updated status

### Order Not Found
1. User sends POST /api/order/{id}/status with non-existent ID
2. Controller validates ID format
3. Service queries database for order
4. Order not found
5. Response: 404 Not Found

### Missing or Invalid Status
1. User sends POST /api/order/{id}/status with missing status
2. Controller/Service validates request body
3. Validation fails
4. Response: 400 Bad Request

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 POST /api/order/{id}/status - Update Order Status

#### Request

**Method:** POST
**Path:** /api/order/{order_id}/status
**Content-Type:** application/json

**Body:**
```json
{
  "status": "delivered"
}
```

**Required Fields:**
- status: lowercase string (non-empty)
  - Example values: "pending", "in progress", "delivered", "cancelled"

#### Success Response (200 OK)

```json
{
  "status": "delivered"
}
```

**Response Fields:**
- status: The updated status value

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenarios:**
- Missing status field in request
- Empty status value
- Invalid status format

#### Not Found Response (404 Not Found)

```json
{
  "error": "Resource not found",
  "details": "Order with id <id> not found"
}
```

**Scenario:** Order ID does not exist

---

## 📊 Data Used or Modified

### Request Data Structure

```java
public class UpdateOrderStatusRequest {
    private String status;  // e.g., "delivered", "in progress"
}
```

### SQL Operations

#### Get Order
```sql
SELECT * FROM orders WHERE id = ?1
```

#### Update Order Status
```sql
UPDATE orders 
SET status = ?1 
WHERE id = ?2
```

### Response Data Structure

```java
public class OrderStatusResponse {
    private String status;
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Path Parameter:** Integer type, positive
- **HTTP Method:** POST
- **Request Format:** JSON only
- **Status Field:** Lowercase string, non-empty (e.g., "delivered")
- **Response Format:** Simple object with status field
- **Status Code:** 200 OK on success
- **SQL:** Parameterized UPDATE query
- **Atomic:** Single update operation

---

## ✅ Acceptance Criteria

### Status Update Success
- [ ] POST /api/order/{id}/status endpoint exists
- [ ] Valid ID and status returns 200 OK
- [ ] Response includes status field
- [ ] Status updated in database
- [ ] Updated status matches request value
- [ ] Database persistence verified

### Missing or Invalid Status
- [ ] Missing status field returns 400 Bad Request
- [ ] Empty status returns 400 Bad Request
- [ ] Response includes error message
- [ ] Database not updated on error

### Order Not Found
- [ ] Non-existent order ID returns 404 Not Found
- [ ] Error message includes "Resource not found"
- [ ] Error details includes order id
- [ ] Status field not in error response

### Parameter Validation
- [ ] Non-numeric ID returns 400 Bad Request (or 404 if caught earlier)
- [ ] Negative ID returns 400 Bad Request
- [ ] Zero ID returns 400 Bad Request
- [ ] Error message included

### Response Format
- [ ] Response is JSON object (not array)
- [ ] Response includes status field
- [ ] Status value matches database

---

## 📝 Notes for the AI

- **Status as Lowercase:** Status in request should be lowercase (e.g., "delivered" not "DELIVERED")
- **Simple Response:** Response is very simple, just the status field
- **No State Machine (at this stage):** Accept any status string without validation of transitions
- **Database Update:** Use parameterized UPDATE statement
- **404 Message:** Include order id in error: "Order with id <id> not found"
- **Idempotency:** Updating to same status should succeed (200 OK)
