# 🤖 AI_FEATURE_Orders List API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Orders List API - Filtered Order Retrieval
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a GET endpoint that returns a list of orders filtered by user type (customer, restaurant, or courier) and user ID. This allows clients to retrieve orders relevant to a specific user in their role.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- List orders filtered by user type and ID
- Supported user types: customer, restaurant, courier
- Return complete order information with nested products
- Include order details: id, customer info, restaurant info, courier info, products, total
- Include product details: id, name, quantity, unit cost, total cost
- HTTP status codes (200 OK, 400 Bad Request)
- Query parameter validation (both type and id required)
- Return detailed nested data structure

### ❌ Out of Scope (Excluded)

- Pagination or limit/offset
- Sorting options
- Filtering by status
- Filtering by date range
- Filtering by price range

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Query Parameters:** Required 'type' and 'id'
- **Type Validation:** Must be 'customer', 'restaurant', or 'courier'
- **ID Validation:** Must be numeric, positive
- **Database Query:** Different WHERE clauses based on type
- **Response Format:** Array of detailed order objects with nested products
- **Cost Fields:** Integer in cents (e.g., 525 = $5.25)
- **Status Field:** Lowercase string (e.g., "in progress")
- **Error Handling:** 400 for invalid parameters

---

## 👥 User Flow / Logic (High Level)

### Get Customer Orders
1. User sends GET /api/orders?type=customer&id=7
2. Controller validates type is 'customer'
3. Controller validates id is numeric
4. Service queries orders WHERE customer_id = 7
5. Service retrieves all product details for each order
6. Response: 200 OK with array of orders

### Get Restaurant Orders
1. User sends GET /api/orders?type=restaurant&id=1
2. Service queries orders WHERE restaurant_id = 1
3. Orders returned with associated customers and couriers
4. Response: 200 OK with matching orders

### Get Courier Orders
1. User sends GET /api/orders?type=courier&id=3
2. Service queries orders WHERE courier_id = 3
3. Response: 200 OK with courier's assigned orders

### Missing or Invalid Parameters
1. User sends GET /api/orders (missing parameters)
2. Validation fails
3. Response: 400 Bad Request
4. Or sends invalid type (not customer/restaurant/courier)
5. Response: 400 Bad Request

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 GET /api/orders - List Orders by User Type and ID

#### Request

**Method:** GET
**Path:** /api/orders
**Query Parameters:**
- `type` (required): "customer", "restaurant", or "courier"
- `id` (required): User ID for given type (integer)

**Examples:**
- `/api/orders?type=customer&id=7`
- `/api/orders?type=restaurant&id=1`
- `/api/orders?type=courier&id=3`

#### Success Response (200 OK)

```json
[
  {
    "id": 3,
    "customer_id": 5,
    "customer_name": "Cathy Spinka",
    "customer_address": "7757 Darwin Causeway, Gerlachfort, 19822",
    "restaurant_id": 1,
    "restaurant_name": "Fast Pub",
    "restaurant_address": "5398 Quigley Harbor, North Lynelle, 60808",
    "courier_id": 3,
    "courier_name": "Cathy Spinka",
    "status": "in progress",
    "products": [
      {
        "product_id": 2,
        "product_name": "Vegetable Soup",
        "quantity": 2,
        "unit_cost": 1975,
        "total_cost": 3950
      },
      {
        "product_id": 4,
        "product_name": "Peking Duck",
        "quantity": 1,
        "unit_cost": 175,
        "total_cost": 175
      },
      {
        "product_id": 6,
        "product_name": "Pasta Carbonara",
        "quantity": 2,
        "unit_cost": 925,
        "total_cost": 1850
      }
    ],
    "total_cost": 5975
  },
  {
    "id": 13,
    "customer_id": 5,
    "customer_name": "Cathy Spinka",
    "customer_address": "7757 Darwin Causeway, Gerlachfort, 19822",
    "restaurant_id": 4,
    "restaurant_name": "Silver Grill",
    "restaurant_address": "5515 Sol Inlet, Shelbyfurt, 49433-4387",
    "courier_id": 5,
    "courier_name": "Rev. Lavina Cartwright",
    "status": "delivered",
    "products": [
      {
        "product_id": 22,
        "product_name": "Pappardelle alla Bolognese",
        "quantity": 1,
        "unit_cost": 1274,
        "total_cost": 1274
      },
      {
        "product_id": 20,
        "product_name": "French Fries with Sausages",
        "quantity": 1,
        "unit_cost": 1624,
        "total_cost": 1624
      }
    ],
    "total_cost": 2898
  }
]
```

**Empty Response (200 OK):**
```json
[]
```

**Response Fields:**
- id: Order ID
- customer_id: Customer user ID
- customer_name: Customer full name
- customer_address: Customer address string
- restaurant_id: Restaurant ID
- restaurant_name: Restaurant name
- restaurant_address: Restaurant address string
- courier_id: Courier user ID
- courier_name: Courier full name
- status: Order status (lowercase, e.g., "in progress")
- products: Array of product order details
  - product_id: Product ID
  - product_name: Product name
  - quantity: Quantity ordered
  - unit_cost: Cost per unit in cents
  - total_cost: quantity × unit_cost in cents
- total_cost: Sum of all product total_costs

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenarios:**
- Missing type parameter
- Missing id parameter
- Invalid type (not customer/restaurant/courier)
- Non-numeric id
- Negative or zero id

---

## 📊 Data Used or Modified

### SQL Query Examples

#### Orders by Customer
```sql
SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id, 
       o.status, o.total_cost,
       c.name as customer_name, c.address as customer_address,
       r.name as restaurant_name, r.address as restaurant_address,
       co.name as courier_name
FROM orders o
JOIN users c ON o.customer_id = c.id
JOIN restaurants r ON o.restaurant_id = r.id
JOIN users co ON o.courier_id = co.id
WHERE o.customer_id = ?1
```

#### Products for Order
```sql
SELECT p.id as product_id, p.name as product_name, 
       po.quantity, p.cost as unit_cost,
       (po.quantity * p.cost) as total_cost
FROM product_orders po
JOIN products p ON po.product_id = p.id
WHERE po.order_id = ?1
```

### Response Data Structure

```java
public class OrderDTO {
    private int id;
    private int customer_id;
    private String customer_name;
    private String customer_address;
    private int restaurant_id;
    private String restaurant_name;
    private String restaurant_address;
    private int courier_id;
    private String courier_name;
    private String status;
    private List<ProductOrderDTO> products;
    private int total_cost;
}

public class ProductOrderDTO {
    private int product_id;
    private String product_name;
    private int quantity;
    private int unit_cost;
    private int total_cost;
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Query Parameters:** Both type and id required
- **Type Validation:** Must be exactly "customer", "restaurant", or "courier"
- **ID Type:** Integer, must be positive
- **Cost Format:** Integer in cents
- **Quantity:** Integer
- **Status:** Lowercase string
- **Response Format:** JSON array (may be empty)
- **SQL:** Parameterized queries to prevent injection
- **Joins:** Multiple table joins to gather complete data

---

## ✅ Acceptance Criteria

### Basic Retrieval
- [ ] GET /api/orders endpoint exists
- [ ] Valid type and id returns 200 OK
- [ ] Response is JSON array
- [ ] Returns matching orders

### Parameter Validation
- [ ] Missing type parameter returns 400 Bad Request
- [ ] Missing id parameter returns 400 Bad Request
- [ ] Invalid type returns 400 Bad Request
- [ ] Non-numeric id returns 400 Bad Request
- [ ] Negative id returns 400 Bad Request
- [ ] Zero id returns 400 Bad Request

### Customer Filter
- [ ] type=customer returns orders for that customer
- [ ] Returns customer details correctly
- [ ] Returns restaurant and courier details
- [ ] No other customers' orders returned

### Restaurant Filter
- [ ] type=restaurant returns orders for that restaurant
- [ ] Returns restaurant details
- [ ] Returns customer and courier details
- [ ] No other restaurants' orders returned

### Courier Filter
- [ ] type=courier returns orders assigned to courier
- [ ] Returns courier details
- [ ] Returns customer and restaurant details

### Response Content
- [ ] Each order includes all required fields
- [ ] Products nested within order
- [ ] Each product includes product_id, product_name, quantity, unit_cost, total_cost
- [ ] total_cost is sum of all product totals
- [ ] Status field is lowercase
- [ ] Addresses are complete strings

### Empty Results
- [ ] No matching orders returns 200 OK with []
- [ ] Empty array returned (not null)
- [ ] No error message

---

## 📝 Notes for the AI

- **Complex Joins:** Requires joining users (customer), restaurants, users (courier), products, product_orders tables
- **Cost Calculations:** total_cost per product = quantity × unit_cost
- **Order total_cost:** Sum of all product totals
- **Address Strings:** Full address as single string field (not split)
- **Nested Products:** Products array must be populated for each order with all details
- **Status Lowercase:** Status field should be lowercase (e.g., "in progress" not "IN_PROGRESS")
- **No Filtering:** At this stage, just filter by type and id. Accept any id value (no validation if user exists).
- **Parameterized Queries:** Use ?1, ?2 parameters to prevent SQL injection
