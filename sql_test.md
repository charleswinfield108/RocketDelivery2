# 📋 SQL Queries Test Reference

**Generated:** April 2, 2026  
**Total Queries:** 20 Native SQL Queries  
**Repositories with Queries:** 6 of 11

---

## 🍽️ RestaurantRepository (7 Queries)

### Query 1: Find Restaurant with Average Rating by ID
**Type:** SELECT  
**Method:** `findRestaurantWithAverageRatingById()`  
**Purpose:** Get restaurant details with calculated average rating

**Java/Spring Version (in code):**
```sql
SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
FROM restaurants r
LEFT JOIN orders o ON r.id = o.restaurant_id
WHERE r.id = :restaurantId
GROUP BY r.id
```

**DBeaver Version (testing directly):**
```sql
SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
FROM restaurants r
LEFT JOIN orders o ON r.id = o.restaurant_id
WHERE r.id = 1
GROUP BY r.id
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID to lookup. Example: `1`

**Return Type:** `List<Object[]>` (restaurant_id, name, price_range, rating)

---

### Query 2: Find Restaurants by Rating and Price Range
**Type:** SELECT (with subquery)  
**Method:** `findRestaurantsByRatingAndPriceRange()`  
**Purpose:** Filter restaurants by rating (optional) and price_range (optional)

**Java/Spring Version (in code):**
```sql
SELECT * FROM (
SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
FROM restaurants r
LEFT JOIN orders o ON r.id = o.restaurant_id
WHERE (:priceRange IS NULL OR r.price_range = :priceRange)
GROUP BY r.id
) AS result
WHERE (:rating IS NULL OR result.rating = :rating)
```

**DBeaver Version (testing directly):**
```sql
-- Example 1: Filter by price_range only
SELECT * FROM (
SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
FROM restaurants r
LEFT JOIN orders o ON r.id = o.restaurant_id
WHERE (2 IS NULL OR r.price_range = 2)
GROUP BY r.id
) AS result
WHERE (NULL IS NULL OR result.rating = NULL)

-- Example 2: Filter by both rating and price_range
SELECT * FROM (
SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
FROM restaurants r
LEFT JOIN orders o ON r.id = o.restaurant_id
WHERE (2 IS NULL OR r.price_range = 2)
GROUP BY r.id
) AS result
WHERE (4 IS NULL OR result.rating = 4)
```

**Parameters:**
- `rating` (Integer, nullable) - Filter by rating. Pass NULL for no filter. Example: `4` or `NULL`
- `priceRange` (Integer, nullable) - Filter by price_range (1-3). Pass NULL for no filter. Example: `2` or `NULL`

**Return Type:** `List<Object[]>` (restaurant_id, name, price_range, rating)

---

### Query 3: Save New Restaurant
**Type:** INSERT  
**Method:** `saveRestaurant()`  
**Purpose:** Create a new restaurant record

**Java/Spring Version (in code):**
```sql
INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
VALUES (?1, ?2, ?3, ?4, ?5, ?6)
```

**DBeaver Version (testing directly):**
```sql
INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
VALUES (1, 5, 'Pizza Palace', 2, '5141234567', 'pizza@palace.com')
```

**Parameters:**
- `?1` / userId (int) - User ID who owns the restaurant. Example: `1`
- `?2` / addressId (int) - Address ID for the restaurant. ⚠️ Must exist and be unique. Example: `5`
- `?3` / name (string) - Restaurant name. Example: `'Pizza Palace'`
- `?4` / priceRange (int) - Price range (1=budget, 2=mid, 3=premium). ⚠️ Must be 1-3. Example: `2`
- `?5` / phone (string) - Phone number. Example: `'5141234567'`
- `?6` / email (string) - Email address. Example: `'pizza@palace.com'`

**Return Type:** void (no data returned)  
**Constraints:** See address_id foreign key, unique, and price_range check constraints below

---

### Query 4: Update Restaurant Details
**Type:** UPDATE  
**Method:** `updateRestaurant()`  
**Purpose:** Update restaurant name, price_range, and phone

**Java/Spring Version (in code):**
```sql
UPDATE restaurants 
SET name = ?2, price_range = ?3, phone = ?4
WHERE id = ?1
```

**DBeaver Version (testing directly):**
```sql
UPDATE restaurants 
SET name = 'New Restaurant Name', price_range = 2, phone = '5551234567'
WHERE id = 1
```

**Parameters:** restaurantId, name, priceRange, phone  
**Return Type:** void

---

### Query 5: Find Restaurant by ID
**Type:** SELECT  
**Method:** `findRestaurantById()`  
**Purpose:** Retrieve full restaurant record by ID

**Java/Spring Version (in code):**
```sql
SELECT r.id, r.user_id, r.address_id, r.name, r.price_range, r.phone, r.email
FROM restaurants r
WHERE r.id = ?1
```

**DBeaver Version (testing directly):**
```sql
SELECT r.id, r.user_id, r.address_id, r.name, r.price_range, r.phone, r.email
FROM restaurants r
WHERE r.id = 1
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID to retrieve. Example: `1`

**Return Type:** `Optional<Restaurant>` (id, user_id, address_id, name, price_range, phone, email)

---

### Query 6: Get Last Inserted Restaurant ID
**Type:** SELECT  
**Method:** `getLastInsertedId()`  
**Purpose:** Retrieve the last auto-generated restaurant ID

**Java/Spring Version (in code):**
```sql
SELECT LAST_INSERT_ID() AS id
```

**DBeaver Version (testing directly):**
```sql
SELECT LAST_INSERT_ID() AS id
```

**Parameters:** None  
**Return Type:** int (auto-generated ID from previous INSERT)  
**Note:** Use immediately after Query 3 (INSERT) to get the new restaurant ID. MySQL auto-increment value

---

### Query 7: Delete Restaurant by ID
**Type:** DELETE  
**Method:** `deleteRestaurantById()`  
**Purpose:** Delete restaurant record (after cascade deletes of related records)

**Java/Spring Version (in code):**
```sql
DELETE FROM restaurants WHERE id = ?1
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM restaurants WHERE id = 5
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID to delete. Example: `5`

**Return Type:** void (no data returned)  
**Important:** Must delete associated ProductOrder and Order records first via cascade delete logic

---

## 📦 OrderRepository (6 Queries)

### Query 8: Find Orders by Restaurant ID
**Type:** SELECT  
**Method:** `findOrdersByRestaurantId()`  
**Purpose:** Get all orders for a specific restaurant

**Java/Spring Version (in code):**
```sql
SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
FROM orders o
WHERE o.restaurant_id = ?1
```

**DBeaver Version (testing directly):**
```sql
SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
FROM orders o
WHERE o.restaurant_id = 2
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID to filter orders. Example: `2`

**Return Type:** `List<Order>` (id, restaurant_id, customer_id, courier_id, status_id, restaurant_rating)

---

### Query 9: Find Orders by Customer ID
**Type:** SELECT  
**Method:** `findOrdersByCustomerId()`  
**Purpose:** Get all orders placed by a specific customer

**Java/Spring Version (in code):**
```sql
SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
FROM orders o
WHERE o.customer_id = ?1
```

**DBeaver Version (testing directly):**
```sql
SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
FROM orders o
WHERE o.customer_id = 5
```

**Parameters:**
- `customerId` (int) - Customer ID to filter orders. Example: `5`

**Return Type:** `List<Order>` (id, restaurant_id, customer_id, courier_id, status_id, restaurant_rating)

---

### Query 10: Find Orders by Courier ID
**Type:** SELECT  
**Method:** `findOrdersByCourierId()`  
**Purpose:** Get all orders assigned to a specific courier

**Java/Spring Version (in code):**
```sql
SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
FROM orders o
WHERE o.courier_id = ?1
```

**DBeaver Version (testing directly):**
```sql
SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
FROM orders o
WHERE o.courier_id = 3
```

**Parameters:**
- `courierId` (int) - Courier ID to filter orders. Example: `3`

**Return Type:** `List<Order>` (id, restaurant_id, customer_id, courier_id, status_id, restaurant_rating)

---

### Query 11: Delete ProductOrders by Order ID
**Type:** DELETE  
**Method:** `deleteProductOrdersByOrderId()`  
**Purpose:** Delete all product items from an order (cascade delete)

**Java/Spring Version (in code):**
```sql
DELETE FROM product_orders WHERE order_id = ?1
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM product_orders WHERE order_id = 10
```

**Parameters:**
- `orderId` (int) - Order ID whose product items to delete. Example: `10`

**Return Type:** void (no data returned)  
**Important:** Must be called before deleting the order itself

---

### Query 12: Delete Order by ID
**Type:** DELETE  
**Method:** `deleteOrderById()`  
**Purpose:** Delete an order record

**Java/Spring Version (in code):**
```sql
DELETE FROM orders WHERE id = ?1
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM orders WHERE id = 8
```

**Parameters:**
- `orderId` (int) - Order ID to delete. Example: `8`

**Return Type:** void (no data returned)  
**Important:** Call Query 11 first to delete associated product_orders, then this query to delete the order

---

### Query 13: Delete Orders by Restaurant ID
**Type:** DELETE  
**Method:** `deleteByRestaurantId()`  
**Purpose:** Delete all orders for a restaurant (cascade delete)

**Java/Spring Version (in code):**
```sql
DELETE FROM orders WHERE restaurant_id = ?1
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM orders WHERE restaurant_id = 3
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID whose orders to delete. Example: `3`

**Return Type:** void (no data returned)  
**Important:** Respects cascade delete logic - deletes all orders associated with a restaurant. Note: Product_orders should be deleted separately first (Query 11)

---

## 🛒 ProductRepository (2 Queries)

### Query 14: Find Products by Restaurant ID
**Type:** SELECT  
**Method:** `findProductsByRestaurantId()`  
**Purpose:** Get all products for a specific restaurant, ordered by ID

**Java/Spring Version (in code):**
```sql
SELECT p.id, p.restaurant_id, p.name, p.description, p.cost
FROM products p
WHERE p.restaurant_id = ?1
ORDER BY p.id ASC
```

**DBeaver Version (testing directly):**
```sql
SELECT p.id, p.restaurant_id, p.name, p.description, p.cost
FROM products p
WHERE p.restaurant_id = 4
ORDER BY p.id ASC
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID to get products for. Example: `4`

**Return Type:** `List<Product>` (id, restaurant_id, name, description, cost)  
**Note:** Results ordered by ID ascending (oldest first)

---

### Query 15: Delete Products by Restaurant ID
**Type:** DELETE  
**Method:** `deleteProductsByRestaurantId()`  
**Purpose:** Delete all products for a restaurant (cascade delete)

**Java/Spring Version (in code):**
```sql
DELETE FROM products WHERE restaurant_id = ?1
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM products WHERE restaurant_id = 3
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID whose products to delete. Example: `3`

**Return Type:** int (count of deleted rows)  
**Important:** Respects cascade delete - removes all products from a restaurant

---

## 🔗 ProductOrderRepository (2 Queries)

### Query 16: Delete ProductOrders by Order ID
**Type:** DELETE  
**Method:** `deleteProductOrdersByOrderId()`  
**Purpose:** Delete all product-order associations for an order

**Java/Spring Version (in code):**
```sql
DELETE FROM product_orders WHERE order_id = ?1
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM product_orders WHERE order_id = 10
```

**Parameters:**
- `orderId` (int) - Order ID whose product items to delete. Example: `10`

**Return Type:** void (no data returned)  
**Note:** Removes product items from an order (must be done before deleting the order itself)

---

### Query 17: Delete ProductOrders by Restaurant ID
**Type:** DELETE (with subquery)  
**Method:** `deleteProductOrdersByRestaurant()`  
**Purpose:** Delete all product-order associations for a restaurant

**Java/Spring Version (in code):**
```sql
DELETE FROM product_orders WHERE order_id IN (
    SELECT id FROM orders WHERE restaurant_id = ?1
)
```

**DBeaver Version (testing directly):**
```sql
DELETE FROM product_orders WHERE order_id IN (
    SELECT id FROM orders WHERE restaurant_id = 3
)
```

**Parameters:**
- `restaurantId` (int) - Restaurant ID whose product items to delete (via orders). Example: `3`

**Return Type:** void (no data returned)  
**Note:** Subquery finds all order IDs for the restaurant, then deletes all product-order associations for those orders

---

## 🏠 AddressRepository (2 Queries)

### Query 18: Save New Address
**Type:** INSERT  
**Method:** `saveAddress()`  
**Purpose:** Create a new address record

**Java/Spring Version (in code):**
```sql
INSERT INTO addresses (street_address, city, postal_code)
VALUES (?1, ?2, ?3)
```

**DBeaver Version (testing directly):**
```sql
INSERT INTO addresses (street_address, city, postal_code)
VALUES ('123 Main Street', 'San Francisco', '94102')
```

**Parameters:**
- `streetAddress` (String) - Street address. Example: `'123 Main Street'`
- `city` (String) - City name. Example: `'San Francisco'`
- `postalCode` (String) - Postal/ZIP code. Example: `'94102'`

**Return Type:** void (no data returned)  
**Note:** Use Query 19 immediately after to retrieve the auto-generated address ID

---

### Query 19: Get Last Inserted Address ID
**Type:** SELECT  
**Method:** `getLastInsertedId()`  
**Purpose:** Retrieve the last auto-generated address ID

**Java/Spring Version (in code):**
```sql
SELECT LAST_INSERT_ID() AS id
```

**DBeaver Version (testing directly):**
```sql
SELECT LAST_INSERT_ID() AS id
```

**Parameters:** None  
**Return Type:** int (auto-generated ID from previous INSERT)  
**Note:** Use immediately after Query 18 (INSERT) to get the new address ID. MySQL auto-increment value

---

## 👤 UserRepository (1 Query)

### Query 20: Find User by ID
**Type:** SELECT  
**Method:** `findById()`  
**Purpose:** Get user record by ID

**Java/Spring Version (in code):**
```sql
SELECT id, email, password, name FROM users WHERE id = ?1
```

**DBeaver Version (testing directly):**
```sql
SELECT id, email, password, name FROM users WHERE id = 5
```

**Parameters:**
- `id` (int) - User ID to lookup. Example: `5`

**Return Type:** `Optional<UserEntity>` (optional user record with id, email, password, name)  
**Note:** Return type is Optional because user may not exist with given ID

---

## 📊 Query Summary

### By Type
| Type | Count | Percentage |
|------|-------|-----------|
| SELECT | 10 | 50% |
| DELETE | 7 | 35% |
| INSERT | 2 | 10% |
| UPDATE | 1 | 5% |
| **TOTAL** | **20** | **100%** |

### By Repository
| Repository | Count |
|---|---|
| RestaurantRepository | 7 |
| OrderRepository | 6 |
| ProductRepository | 2 |
| ProductOrderRepository | 2 |
| AddressRepository | 2 |
| UserRepository | 1 |
| **TOTAL** | **20** |

### Repositories WITHOUT Custom Queries
- CustomerRepository (uses JPA defaults)
- CourierRepository (uses JPA defaults)
- EmployeeRepository (uses JPA defaults)
- OrderStatusRepository (uses JPA defaults)
- CourierStatusRepository (uses JPA defaults)

---

## 🧪 Testing in DBeaver

### ⚠️ Important: Parameter Syntax Differences

**Java/Spring Syntax (in code):** `?1, ?2, :paramName` ← Used in repositories  
**MySQL/DBeaver Syntax:** Replace with actual values ← Use when testing directly

### How to Test Each Query Type:

**1. For Named Parameters (`:paramName`):**
Replace `:paramName` with actual values
```sql
-- WRONG (in DBeaver):
SELECT * FROM restaurants WHERE r.id = :restaurantId

-- CORRECT (in DBeaver):
SELECT * FROM restaurants WHERE id = 1
```

**2. For Positional Parameters (`?1, ?2, ?3`):**
Replace `?1`, `?2`, etc. with actual values
```sql
-- WRONG (in DBeaver):
INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
VALUES (?1, ?2, ?3, ?4, ?5, ?6)

-- CORRECT (in DBeaver):
INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
VALUES (1, 2, 'Pizza Palace', 2, '5141234567', 'pizza@palace.com')
```

**3. For NULL checks:**
Use literal NULL values
```sql
-- Test rating filter with NULL:
SELECT * FROM (
  SELECT r.id, r.name, r.price_range, 
         COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
  FROM restaurants r
  LEFT JOIN orders o ON r.id = o.restaurant_id
  GROUP BY r.id
) AS result
WHERE (NULL IS NULL OR result.rating = NULL)  -- NULL passthrough
```

### Example Test Values for DBeaver:
- Restaurant ID: `1`
- User ID: `1`
- Address ID: `1`
- Customer ID: `5`
- Courier ID: `3`
- Order ID: `10`
- Rating: `4`
- Price Range: `2` ⚠️ **MUST be 1, 2, or 3 (CHECK constraint)**
- Name: `'Pizza Palace'`
- Phone: `'5141234567'`
- Email: `'pizza@palace.com'`

### ⚠️ Database Constraints

**Address ID FOREIGN KEY Constraint:**
```
FOREIGN KEY (address_id) REFERENCES addresses (id) ON DELETE CASCADE
```
- **address_id MUST exist in the addresses table**
- If you get error `[1452] [23000]: Cannot add or update a child row`, the address doesn't exist
- **Solution:** Either use an existing address_id, or create a new address first

```sql
-- 1. See all available addresses:
SELECT id, street_address, city, postal_code FROM addresses;

-- 2. OR create a new address:
INSERT INTO addresses (street_address, city, postal_code)
VALUES ('789 Queen St', 'Montreal', 'H3C3C3');

-- 3. Get the new address ID:
SELECT LAST_INSERT_ID();

-- 4. Now use that ID in your restaurant insert:
INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
VALUES (1, <new_address_id>, 'Restaurant Name', 2, '5551234567', 'email@email.com');
```

**Address ID UNIQUE Constraint:**
```
UNIQUE KEY (address_id)
```
- Each restaurant must have a **unique address_id**
- **No two restaurants can share the same address**
- If you get error `[1062] [23000]: Duplicate entry`, that address is already assigned
- **Solution:** Use a different address_id, or create a new address first

**Price Range CHECK Constraint:**
```sql
CHECK (price_range BETWEEN 1 AND 3)
```
- `1` = Budget/Economy
- `2` = Mid-Range  
- `3` = Premium/Fine Dining
- **Any other value will be rejected!**

### ⚠️ Important Note on Restaurant Ratings
- **Restaurants table does NOT have a `rating` column**
- **Rating is calculated dynamically** from the orders table
- Queries 1 & 2 calculate: `CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0))`
- This averages all customer ratings from the orders table and rounds up
- If a restaurant has no orders, the rating defaults to 0 via `COALESCE()`

---

## 📝 Notes

- All queries use **parameterized bindings** to prevent SQL injection
- **DELETE operations** often require cascade delete logic (related records deleted first)
- **SELECT with JOIN** operations use `LEFT JOIN` to include restaurants without orders
- **GROUP BY** used for aggregation (rating calculations)
- **COALESCE** and **CEIL** functions used for rating calculations
- **LAST_INSERT_ID()** retrieves auto-generated IDs after INSERT operations
