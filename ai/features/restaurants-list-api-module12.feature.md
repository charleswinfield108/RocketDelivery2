# 🤖 AI_FEATURE_Restaurants List API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Restaurants List API - Filtered Retrieval
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a GET endpoint that returns a list of restaurants with optional filtering by rating (1-5) and price_range (1-3). Users can filter by one, both, or neither parameter to discover restaurants matching their preferences.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- List all restaurants without filters
- Filter restaurants by rating (optional, 1-5)
- Filter restaurants by price_range (optional, 1-3)
- Combine rating and price_range filters
- HTTP status code 200 OK
- Response as JSON array of restaurant objects
- Return restaurant id, name, price_range, and rating
- Handle cases with no matching results (empty array)
- Query parameter validation

### ❌ Out of Scope (Excluded)

- Pagination or limit/offset
- Sorting (beyond natural order)
- Search by restaurant name
- Filtering by location/address
- Detailed restaurant information (phone, email, address)
- Distance-based filtering

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Query Parameters:** Optional rating (1-5) and price_range (1-3)
- **Filter Logic:** AND condition (both filters if provided)
- **Response Format:** Array of restaurant objects
- **Validation:** Rating and price_range must be in valid ranges if provided
- **Empty Results:** Return 200 OK with empty array if no matches
- **Parameter Types:** Integers only

---

## 👥 User Flow / Logic (High Level)

### List All Restaurants
1. User sends GET request to /api/restaurants (no parameters)
2. Controller receives request
3. Service queries all restaurants from database
4. Service returns list of all restaurants
5. Response: 200 OK with array of restaurants

### Filter by Rating Only
1. User sends GET request to /api/restaurants?rating=5
2. Controller parses rating parameter
3. Service filters restaurants WHERE rating = 5
4. Service returns matching restaurants
5. Response: 200 OK with filtered array

### Filter by Price Range Only
1. User sends GET request to /api/restaurants?price_range=1
2. Controller parses price_range parameter
3. Service filters restaurants WHERE price_range = 1
4. Service returns matching restaurants
5. Response: 200 OK with filtered array

### Filter by Both Rating and Price Range
1. User sends GET request to /api/restaurants?rating=4&price_range=2
2. Controller parses both parameters
3. Service filters WHERE rating = 4 AND price_range = 2
4. Service returns matching restaurants
5. Response: 200 OK with filtered array

### No Results
1. User sends GET request with filters that match no restaurants
2. Service executes query
3. No rows returned
4. Response: 200 OK with empty array []

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 GET /api/restaurants - List with Optional Filters

#### Request

**Method:** GET
**Path:** /api/restaurants
**Query Parameters:**
- `rating` (optional): integer 1-5
- `price_range` (optional): integer 1-3

**Examples:**
- `/api/restaurants?rating=5&price_range=1`
- `/api/restaurants?rating=3`
- `/api/restaurants?price_range=1`
- `/api/restaurants`

#### Success Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "Villa Wellington",
    "price_range": 3,
    "rating": 4
  },
  {
    "id": 2,
    "name": "Fast Pub",
    "price_range": 2,
    "rating": 3
  }
]
```

**Empty Response (200 OK):**
```json
[]
```

#### Error Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenario:** Rating outside 1-5 or price_range outside 1-3

---

## 📊 Data Used or Modified

### SQL Queries

#### Get All Restaurants
```sql
SELECT id, name, price_range, rating 
FROM restaurants
```

#### Filter by Rating
```sql
SELECT id, name, price_range, rating 
FROM restaurants 
WHERE rating = ?1
```

#### Filter by Price Range
```sql
SELECT id, name, price_range, rating 
FROM restaurants 
WHERE price_range = ?1
```

#### Filter by Both
```sql
SELECT id, name, price_range, rating 
FROM restaurants 
WHERE rating = ?1 AND price_range = ?2
```

### Response Data Structure

```java
public class RestaurantDTO {
    private int id;
    private String name;
    private int price_range;    // 1-3
    private int rating;         // 1-5
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Query Parameters:** Optional, validated before use
- **Parameter Validation:** rating must be 1-5, price_range must be 1-3
- **Filter Logic:** AND when both provided
- **Query Method:** Spring Data JPA with @Query and parameterized SQL
- **Response Format:** JSON array
- **Status Code:** Always 200 OK (even if empty)

---

## ✅ Acceptance Criteria

### Basic Retrieval
- [ ] GET /api/restaurants endpoint exists
- [ ] Returns 200 OK status
- [ ] Response is JSON array
- [ ] No parameters returns all restaurants

### Response Format
- [ ] Each restaurant includes id field
- [ ] Each restaurant includes name field
- [ ] Each restaurant includes price_range field
- [ ] Each restaurant includes rating field
- [ ] No additional fields in response

### Rating Filter
- [ ] rating=1 returns only restaurants with rating 1
- [ ] rating=5 returns only restaurants with rating 5
- [ ] rating outside 1-5 returns 400 Bad Request
- [ ] rating=0 returns 400 Bad Request
- [ ] rating=6 returns 400 Bad Request

### Price Range Filter
- [ ] price_range=1 returns only restaurants with price_range 1
- [ ] price_range=3 returns only restaurants with price_range 3
- [ ] price_range outside 1-3 returns 400 Bad Request
- [ ] price_range=0 returns 400 Bad Request
- [ ] price_range=4 returns 400 Bad Request

### Combined Filters
- [ ] rating=3&price_range=2 returns matching restaurants
- [ ] Filter combination uses AND logic
- [ ] Returns empty array if no matches (200 OK)

### Error Handling
- [ ] Invalid rating value returns 400 Bad Request
- [ ] Invalid price_range value returns 400 Bad Request
- [ ] Non-numeric values return 400 Bad Request

---

## 📝 Notes for the AI

- **AND Logic:** When both filters provided, use AND condition (WHERE rating = ? AND price_range = ?)
- **Empty Array:** Empty results should return 200 OK with [], NOT 404
- **Parameter Validation:** Validate before database query
- **SQL Injection:** Use parameterized queries (@Query with ?1, ?2)
- **Response Simplicity:** Only include id, name, price_range, rating (no address, phone, email)
