# 🤖 AI_FEATURE_Orders Create API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Orders Create API - New Order Registration
- **Related Area:** Backend / API / Module 12

---

## 🎪 Feature Goal

Implement a POST endpoint that creates a new order with associated products. This allows customers to place orders from restaurants, creating the order record and linking selected products with quantities.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Create new order for customer at restaurant
- Associate multiple products with quantities
- Store product selections and quantities
- HTTP status code 200 OK
- Return created order with detailed nested response (same as GET /api/orders)
- Validate required fields
- Calculate total cost from products and quantities

### ❌ Out of Scope (Excluded)

- Assigning courier to order
- Payment processing
- Order confirmation emails
- Inventory management
- Promotion/discount handling

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Request Body:** restaurant_id, customer_id, products array
- **Products Format:** Array of {id, quantity} objects
- **Field Validation:** All required, positive integers
- **Database Operations:** Create order, create product_order records
- **Cost Calculation:** Sum product costs × quantities
- **Response Format:** Complete order with nested products (same as GET response)
- **Error Handling:** 400 for invalid/missing parameters

---

## 👥 User Flow / Logic (High Level)

### Order Creation Success
1. Customer sends POST /api/orders with restaurant, customer, and products
2. Controller validates request body
3. Service validates all fields present and valid
4. Service validates customer_id, restaurant_id, all product ids exist
5. Service calculates order total_cost
6. Service creates Order record in database
7. Service creates ProductOrder records for each product
8. Database assigns ID to order
9. Service retrieves complete order with details
10. Response: 200 OK with complete order data (same structure as GET)

### Validation Failure
1. Customer sends POST /api/orders with missing/invalid data
2. Controller validates request body
3. Validation fails
4. Response: 400 Bad Request with error message

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 POST /api/orders - Create Order

#### Request

**Method:** POST
**Path:** /api/orders
**Content-Type:** application/json

**Body:**
```json
{
  "restaurant_id": 1,
  "customer_id": 3,
  "products": [
    {
      "id": 2,
      "quantity": 1
    },
    {
      "id": 3,
      "quantity": 3
    }
  ]
}
```

**Required Fields:**
- restaurant_id: integer, must exist in restaurants table
- customer_id: integer, must exist in users table (customer)
- products: array of product selections
  - id: product ID (integer), must exist in products table
  - quantity: order quantity (integer), must be positive

**Constraints:**
- At least one product required
- All product IDs must belong to specified restaurant
- All IDs must be positive integers

#### Success Response (200 OK)

```json
{
  "id": 15,
  "customer_id": 3,
  "customer_name": "John Doe",
  "customer_address": "123 Main St, City, State 12345",
  "restaurant_id": 1,
  "restaurant_name": "Fast Pub",
  "restaurant_address": "5398 Quigley Harbor, North Lynelle, 60808",
  "courier_id": null,
  "courier_name": null,
  "status": "pending",
  "products": [
    {
      "product_id": 2,
      "product_name": "Hamburger",
      "quantity": 1,
      "unit_cost": 850,
      "total_cost": 850
    },
    {
      "product_id": 3,
      "product_name": "Fries",
      "quantity": 3,
      "unit_cost": 350,
      "total_cost": 1050
    }
  ],
  "total_cost": 1900
}
```

**Response fields:**
- Same structure as GET /api/orders response
- id: Generated order ID
- status: Initially "pending" or empty
- courier_id and courier_name: null initially (no courier assigned)
- products: Complete product details from product_orders
- total_cost: Sum of all product totals

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenarios:**
- Missing restaurant_id
- Missing customer_id
- Missing products array
- Empty products array
- Non-numeric IDs
- Negative or zero quantity
- Non-existent restaurant_id
- Non-existent customer_id
- Product not found or doesn't belong to restaurant

---

## 📊 Data Used or Modified

### Request Data Structure

```java
public class CreateOrderRequest {
    private int restaurant_id;
    private int customer_id;
    private List<OrderProductRequest> products;
}

public class OrderProductRequest {
    private int id;           // Product ID
    private int quantity;      // Order quantity
}
```

### SQL Operations

#### Create Order
```sql
INSERT INTO orders (customer_id, restaurant_id, courier_id, status, total_cost)
VALUES (?1, ?2, NULL, 'pending', ?3)
```

#### Create Product Order
```sql
INSERT INTO product_orders (order_id, product_id, quantity)
VALUES (?1, ?2, ?3)
```

#### Get Complete Order (for response)
```sql
SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id,
       o.status, o.total_cost,
       c.name as customer_name, c.address as customer_address,
       r.name as restaurant_name, r.address as restaurant_address,
       co.name as courier_name
FROM orders o
JOIN users c ON o.customer_id = c.id
JOIN restaurants r ON o.restaurant_id = r.id
LEFT JOIN users co ON o.courier_id = co.id
WHERE o.id = ?1
```

#### Get Products for Order
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
    private Integer courier_id;      // null initially
    private String courier_name;     // null initially
    private String status;           // "pending"
    private List<ProductOrderDTO> products;
    private int total_cost;
}

public class ProductOrderDTO {
    private int product_id;
    private String product_name;
    private int quantity;
    private int unit_cost;
    private int total_cost;         // quantity × unit_cost
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **HTTP Method:** POST
- **Request Format:** JSON only
- **Status Code:** 200 OK (not 201 Created) per requirements
- **Initial Status:** "pending"
- **Initial Courier:** null (not assigned)
- **Response Format:** Complete order with nested products (same as GET)
- **Cost Format:** Integer in cents
- **Transactions:** Order and product_orders must be atomic (all or nothing)
- **Cost Calculation:** total_cost = Σ(product.cost × quantity)

---

## ✅ Acceptance Criteria

### Creation Success
- [ ] POST /api/orders endpoint exists
- [ ] Valid request returns 200 OK
- [ ] Response includes all order fields
- [ ] Order created in database
- [ ] ProductOrder records created for each product
- [ ] Order ID generated and returned
- [ ] Status set to "pending"
- [ ] Courier fields are null

### Response Format
- [ ] Response same structure as GET /api/orders
- [ ] Includes customer details
- [ ] Includes restaurant details
- [ ] Includes products array with all details
- [ ] Includes total_cost calculation
- [ ] courier_id and courier_name are null

### Validation
- [ ] Missing restaurant_id returns 400 Bad Request
- [ ] Missing customer_id returns 400 Bad Request
- [ ] Missing products array returns 400 Bad Request
- [ ] Empty products array returns 400 Bad Request
- [ ] Non-numeric IDs return 400 Bad Request
- [ ] Negative quantity returns 400 Bad Request
- [ ] Zero quantity returns 400 Bad Request
- [ ] Non-existent restaurant returns 400 Bad Request
- [ ] Non-existent customer returns 400 Bad Request
- [ ] Non-existent product returns 400 Bad Request

### Database State
- [ ] Order record created with all fields
- [ ] ProductOrder records created with correct quantities
- [ ] Total cost calculated correctly
- [ ] All records linked correctly
- [ ] Atomic operation (all or nothing)

### Cost Calculation
- [ ] total_cost per product = quantity × unit_cost
- [ ] order total_cost = Σ all product total_costs
- [ ] Calculation verified in response and database

---

## 📝 Notes for the AI

- **Response Status:** 200 OK (not 201) per Module 12 requirements
- **Atomic Creation:** Both order and product_orders must succeed together or fail completely
- **Response Format:** Use same DTO structure as GET /api/orders so response can be reused
- **Courier Initially Null:** New orders don't have courier assigned yet, so courier_id and courier_name are null
- **Cost Calculation:** Ensure total_cost = sum of (product.cost × quantity) for each product
- **Status:** Initially "pending"
- **Joins:** Response requires joining users (customer), restaurants, users (courier if present)
- **Product Validation:** Verify each product_id belongs to specified restaurant
- **Parameterized Queries:** Use ?1, ?2 parameters for all SQL operations
