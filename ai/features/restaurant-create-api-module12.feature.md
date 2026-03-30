# 🤖 AI_FEATURE_Restaurant Create API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Restaurant Create API - New Restaurant Registration
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a POST endpoint that creates a new restaurant with associated address information. This allows restaurant owners to register their restaurants in the system.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Create new restaurant record
- Store associated address information
- Accept user_id, name, phone, email, price_range, and address details
- Validate required fields
- HTTP status codes (201 Created, 400 Bad Request)
- Return created restaurant with address ID
- Address is embedded in request and response

### ❌ Out of Scope (Excluded)

- Restaurant approval/verification process
- Address validation against external services
- State/province validation
- Country validation
- Phone number format validation

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Request Body:** user_id, name, phone, email, price_range, address object
- **Address Object:** street_address, city, postal_code
- **Field Validation:** Required fields and constraints
- **Database Operations:** Create restaurant and address records
- **Response Format:** Created restaurant with nested address and generated IDs
- **Error Handling:** 400 for invalid/missing parameters

---

## 👥 User Flow / Logic (High Level)

### Restaurant Creation
1. User sends POST /api/restaurants with restaurant data and address
2. Controller validates request body
3. Service validates all required fields present and valid
4. Service validates price_range is 1-3
5. Service creates Address record first
6. Service creates Restaurant record with address_id
7. Database assigns IDs to both records
8. Response: 201 Created with complete restaurant object and generated IDs

### Validation Failure
1. User sends POST /api/restaurants with missing/invalid data
2. Controller validates request body
3. Validation fails (missing field, invalid value, etc.)
4. Response: 400 Bad Request with error message

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 POST /api/restaurants - Create Restaurant

#### Request

**Method:** POST
**Path:** /api/restaurants
**Content-Type:** application/json

**Body:**
```json
{
  "user_id": 2,
  "name": "Villa Wellington",
  "phone": "15141234567",
  "email": "villa@wellington.com",
  "price_range": 2,
  "address": {
    "street_address": "123 Wellington St.",
    "city": "Montreal",
    "postal_code": "H3G264"
  }
}
```

**Required Fields:**
- user_id: integer, must exist in users table
- name: string, non-empty
- phone: string, non-empty
- email: string, valid email format
- price_range: integer, must be 1-3
- address.street_address: string, non-empty
- address.city: string, non-empty
- address.postal_code: string, non-empty

#### Success Response (201 Created)

```json
{
  "message": "Success",
  "data": {
    "id": 9,
    "name": "Villa Wellington",
    "phone": "15141234567",
    "email": "villa@wellington.com",
    "user_id": 2,
    "price_range": 2,
    "address": {
      "id": 21,
      "street_address": "123 Wellington St.",
      "city": "Montreal",
      "postal_code": "H3G264"
    }
  }
}
```

**Response Fields:**
- message: "Success" on successful creation
- data: Created restaurant object
- data.id: Generated restaurant ID
- data.address.id: Generated address ID
- All request fields reflected in response

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenarios:**
- Missing required field
- Invalid email format
- price_range outside 1-3
- price_range not numeric
- Empty string for name, phone, email, or address fields
- user_id doesn't exist (referential constraint)

---

## 📊 Data Used or Modified

### Request Data Structure

```java
public class CreateRestaurantRequest {
    private int user_id;
    private String name;
    private String phone;
    private String email;
    private int price_range;  // 1-3
    private AddressRequest address;
}

public class AddressRequest {
    private String street_address;
    private String city;
    private String postal_code;
}
```

### SQL Operations

#### Create Address
```sql
INSERT INTO addresses (street_address, city, postal_code) 
VALUES (?1, ?2, ?3)
```

#### Create Restaurant
```sql
INSERT INTO restaurants (user_id, name, phone, email, price_range, address_id) 
VALUES (?1, ?2, ?3, ?4, ?5, ?6)
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

- **Request Format:** JSON only
- **Status Code:** 201 Created on success
- **Address Foreign Key:** address_id in restaurants table
- **user_id Foreign Key:** Must reference existing user
- **price_range Validation:** Integer between 1 and 3
- **Email Format:** Valid email format required
- **Transaction:** Both address and restaurant created or both fail (atomic)
- **ID Generation:** Auto-generated by database

---

## ✅ Acceptance Criteria

### Creation Success
- [ ] POST /api/restaurants endpoint exists
- [ ] Valid request returns 201 Created
- [ ] Response includes message: "Success"
- [ ] Response includes data field with restaurant
- [ ] Restaurant has generated id field
- [ ] Address has generated id field
- [ ] All request fields in response
- [ ] price_range is numeric

### Validation
- [ ] Missing user_id returns 400 Bad Request
- [ ] Missing name returns 400 Bad Request
- [ ] Missing phone returns 400 Bad Request
- [ ] Missing email returns 400 Bad Request
- [ ] Missing price_range returns 400 Bad Request
- [ ] Missing address fields returns 400 Bad Request
- [ ] Invalid email format returns 400 Bad Request
- [ ] price_range outside 1-3 returns 400 Bad Request
- [ ] Empty string fields return 400 Bad Request
- [ ] Non-existent user_id returns 400 Bad Request

### Address Handling
- [ ] Address created and assigned ID
- [ ] Address linked to restaurant
- [ ] All address fields returned
- [ ] street_address field required
- [ ] city field required
- [ ] postal_code field required

---

## 📝 Notes for the AI

- **Atomic Transaction:** Address and restaurant must be created together. If either fails, both fail.
- **price_range Validation:** Enforce 1-3 range in service layer
- **Email Validation:** Use Spring validation annotations @Email
- **Foreign Key:** Validate user_id exists before creating restaurant
- **Response Format:** Include generated IDs from both addresses and restaurants tables
- **Status Code:** Use 201 Created specifically for POST creation (not 200)
