# 🤖🛠️ AI Feature Specification - Order API Test Coverage

**Feature ID:** ORDER-TEST  
**Priority:** High  
**Status:** In Development  
**Release Version:** Module 12  
**Last Updated:** April 1, 2026

---

## 📋 1. Feature Goal & Scope

### Feature Goal
Implement comprehensive automated test coverage for the Orders API GET and POST endpoints using Test-Driven Development (TDD) methodology with JUnit 5 and MockMvc. Ensure all order retrieval and creation operations are thoroughly tested with both success and failure scenarios, preventing regressions and maintaining API reliability across the entire order management lifecycle.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Unit tests for OrderService methods (create, retrieve)
- Integration tests for OrderApiController GET endpoint
- Integration tests for OrderApiController POST endpoint
- Request validation testing (missing fields, invalid IDs, price mismatches)
- Response validation testing (correct format, all fields including products)
- HTTP status code validation (200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Error)
- Database persistence verification (orders and product_orders saved correctly)
- Exception handling testing (validation errors, non-existent references, price validation)
- Complex order scenarios (multiple products, different quantities, price aggregation)
- Price validation (totalPrice matches sum of product prices × quantities)
- ProductOrder junction table creation and association
- MockMvc setup and configuration
- Test fixtures and data builders
- Parameterized tests for multiple scenarios
- Edge case coverage (empty arrays, boundary values, zero quantities)

### ❌ Out of Scope (Excluded)

- Testing Authentication/Authorization (assume authenticated)
- Testing Order Status Update endpoint (separate test class)
- Performance or load testing
- Test coverage for DELETE endpoint
- UI or frontend testing
- Integration with external payment services
- Real-time order tracking testing
- Database migration testing
- Deployment or CI/CD testing
- Manual test procedures

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **GET Endpoint Tests:** Write JUnit 5 tests for order retrieval (all orders, filtered orders)
- **GET Success Tests:** Test retrieving orders with various filter combinations
- **GET Failure Tests:** Test invalid filters, non-existent references, malformed parameters
- **POST Endpoint Tests:** Write JUnit 5 tests for order creation with valid and invalid data
- **POST Success Tests:** Test creating orders with single/multiple products, price validation
- **POST Failure Tests:** Test missing fields, invalid references, price mismatches
- **Request Validation Tests:** Test missing required fields, invalid formats, boundary values
- **Response Validation Tests:** Verify response format, status codes, product inclusions, timestamps
- **MockMvc Setup:** Configure MockMvc for controller testing
- **Service Mocking:** Mock repository and service dependencies
- **Database Persistence Tests:** Verify orders and ProductOrder records saved correctly
- **Error Handling Tests:** Test error responses and exception handling
- **Price Calculation Tests:** Verify totalPrice validation and correctness
- **ProductOrder Creation Tests:** Verify junction table records created for each product
- **Test Data:** Create fixtures and builders for complex order scenarios
- **Parameterized Tests:** Use @ParameterizedTest for multiple input scenarios

---

## 👥 User Flow / Logic (High Level)

### GET Orders Test Execution Flow
1. Test class loads Spring context with @SpringBootTest
2. MockMvc is configured to test OrderApiController
3. Test setup creates test orders in database (fixtures)
4. Test case sends GET request to /api/orders (no parameters or with filters)
5. MockMvc captures HTTP request and response
6. Test verifies response status code (200 for success, 400 for invalid filter)
7. Test verifies response format using JsonPath assertions
8. Test verifies all orders/filtered orders are returned
9. Test verifies each order includes all required fields
10. Test verifies products array is populated for each order
11. Test verifies timestamps are in ISO 8601 format
12. Test verifies empty results return 200 with empty array (not error)

### POST Order Success Test Execution Flow
1. Test class loads Spring context and MockMvc
2. Test setup creates test customer, restaurant, and products in database
3. Test case creates request with valid order data:
   - customerId (valid, exists)
   - restaurantId (valid, exists)
   - products array with quantities
   - totalPrice (matches sum of products × quantities)
4. Test sends POST request to /api/orders with JSON body
5. MockMvc captures HTTP request and response
6. Test verifies response status code (201 Created)
7. Test verifies response format (ApiResponseDTO with data)
8. Test verifies order data in response:
   - Auto-generated ID is present
   - Status is PENDING
   - All products are included
   - Total price matches request
   - Timestamps are set
9. Test verifies order is persisted to database
10. Test verifies ProductOrder records are created for each product
11. Test verifies order is queryable via GET /api/orders

### POST Order Failure Test Execution Flow
1. Test class loads Spring context
2. Test case creates request with invalid data:
   - Missing customerId, restaurantId, products, or totalPrice
   - Non-existent customer or restaurant ID
   - Product prices don't match
   - Empty products array
   - Negative or zero quantities
3. Test sends POST request with invalid data
4. MockMvc captures response
5. Test verifies response status code (400 Bad Request or 404 Not Found)
6. Test verifies error message indicates problem
7. Test verifies no order is created in database
8. Test verifies no ProductOrder records are created

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Testing Interfaces (JUnit 5 + MockMvc)

#### OrderApiControllerTest Class Structure
```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired OrderRepository orderRepository;
    @Autowired ProductOrderRepository productOrderRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired RestaurantRepository restaurantRepository;
    @Autowired ProductRepository productRepository;
    // Test methods...
}
```

#### GET /api/orders Test Cases (Success)
- `testGetAllOrders()` — Retrieve all orders
- `testGetAllOrdersEmptyDatabase()` — No orders returns empty array with 200
- `testGetOrdersWithMultipleRecords()` — Multiple orders returned correctly
- `testGetOrdersIncludesProducts()` — Each order includes product array
- `testGetOrdersResponseFormat()` — Verify response structure
- `testGetOrdersFilterByCustomer()` — Filter by customer_id parameter works
- `testGetOrdersFilterByRestaurant()` — Filter by restaurant_id parameter works
- `testGetOrdersFilterByStatus()` — Filter by status parameter works
- `testGetOrdersMultipleFilters()` — Multiple filters applied together
- `testGetOrdersIncludesAllRequiredFields()` — id, customerId, restaurantId, products, status, totalPrice
- `testGetOrdersTimestampsFormatted()` — Timestamps in ISO 8601 format
- `testGetOrdersProductDetails()` — Product array contains name, price, quantity

#### GET /api/orders Test Cases (Failure)
- `testGetOrdersInvalidCustomerFilter()` — Invalid customer ID returns 400
- `testGetOrdersInvalidRestaurantFilter()` — Invalid restaurant ID returns 400
- `testGetOrdersInvalidStatusFilter()` — Invalid status value returns 400
- `testGetOrdersNonExistentCustomerId()` — Non-existent customer returns 404
- `testGetOrdersNonExistentRestaurantId()` — Non-existent restaurant returns 404
- `testGetOrdersNegativeCustomerId()` — Negative customer ID returns 400
- `testGetOrdersNegativeRestaurantId()` — Negative restaurant ID returns 400
- `testGetOrdersNonNumericFilter()` — Non-numeric ID returns 400
- `testGetOrdersMissingFilter()` — Missing filter (if required) handled correctly
- `testGetOrdersDatabaseError()` — Database error returns 500

#### POST /api/orders Test Cases (Success)
- `testCreateOrderWithValidData()` — Create order with single product
- `testCreateOrderWithMultipleProducts()` — Create order with 2+ products
- `testCreateOrderResponseFormat()` — Verify response structure
- `testCreateOrderGeneratesId()` — Auto-generated order ID present
- `testCreateOrderInitialStatus()` — Status is PENDING
- `testCreateOrderIncludesAllProducts()` — All products in response
- `testCreateOrderCalculatesTotalPrice()` — Total price correct
- `testCreateOrderPersistence()` — Order saved to database
- `testCreateOrderProductOrderCreation()` — ProductOrder records created
- `testCreateOrderProductQuantities()` — Product quantities correct
- `testCreateOrderTimestamps()` — createdAt and updatedAt set
- `testCreateOrderDifferentQuantities()` — Different quantities per product
- `testCreateOrderLargeQuantity()` — Large quantity (e.g., 999) works

#### POST /api/orders Test Cases (Failure)
- `testCreateOrderMissingCustomerId()` — Missing customerId returns 400
- `testCreateOrderMissingRestaurantId()` — Missing restaurantId returns 400
- `testCreateOrderMissingProductsArray()` — Missing products returns 400
- `testCreateOrderEmptyProductsArray()` — Empty products array returns 400
- `testCreateOrderMissingTotalPrice()` — Missing totalPrice returns 400
- `testCreateOrderNullCustomerId()` — Null customerId returns 400
- `testCreateOrderNullRestaurantId()` — Null restaurantId returns 400
- `testCreateOrderNonExistentCustomer()` — Non-existent customer ID returns 404
- `testCreateOrderNonExistentRestaurant()` — Non-existent restaurant ID returns 404
- `testCreateOrderNonExistentProduct()` — Non-existent product ID returns 404
- `testCreateOrderProductFromDifferentRestaurant()` — Product not from restaurant returns 400
- `testCreateOrderPriceMismatch()` — Total price doesn't match products returns 400
- `testCreateOrderZeroQuantity()` — Zero quantity returns 400
- `testCreateOrderNegativeQuantity()` — Negative quantity returns 400
- `testCreateOrderNegativePrice()` — Negative totalPrice returns 400
- `testCreateOrderInvalidPriceFormat()` — Invalid price format returns 400
- `testCreateOrderPriceCalculationError()` — Price calculation error returns 400
- `testCreateOrderDatabaseError()` — Database error returns 500

---

## 📊 Data Used or Modified

### Test Data Structure (GET Tests)

#### Valid Test Data - Multiple Orders
```java
// Setup test data before test
Customer customer1 = createCustomer("cust1");
Customer customer2 = createCustomer("cust2");
Restaurant restaurant = createRestaurant("Restaurant A");
Product product1 = createProduct(restaurant, "Pizza", 12.99);
Product product2 = createProduct(restaurant, "Salad", 8.99);

// Create test orders
Order order1 = createOrder(customer1, restaurant, 
    Arrays.asList(product1), 12.99);
Order order2 = createOrder(customer2, restaurant, 
    Arrays.asList(product1, product2), 21.98);
```

#### Filter Parameter Examples
```java
GET /api/orders                              // All orders
GET /api/orders?customer=1                   // Customer 1's orders
GET /api/orders?restaurant=5                 // Restaurant 5's orders
GET /api/orders?status=PENDING               // Pending orders
GET /api/orders?customer=1&status=DELIVERED  // Customer 1's delivered orders
```

### Test Data Structure (POST Tests)

#### Valid Request Data - Single Product
```java
ApiOrderRequestDTO request = new ApiOrderRequestDTO(
    1L,  // customerId
    2L,  // restaurantId
    Arrays.asList(
        new OrderProductRequest(10L, 2)  // product 10, quantity 2
    ),
    25.98  // totalPrice (12.99 × 2)
);
```

#### Valid Request Data - Multiple Products
```java
ApiOrderRequestDTO request = new ApiOrderRequestDTO(
    1L,  // customerId
    2L,  // restaurantId
    Arrays.asList(
        new OrderProductRequest(10L, 2),  // product 10, qty 2, price 12.99 each
        new OrderProductRequest(11L, 1)   // product 11, qty 1, price 8.99
    ),
    34.97  // totalPrice (12.99×2 + 8.99×1)
);
```

#### Invalid Request Data Examples
```java
// Missing fields
new ApiOrderRequestDTO(null, 2L, products, 25.00)     // null customerId
new ApiOrderRequestDTO(1L, null, products, 25.00)     // null restaurantId
new ApiOrderRequestDTO(1L, 2L, null, 25.00)           // null products
new ApiOrderRequestDTO(1L, 2L, new ArrayList(), 25.00) // empty products
new ApiOrderRequestDTO(1L, 2L, products, null)        // null totalPrice

// Invalid values
new ApiOrderRequestDTO(999L, 2L, products, 25.00)     // non-existent customer
new ApiOrderRequestDTO(1L, 999L, products, 25.00)     // non-existent restaurant
new ApiOrderRequestDTO(1L, 2L, products, 10.00)       // price mismatch
new ApiOrderRequestDTO(1L, 2L, badProducts, 25.00)    // product from different restaurant

// Invalid quantities
new OrderProductRequest(10L, 0)   // zero quantity
new OrderProductRequest(10L, -1)  // negative quantity
```

### Test Assertions

#### GET Response Assertions
```java
mockMvc.perform(get("/api/orders"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.statusCode").value(200))
    .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
    .andExpect(jsonPath("$.data[0].id").exists())
    .andExpect(jsonPath("$.data[0].customerId").exists())
    .andExpect(jsonPath("$.data[0].restaurants").exists())
    .andExpect(jsonPath("$.data[0].products", isArray()))
    .andExpect(jsonPath("$.data[0].status").exists())
    .andExpect(jsonPath("$.data[0].totalPrice").exists())
    .andExpect(jsonPath("$.data[0].createdAt").exists());
```

#### POST Response Assertions
```java
mockMvc.perform(post("/api/orders")
    .contentType(MediaType.APPLICATION_JSON)
    .content(json))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.statusCode").value(201))
    .andExpect(jsonPath("$.data.id").exists())
    .andExpect(jsonPath("$.data.status").value("PENDING"))
    .andExpect(jsonPath("$.data.customerId").value(1L))
    .andExpect(jsonPath("$.data.restaurantId").value(2L))
    .andExpect(jsonPath("$.data.products", hasSize(2)))
    .andExpect(jsonPath("$.data.totalPrice").value(34.97))
    .andExpect(jsonPath("$.data.createdAt").exists());
```

#### Database Assertions (POST)
```java
Order saved = orderRepository.findById(createdId);
assertNotNull(saved);
assertEquals(1L, saved.getCustomerId());
assertEquals(2L, saved.getRestaurantId());
assertEquals(OrderStatus.PENDING, saved.getStatus());

List<ProductOrder> productOrders = 
    productOrderRepository.findByOrderId(createdId);
assertEquals(2, productOrders.size());
assertEquals(2, productOrders.get(0).getQuantity());
assertEquals(1, productOrders.get(1).getQuantity());
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Testing Framework:** JUnit 5 (Jupiter) with Spring Boot Test
- **Mocking:** Mockito for service/repository mocking
- **Integration Testing:** MockMvc for controller testing
- **Assertions:** AssertJ or JUnit 5 native assertions
- **Test Scope:** Test from controller down through service
- **Data Cleanup:** Use @DirtiesContext or @Transactional with rollback
- **Test Isolation:** Each test independent (no shared state)
- **Parameterized Tests:** Use @ParameterizedTest for multiple scenarios
- **Test Data:** Use factory methods or builders
- **JSON Processing:** Use ObjectMapper for serialization
- **Annotations:** Use @Test, @DisplayName, @BeforeEach, @AfterEach
- **Database Verification:** Verify both response AND database state
- **Price Precision:** Handle BigDecimal for price calculations

---

## ✅ Acceptance Criteria

### GET Endpoint Test Coverage
- [ ] Test class exists: OrderApiControllerTest
- [ ] Test method exists for GET all orders
- [ ] Test method exists for GET empty database
- [ ] GET with no filters returns HTTP 200
- [ ] GET response includes statusCode = 200
- [ ] GET response data is array
- [ ] GET with no orders returns empty array (not error)
- [ ] GET with orders returns all orders
- [ ] Each order includes: id, customerId, restaurantId, products, status, totalPrice, timestamps
- [ ] Products array is populated (not empty)
- [ ] Test for customer filter parameter
- [ ] Test for restaurant filter parameter
- [ ] Test for status filter parameter
- [ ] Test for multiple filters together
- [ ] Test for invalid customer ID filter → 400 or 404
- [ ] Test for invalid restaurant ID filter → 400 or 404
- [ ] Test for invalid status filter → 400
- [ ] Test for non-existent customer → 404
- [ ] Test for non-existent restaurant → 404
- [ ] Test for non-numeric filter → 400
- [ ] Test for negative ID filter → 400
- [ ] All error tests include error message
- [ ] All error tests return appropriate status code

### POST Endpoint Test Coverage - Success
- [ ] Test method exists for valid POST with single product
- [ ] Test method exists for valid POST with multiple products
- [ ] Valid POST returns 201 Created status
- [ ] Valid POST returns ApiResponseDTO format
- [ ] Valid POST response includes statusCode = 201
- [ ] Valid POST response includes order data
- [ ] Created order has auto-generated ID
- [ ] Created order status is PENDING
- [ ] Created order includes customerId
- [ ] Created order includes restaurantId
- [ ] Created order includes products array with all products
- [ ] Created order includes product details (id, name, price, quantity)
- [ ] Created order totalPrice is accurate
- [ ] Created order has createdAt timestamp
- [ ] Created order has updatedAt timestamp
- [ ] Order is persisted to database
- [ ] ProductOrder records created for each product
- [ ] ProductOrder quantity matches request
- [ ] Order is queryable via GET /api/orders
- [ ] Test with single product works
- [ ] Test with 2+ products works
- [ ] Test with different quantities works
- [ ] Test with large quantities works

### POST Endpoint Test Coverage - Failure
- [ ] Test for missing customerId → 400
- [ ] Test for missing restaurantId → 400
- [ ] Test for missing products array → 400
- [ ] Test for empty products array → 400
- [ ] Test for missing totalPrice → 400
- [ ] Test for null customerId → 400
- [ ] Test for null restaurantId → 400
- [ ] Test for null products → 400
- [ ] Test for null totalPrice → 400
- [ ] Test for non-existent customerId → 404
- [ ] Test for non-existent restaurantId → 404
- [ ] Test for non-existent product ID → 404
- [ ] Test for product not from restaurant → 400
- [ ] Test for price mismatch → 400
- [ ] Test for zero quantity → 400
- [ ] Test for negative quantity → 400
- [ ] Test for negative totalPrice → 400
- [ ] Test for non-numeric quantity → 400
- [ ] All error tests include error message
- [ ] All error tests return appropriate status code
- [ ] No order created on validation failure
- [ ] No ProductOrder records created on failure

### Test Quality Criteria
- [ ] All tests are independent
- [ ] All tests use descriptive @DisplayName
- [ ] All tests clean up database after execution
- [ ] All tests verify both response and database state
- [ ] Assertions are clear and specific
- [ ] Test data created using consistent methods
- [ ] No hard-coded magic numbers
- [ ] Tests follow naming convention
- [ ] Each test tests exactly one thing
- [ ] Tests run successfully (./mvnw test)
- [ ] Test coverage includes all happy paths
- [ ] Test coverage includes all error paths
- [ ] Test coverage includes boundary conditions
- [ ] Test coverage includes complex scenarios (multiple products)

---

## 📝 Notes for the AI

- **Complex Order Creation:** POST order tests must verify:
  1. All input validation (required fields, valid references)
  2. Price calculation and validation
  3. Order entity creation
  4. ProductOrder junction records creation
  5. Database persistence for both tables
  6. Response includes all products with correct details
- **Price Validation Logic:** Service must validate:
  - totalPrice = SUM(product[i].price × quantity[i]) for all products
  - No underpayment or overpayment allowed
  - Prices stored as BigDecimal (2 decimal places)
- **GET Filters:** Implement flexible query parameter handling:
  - All parameters optional
  - Filters can be combined
  - Invalid filters return 400
  - Non-existent entity IDs return 404
  - Empty results return 200 with empty array
- **Test Fixtures:** Create helper methods for test data:
  ```java
  private Customer createCustomer(String username) { ... }
  private Restaurant createRestaurant(String name) { ... }
  private Product createProduct(Restaurant r, String name, BigDecimal price) { ... }
  private Order createOrder(Customer c, Restaurant r, List<Product> products, 
                           BigDecimal totalPrice) { ... }
  ```
- **MockMvc JSON Path Assertions:**
  - Use `jsonPath("$.path.to.field")` for nested objects
  - Use `hasSize(n)` for array size assertions
  - Use `isArray()` to verify array type
  - Use `value(expected)` for equality assertions
- **Database Cleanup:** Options:
  - @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
  - @Transactional with rollback
  - Manually delete in @AfterEach
- **ProductOrder Verification:** After POST order creation, verify:
  ```java
  List<ProductOrder> records = 
      productOrderRepository.findByOrderId(orderId);
  assertEquals(expectedCount, records.size());
  for (ProductOrder record : records) {
      assertNotNull(record.getProductId());
      assertTrue(record.getQuantity() > 0);
  }
  ```
- **Price Calculation Examples:**
  - 1 × Product A ($12.99) = $12.99
  - 2 × Product A ($12.99) = $25.98
  - 2 × Product A ($12.99) + 1 × Product B ($8.99) = $34.97
  - Request must include exact total, or validation fails
- **TDD Approach:** For POST and GET endpoints:
  1. Write test for success case
  2. Write test for validation failure
  3. Write test for not found case
  4. Run tests (fail expected)
  5. Implement endpoint
  6. Tests pass
- **Parameterized Tests:** Use for testing multiple scenarios:
  ```java
  @ParameterizedTest
  @CsvSource({
      "1, 2, 'PENDING'",
      "2, 1, 'ACCEPTED'",
      "3, 3, 'CANCELED'"
  })
  void testGetOrdersFilterBy(Long customerId, Long restaurantId, String status) { ... }
  ```
- **Common Mistakes to Avoid:**
  - Not verifying ProductOrder records created
  - Not checking database state in POST tests
  - Testing implementation instead of contracts
  - Fragile tests that break on unrelated changes
  - Not cleaning up test data (pollution)
  - Testing only happy path (missing error cases)
- **Integration Verification:** After POST order creation, immediately GET order and verify all data matches
- **Assertion Count:** Each test should have 3-5 key assertions (not too many, not too few)
- **Response Format:** Ensure all GET and POST tests verify correct ApiResponseDTO structure with statusCode, message, and data fields
