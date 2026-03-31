# 🤖 AI_FEATURE_Restaurant Tests - GET & DELETE

## 🎯 Feature Identity

- **Feature Name:** Restaurant API Tests - GET and DELETE Endpoint Coverage (Extra Miles)
- **Related Area:** Backend / Testing / Quality Assurance

---

## 🎪 Feature Goal

Establish comprehensive test coverage for restaurant retrieval (GET /api/restaurants, GET /api/restaurants/{id}) and deletion (DELETE /api/restaurants/{id}) endpoints using JUnit 5 and MockMvc. The tests must validate list operations, single-resource retrieval, cascade deletion, and error handling to complete full test coverage of the restaurant CRUD API.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Unit tests for RestaurantService GET methods
- Integration tests for RestaurantApiController GET /api/restaurants endpoint (list all)
- Integration tests for RestaurantApiController GET /api/restaurants/{id} endpoint (single)
- Integration tests for RestaurantApiController DELETE /api/restaurants/{id} endpoint
- Response validation for list and single resource operations
- HTTP status code validation (200 OK, 204 No Content, 400 Bad Request, 404 Not Found, 500 Error)
- Cascade deletion verification (products, product_orders, orders removed)
- Database state verification (restaurants, products, orders, product_orders tables)
- Empty list handling (zero restaurants returns 200 with empty array)
- Pagination support (if implemented)
- Error handling testing (non-existent resources, invalid IDs, database errors)
- MockMvc setup and JSON path assertions
- Test fixtures and data builders
- Edge cases (boundary values, special characters in names)

### ❌ Out of Scope (Excluded)

- Filtering or search (out of scope for basic GET)
- Sorting beyond natural order
- Performance or load testing
- Authentication/authorization testing
- UI or frontend testing
- Database migration testing
- Deployment testing

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **GET All Restaurants Tests:** Write tests for list endpoint with/without data
- **GET Single Restaurant Tests:** Write tests for retrieving restaurant by ID
- **DELETE Endpoint Tests:** Write tests for cascade deletion functionality
- **Response Validation:** Verify format, status codes, all fields present
- **Database Verification:** Verify data persisted and deleted correctly
- **Cascade Deletion:** Verify products, product_orders, orders removed
- **Error Handling:** Test invalid IDs, non-existent resources, server errors
- **Empty Results:** Verify empty array returns 200 (not error)
- **Test Fixtures:** Create test data builders and helper methods
- **Edge Cases:** Test boundary conditions and special values

---

## 👥 User Flow / Logic (High Level)

### GET All Restaurants Flow
1. Test setup creates multiple restaurants in database
2. Test sends GET request to /api/restaurants
3. MockMvc captures response
4. Test verifies 200 status code
5. Test verifies response format (ApiResponseDTO)
6. Test verifies data is array of restaurants
7. Test verifies all restaurants returned
8. Test verifies each restaurant has all fields
9. Test verifies restaurant count matches database

### GET Single Restaurant Flow
1. Test setup creates restaurant in database
2. Test sends GET request to /api/restaurants/{id}
3. MockMvc captures response
4. Test verifies 200 status code if found
5. Test verifies response includes complete restaurant
6. Test verifies all fields present (id, name, address, phone, rating, timestamps)
7. Test verifies returned data matches database
8. If not found → verify 404 status code
9. If invalid ID → verify 400 status code

### DELETE Endpoint Flow
1. Test setup creates restaurant with products and orders
2. Test verifies restaurant exists
3. Test sends DELETE request to /api/restaurants/{id}
4. MockMvc captures response
5. Test verifies 200 or 204 status code
6. Test verifies restaurant is deleted from database
7. Test verifies all products for restaurant are deleted
8. Test verifies all product_orders are deleted
9. Test verifies all orders are deleted
10. Verify cascade deletion is complete and atomic
11. Test verifies 404 if trying to delete again

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Testing Interfaces (JUnit 5 + MockMvc)

#### GET /api/restaurants Test Cases (List All)
- `testGetAllRestaurants()` — Retrieve all restaurants
- `testGetAllRestaurantsEmptyDatabase()` — No restaurants returns empty array
- `testGetAllRestaurantsMultipleRecords()` — Multiple restaurants returned
- `testGetAllRestaurantsResponseFormat()` — Verify ApiResponseDTO structure
- `testGetAllRestaurantsIncludesAllFields()` — Each restaurant has all fields
- `testGetAllRestaurantsStatus()` — Returns 200 status

#### GET /api/restaurants/{id} Test Cases (Single)
- `testGetRestaurantByIdValid()` — Retrieve existing restaurant
- `testGetRestaurantByIdReturnedData()` — Verify all fields returned
- `testGetRestaurantByIdNotFound()` — Non-existent ID returns 404
- `testGetRestaurantByIdInvalidFormat()` — Non-numeric ID returns 400
- `testGetRestaurantByIdNegative()` — Negative ID returns 400
- `testGetRestaurantByIdZero()` — Zero ID returns 400
- `testGetRestaurantByIdResponseFormat()` — Verify response structure

#### DELETE /api/restaurants/{id} Test Cases
- `testDeleteRestaurantValid()` — Delete existing restaurant
- `testDeleteRestaurantCascadeProducts()` — Products are deleted
- `testDeleteRestaurantCascadeProductOrders()` — ProductOrders are deleted
- `testDeleteRestaurantCascadeOrders()` — Orders are deleted
- `testDeleteRestaurantNotFound()` — Non-existent ID returns 404
- `testDeleteRestaurantInvalidFormat()` — Non-numeric ID returns 400
- `testDeleteRestaurantNegative()` — Negative ID returns 400
- `testDeleteRestaurantResponseStatus()` — 200 or 204 returned
- `testDeleteRestaurantVerifyRemoved()` — Cannot retrieve after delete
- `testDeleteRestaurantWithoutCascade()` — Cascade deletion works atomically

---

## 📊 Data Used or Modified

### Test Data Structure (GET Tests)

#### Valid Test Data - Multiple Restaurants
```java
Restaurant restaurant1 = new Restaurant("Pizza Palace", "123 Main", "5551234567");
Restaurant restaurant2 = new Restaurant("Burger Barn", "456 Oak", "5555554321");
Restaurant restaurant3 = new Restaurant("Taco Tower", "789 Pine", "5559876543");
// Save to database before test
```

#### Test Assertions
```java
mockMvc.perform(get("/api/restaurants"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.data", hasSize(3)))
    .andExpect(jsonPath("$.data[0].id").exists())
    .andExpect(jsonPath("$.data[0].name").exists())
    .andExpect(jsonPath("$.data[0].address").exists())
    .andExpect(jsonPath("$.data[0].phone").exists());
```

### Test Data Structure (DELETE Tests)

#### Complete Restaurant with Dependencies
```java
Restaurant restaurant = createRestaurant("Delete Me Restaurant", "999 Test St", "5551234567");
Product product1 = createProduct(restaurant, "Item 1", 10.00);
Product product2 = createProduct(restaurant, "Item 2", 15.00);
Order order = createOrder(customer, restaurant, 
    Arrays.asList(product1, product2), 25.00);
// Creates ProductOrder records automatically
```

#### Database Verification After Delete
```java
// Verify restaurant is gone
assertFalse(restaurantRepository.findById(restaurantId).isPresent());

// Verify products are gone
List<Product> products = productRepository.findByRestaurantId(restaurantId);
assertEquals(0, products.size());

// Verify product_orders are gone
List<ProductOrder> productOrders = 
    productOrderRepository.findByOrderId(orderId);
assertEquals(0, productOrders.size());

// Verify orders might be orphaned or deleted (depends on design)
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Testing Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito for dependencies
- **Integration Testing:** MockMvc
- **Assertions:** AssertJ or JUnit 5 native
- **Test Scope:** Controller through service to repository
- **Data Cleanup:** @DirtiesContext or @Transactional
- **Test Isolation:** Independent tests
- **Database Verification:** Check both response and database state
- **Cascade Validation:** Verify all related records deleted

---

## ✅ Acceptance Criteria

### GET All Restaurants Tests
- [ ] Test method exists for list endpoint
- [ ] GET returns 200 status
- [ ] Empty database returns 200 with empty array
- [ ] Response includes ApiResponseDTO format
- [ ] Data is array type
- [ ] Multiple restaurants return all records
- [ ] Each restaurant includes: id, name, address, phone, rating, timestamps
- [ ] Restaurant count is correct

### GET Single Restaurant Tests
- [ ] Test method exists for single endpoint
- [ ] Valid ID returns 200 status
- [ ] Response includes complete restaurant
- [ ] All required fields present
- [ ] Non-existent ID returns 404
- [ ] Invalid ID format returns 400
- [ ] Negative ID returns 400
- [ ] Response format is correct

### DELETE Tests
- [ ] Test method exists for delete
- [ ] Valid ID returns 200 or 204 status
- [ ] Restaurant deleted from database
- [ ] Products cascade deleted
- [ ] ProductOrders cascade deleted
- [ ] Related orders handled appropriately
- [ ] Non-existent ID returns 404
- [ ] Invalid ID format returns 400
- [ ] Cascade deletion is atomic
- [ ] Cannot retrieve deleted restaurant

---

## 📝 Notes for the AI

- **GET List Performance:** Verify response time acceptable for multiple records
- **Cascade Delete Atomicity:** All or nothing — if any part fails, entire delete should fail
- **Empty Array vs Empty Response:** GET with no restaurants should return 200 with `[]`, not null or 404
- **Test Data Setup:** Use @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD) for clean database between tests
- **Cascade Complexity:** Verify product_orders are deleted before products (foreign key constraints)
- **Response Consistency:** GET endpoints should use same format as POST/PUT
