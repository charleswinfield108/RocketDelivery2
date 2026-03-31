# 🤖 AI_FEATURE_Restaurant Delete API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Restaurant Delete API - Remove Restaurant
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a DELETE endpoint that removes a restaurant from the system. This allows administrators to delete restaurants that are no longer operating or need to be removed from the platform.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Delete restaurant by ID
- HTTP status codes (200 OK, 404 Not Found)
- Path parameter validation
- Return deleted restaurant in response
- Delete operation on single restaurant

### ❌ Out of Scope (Excluded)

- Cascade deletion of related data (products, orders, addresses)
- Soft delete (archive vs hard delete)
- Delete confirmation/verification
- Audit logging
- Authorization checks

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Path Parameter:** Required restaurant ID
- **ID Validation:** Must be numeric, positive
- **Database Query:** Parameterized SQL delete
- **Response Format:** Deleted restaurant object or error
- **Error Handling:** 404 if not found, 400 if invalid ID

---

## 👥 User Flow / Logic (High Level)

### Successful Deletion
1. User sends DELETE /api/restaurants/{id}
2. Controller validates ID format
3. Service queries for restaurant by ID
4. Restaurant found
5. Service deletes restaurant from database
6. Response: 200 OK with deleted restaurant data

### Not Found
1. User sends DELETE /api/restaurants/{id} with non-existent ID
2. Controller validates ID format
3. Service queries database
4. No restaurant found
5. Response: 404 Not Found with error message

### Invalid ID Format
1. User sends DELETE /api/restaurants/abc (non-numeric)
2. Controller validates ID format
3. ID validation fails
4. Response: 400 Bad Request

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 DELETE /api/restaurants/{id} - Delete Restaurant

#### Request

**Method:** DELETE
**Path:** /api/restaurants/{id}
**Path Parameters:**
- `id` (required): restaurant ID (integer)

**Examples:**
- `/api/restaurants/1`
- `/api/restaurants/100`

**No Request Body**

#### Success Response (200 OK)

```json
{
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Don Mole",
    "price_range": 2,
    "rating": 4
  }
}
```

**Response Fields:**
- message: "Success"
- data: Deleted restaurant object (before deletion)
- Includes id, name, price_range, rating

#### Not Found Response (404 Not Found)

```json
{
  "error": "Resource not found",
  "details": "Restaurant with id 942 not found"
}
```

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenario:** Non-numeric ID, negative ID, or zero ID

---

## 📊 Data Used or Modified

### SQL Operations

#### Fetch Restaurant Before Deletion
```sql
SELECT id, name, price_range, rating 
FROM restaurants 
WHERE id = ?1
```

#### Delete Restaurant
```sql
DELETE FROM restaurants 
WHERE id = ?1
```

### Response Data Structure

```java
public class RestaurantResponse {
    private int id;
    private String name;
    private int price_range;
    private int rating;
}
```

### Data Returned
- Returns the restaurant data as it was before deletion
- Used to confirm deletion to client

---

## 🔒 Tech Constraints (Feature-Level)

- **Path Parameter:** Integer type, must be positive
- **HTTP Method:** DELETE only
- **Status Code:** 200 OK on success
- **No Request Body:** DELETE has no body
- **SQL:** Parameterized @Query delete
- **Atomic:** Single operation, all or nothing
- **Response:** Returns deleted restaurant data for confirmation

---

## ✅ Acceptance Criteria

### Deletion Success
- [ ] DELETE /api/restaurants/{id} endpoint exists
- [ ] Valid ID returns 200 OK
- [ ] Response includes message: "Success"
- [ ] Response includes deleted restaurant data
- [ ] Restaurant removed from database
- [ ] Cannot retrieve deleted restaurant (404 on subsequent GET)

### Data Returned
- [ ] Response includes id field
- [ ] Response includes name field
- [ ] Response includes price_range field
- [ ] Response includes rating field

### Not Found
- [ ] Non-existent ID returns 404 Not Found
- [ ] Error message includes "Resource not found"
- [ ] Error details includes restaurant id
- [ ] No data field in response

### Invalid Parameters
- [ ] Non-numeric ID returns 400 Bad Request
- [ ] Negative ID returns 400 Bad Request
- [ ] Zero ID returns 400 Bad Request
- [ ] Error message included in response

### Database Verification
- [ ] After deletion, GET same ID returns 404
- [ ] Restaurant no longer exists in database
- [ ] Deletion is permanent (not soft delete)

---

## 📝 Notes for the AI

- **ID Validation:** Validate ID is positive integer before database operation
- **404 Message:** Include requested ID in error details: "Restaurant with id 942 not found"
- **Response Data:** Return the restaurant data from before deletion (not empty response)
- **Cascade Handling:** At this stage, assume restaurant has no dependent records or cascade is handled by database constraints
- **Idempotency:** Second DELETE of same ID returns 404 (not 200)
- **No Soft Delete:** Hard delete from database (not marking as deleted)
