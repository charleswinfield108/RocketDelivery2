# 🤖 AI_FEATURE_Products List API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Products List API - Filtered Product Retrieval
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a GET endpoint that returns a list of products for a specific restaurant. Clients can request all products from a particular restaurant to display menu items.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- List all products for a given restaurant
- Filter by restaurant ID (required query parameter)
- Return product id, name, and cost
- HTTP status codes (200 OK, 404 Not Found, 400 Bad Request)
- Query parameter validation
- Handle cases with no products (empty array)

### ❌ Out of Scope (Excluded)

- Filtering by product category
- Price range filtering
- Availability status
- Product descriptions
- Sorting options

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Query Parameter:** Required restaurant ID
- **Parameter Validation:** Must be numeric, positive
- **Database Query:** Parameterized SQL with restaurant filter
- **Response Format:** Array of product objects
- **Error Handling:** 404 if restaurant not found, 400 for invalid parameters
- **Empty Results:** Return 200 OK with empty array

---

## 👥 User Flow / Logic (High Level)

### Successful Product List Retrieval
1. User sends GET /api/products?restaurant=5
2. Controller parses restaurant parameter
3. Service validates restaurant ID exists
4. Restaurant found
5. Service queries products for restaurant
6. Service returns product list (may be empty)
7. Response: 200 OK with product array

### Restaurant Not Found
1. User sends GET /api/products?restaurant=999
2. Controller parses restaurant parameter
3. Service queries for restaurant
4. Restaurant not found
5. Response: 404 Not Found

### Missing Parameter
1. User sends GET /api/products (no query parameter)
2. Controller validates parameters
3. Parameter validation fails
4. Response: 400 Bad Request

### No Products for Restaurant
1. User sends GET /api/products?restaurant=7
2. Restaurant exists but has no products
3. Service returns empty product list
4. Response: 200 OK with empty array []

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 GET /api/products - List Products by Restaurant

#### Request

**Method:** GET
**Path:** /api/products
**Query Parameters:**
- `restaurant` (required): restaurant ID (integer)

**Examples:**
- `/api/products?restaurant=5`
- `/api/products?restaurant=1`

#### Success Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "Cheeseburger",
    "cost": 525
  },
  {
    "id": 2,
    "name": "French Fries",
    "cost": 250
  },
  {
    "id": 3,
    "name": "Milkshake",
    "cost": 350
  }
]
```

**Empty Response (200 OK):**
```json
[]
```

#### Not Found Response (404 Not Found)

```json
{
  "error": "Resource not found",
  "details": "Product with id <id> not found"
}
```

**Scenario:** Restaurant with ID does not exist

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenarios:**
- Missing restaurant query parameter
- Non-numeric restaurant ID
- Negative restaurant ID
- Zero restaurant ID

---

## 📊 Data Used or Modified

### SQL Query

#### Get Products by Restaurant
```sql
SELECT id, name, cost 
FROM products 
WHERE restaurant_id = ?1
```

### Verification Query (Check Restaurant Exists)
```sql
SELECT COUNT(*) 
FROM restaurants 
WHERE id = ?1
```

### Response Data Structure

```java
public class ProductDTO {
    private int id;
    private String name;
    private int cost;  // In cents (e.g., 525 = $5.25)
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Query Parameter:** Required, integer type, positive
- **Parameter Validation:** Validate before database queries
- **Cost Format:** Integer in cents (not decimal)
- **Response Format:** JSON array (not wrapped in object)
- **Empty Array:** Return 200 OK with [], not 404
- **SQL:** Parameterized @Query to prevent injection
- **Response Fields:** Only id, name, cost (no description, quantity, etc.)

---

## ✅ Acceptance Criteria

### Basic Retrieval
- [ ] GET /api/products endpoint exists
- [ ] Valid restaurant ID returns 200 OK
- [ ] Response is JSON array
- [ ] All products for restaurant returned

### Response Format
- [ ] Each product includes id field
- [ ] Each product includes name field
- [ ] Each product includes cost field
- [ ] No additional fields in response
- [ ] cost is integer type (cents)

### Multiple Products
- [ ] Single product returns array with 1 item
- [ ] Multiple products returns all items
- [ ] Product count matches database
- [ ] All products belong to specified restaurant

### Empty Results
- [ ] Restaurant with no products returns 200 OK
- [ ] Empty products returns empty array []
- [ ] No error message for empty results

### Restaurant Validation
- [ ] Non-existent restaurant returns 404 Not Found
- [ ] Error details includes restaurant reference
- [ ] Error message includes "Resource not found"

### Parameter Validation
- [ ] Missing restaurant parameter returns 400 Bad Request
- [ ] Non-numeric parameter returns 400 Bad Request
- [ ] Negative restaurant ID returns 400 Bad Request
- [ ] Zero restaurant ID returns 400 Bad Request

---

## 📝 Notes for the AI

- **Empty Array vs 404:** Important distinction:
  - Restaurant exists but no products: 200 OK with []
  - Restaurant doesn't exist: 404 Not Found
- **Cost Format:** Store cost in cents as integer (525 = $5.25), not as decimal
- **Restaurant Validation:** Verify restaurant exists before or alongside product query
- **Parameterized SQL:** Use @Query("SELECT ... WHERE restaurant_id = ?1")
- **Array Response:** Return as JSON array directly, not wrapped in object
