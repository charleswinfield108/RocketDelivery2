# 🤖 AI_FEATURE_Restaurant Update API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Restaurant Update API - Modify Restaurant Details
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a PUT endpoint that updates existing restaurant information. Allows modification of name, price_range, and phone while preserving other fields like email, user_id, and address.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Update restaurant by ID
- Modifiable fields: name, price_range, phone
- Preserve unchanged fields: email, user_id, address
- HTTP status codes (200 OK, 404 Not Found, 400 Bad Request)
- Path parameter validation
- Request body validation
- Return updated restaurant in response
- price_range must be 1-3

### ❌ Out of Scope (Excluded)

- Update email address
- Update user_id
- Update or change address
- Update rating
- Cascade updates

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Path Parameter:** Required restaurant ID
- **Request Body:** name, price_range, phone (optional/required fields)
- **Field Validation:** Constraints on each field
- **Database Update:** Parameterized SQL update
- **Response Format:** Complete updated restaurant object
- **Error Handling:** 404 if not found, 400 for validation errors

---

## 👥 User Flow / Logic (High Level)

### Successful Update
1. User sends PUT /api/restaurants/{id} with updates
2. Controller validates ID format
3. Service queries restaurant by ID
4. Restaurant found
5. Service validates request fields (name, price_range, phone)
6. Validation passes
7. Service updates restaurant in database
8. Service returns updated restaurant
9. Response: 200 OK with updated restaurant

### Not Found
1. User sends PUT /api/restaurants/{id} with non-existent ID
2. Controller validates ID format
3. Service queries database
4. Restaurant not found
5. Response: 404 Not Found

### Validation Failure
1. User sends PUT with invalid price_range
2. Service validates request
3. Validation fails
4. Response: 400 Bad Request with validation error details

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 PUT /api/restaurants/{id} - Update Restaurant

#### Request

**Method:** PUT
**Path:** /api/restaurants/{id}
**Content-Type:** application/json

**Body:**
```json
{
  "name": "B12 Nation",
  "price_range": 3,
  "phone": "2223334444"
}
```

**Modifiable Fields:**
- name: string (optional or required)
- price_range: integer 1-3 (optional or required)
- phone: string (optional or required)

**Non-modifiable Fields (Ignored if Provided):**
- email: preserved
- user_id: preserved
- address: preserved
- rating: preserved
- id: preserved

#### Success Response (200 OK)

```json
{
  "message": "Success",
  "data": {
    "id": 9,
    "name": "B12 Nation",
    "phone": "2223334444",
    "email": "villa@wellington.com",
    "address": {
      "id": 21,
      "city": "Montreal",
      "street_address": "123 Wellington St.",
      "postal_code": "H3G264"
    },
    "user_id": 3,
    "price_range": 3
  }
}
```

**Response Fields:**
- message: "Success"
- data: Complete updated restaurant object
- Includes unchanged fields (email, user_id, address)
- Includes updated fields (name, price_range, phone)

#### Not Found Response (404 Not Found)

```json
{
  "error": "Resource not found",
  "details": "Restaurant with id 10 not found"
}
```

#### Validation Error Response (400 Bad Request)

```json
{
  "error": "Validation failed",
  "details": "<error details>"
}
```

**Scenarios:**
- price_range outside 1-3
- price_range not numeric
- Empty string for name or phone
- Invalid field values

---

## 📊 Data Used or Modified

### Request Data Structure

```java
public class UpdateRestaurantRequest {
    private String name;        // Optional/Required
    private int price_range;    // Optional/Required, must be 1-3
    private String phone;       // Optional/Required
}
```

### SQL Query

#### Fetch Current Restaurant
```sql
SELECT * FROM restaurants WHERE id = ?1
```

#### Update Restaurant
```sql
UPDATE restaurants 
SET name = ?1, price_range = ?2, phone = ?3 
WHERE id = ?4
```

### Response Data Structure

```java
public class RestaurantResponse {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int user_id;
    private int price_range;
    private AddressResponse address;
}

public class AddressResponse {
    private int id;
    private String street_address;
    private String city;
    private String postal_code;
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **HTTP Method:** PUT (not PATCH)
- **Path Parameter:** Integer, positive
- **Request Format:** JSON only
- **Status Code:** 200 OK on success
- **price_range Validation:** Must be 1-3
- **Update Logic:** Only update provided fields
- **Preserved Fields:** email, user_id, address, rating remain unchanged
- **SQL:** Parameterized @Query update

---

## ✅ Acceptance Criteria

### Update Success
- [ ] PUT /api/restaurants/{id} endpoint exists
- [ ] Valid ID with valid data returns 200 OK
- [ ] Response includes message: "Success"
- [ ] Response includes data field
- [ ] name field updated in database
- [ ] price_range field updated in database
- [ ] phone field updated in database
- [ ] email field preserved
- [ ] user_id field preserved
- [ ] address field preserved

### Not Found
- [ ] Non-existent ID returns 404 Not Found
- [ ] Error message includes "Resource not found"
- [ ] Error details includes restaurant id

### Validation
- [ ] price_range outside 1-3 returns 400 Bad Request
- [ ] price_range not numeric returns 400 Bad Request
- [ ] Empty name returns 400 Bad Request
- [ ] Empty phone returns 400 Bad Request
- [ ] Error message describes validation failure
- [ ] Database not updated on validation failure

### Response Fields
- [ ] Response includes all fields (id, name, phone, email, price_range, user_id, address)
- [ ] Address object nested in response
- [ ] Address fields present (street_address, city, postal_code)

---

## 📝 Notes for the AI

- **price_range Validation:** Enforce 1-3 range before database update
- **Partial Updates:** Only fields in request body are updated; others preserved
- **Non-modifiable Fields:** If email, user_id, or address provided, ignore them
- **Response Completeness:** Even though only name/phone/price_range updated, response includes ALL restaurant fields including unchanged ones
- **SQL Update:** Use parameterized UPDATE statement with WHERE clause
- **Status Code:** 200 OK (not 201) for update operations
