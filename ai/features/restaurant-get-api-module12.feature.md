# 🤖 AI_FEATURE_Restaurant Get API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Restaurant Get API - Single Restaurant Retrieval
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a GET endpoint that returns detailed information for a single restaurant given its ID. This allows clients to fetch restaurant details for display or further operations.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Retrieve restaurant by ID
- Return restaurant id, name, price_range, and rating
- HTTP status codes (200 OK, 404 Not Found, 400 Bad Request)
- Path parameter validation (numeric, non-negative)
- Error response with descriptive message

### ❌ Out of Scope (Excluded)

- Detailed restaurant information (phone, email, address)
- Menu/product listing
- Reviews or comments
- Availability status

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Path Parameter:** Required restaurant ID
- **ID Validation:** Must be numeric and positive
- **Database Query:** Parameterized SQL lookup
- **Response Format:** Single restaurant object or error
- **Error Handling:** 404 if not found, 400 if invalid ID

---

## 👥 User Flow / Logic (High Level)

### Successful Retrieval
1. User sends GET /api/restaurants/{id} with valid ID
2. Controller validates ID format
3. Service queries database for restaurant
4. Restaurant found
5. Response: 200 OK with restaurant object

### Not Found
1. User sends GET /api/restaurants/{id} with non-existent ID
2. Controller validates ID format
3. Service queries database
4. No restaurant found
5. Response: 404 Not Found with error message

### Invalid ID Format
1. User sends GET /api/restaurants/abc (non-numeric)
2. Controller validates ID format
3. ID validation fails
4. Response: 400 Bad Request

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 GET /api/restaurants/{id} - Single Restaurant

#### Request

**Method:** GET
**Path:** /api/restaurants/{id}
**Path Parameters:**
- `id` (required): restaurant ID (integer)

**Examples:**
- `/api/restaurants/1`
- `/api/restaurants/5`

#### Success Response (200 OK)

```json
{
  "id": 1,
  "name": "Villa Wellington",
  "price_range": 3,
  "rating": 4
}
```

#### Not Found Response (404 Not Found)

```json
{
  "error": "Resource not found",
  "details": "Restaurant with id <id> not found"
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

### SQL Query

```sql
SELECT id, name, price_range, rating 
FROM restaurants 
WHERE id = ?1
```

### Response Data Structure

```java
public class RestaurantDTO {
    private int id;
    private String name;
    private int price_range;
    private int rating;
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Path Parameter:** Integer type, must be positive
- **Parameter Validation:** Validate before database query
- **Query Method:** Spring Data JPA with parameterized @Query
- **Response Format:** Single object or error
- **Status Codes:** 200, 404, 400

---

## ✅ Acceptance Criteria

### Successful Retrieval
- [ ] GET /api/restaurants/{id} endpoint exists
- [ ] Valid ID returns 200 OK
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

---

## 📝 Notes for the AI

- **ID Validation:** Validate ID is positive integer before database query
- **404 Message:** Include the requested ID in error details: "Restaurant with id 10 not found"
- **Parameterized SQL:** Use @Query("SELECT ... WHERE id = ?1")
