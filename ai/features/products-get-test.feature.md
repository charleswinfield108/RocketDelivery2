# 🤖 AI_FEATURE_Products Tests - GET

## 🎯 Feature Identity

- **Feature Name:** Products API Tests - GET Endpoint Coverage (Extra Miles)
- **Related Area:** Backend / Testing / Quality Assurance

---

## 🎪 Feature Goal

Establish comprehensive test coverage for product retrieval (GET /api/products) endpoint using JUnit 5 and MockMvc. The tests must validate restaurant-filtered product retrieval, handle edge cases (no products, invalid restaurants, empty filters), and verify all fields are correctly returned in the correct format.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Unit tests for ProductService GET method
- Integration tests for ProductApiController GET /api/products endpoint
- Filter parameter validation (restaurant_id or restaurant query parameter)
- Response validation for product lists
- HTTP status code validation (200 OK, 400 Bad Request, 404 Not Found, 500 Error)
- Empty product lists (restaurant exists but no products returns 200 with empty array)
- Non-existent restaurant returns 404
- Missing or invalid filter parameters
- Database verification (products retrieved match database)
- MockMvc setup and JSON path assertions
- Test fixtures and data builders
- Price validation in responses
- Stock/inventory validation in responses
- Timestamp validation (createdAt, updatedAt)
- Multiple products per restaurant
- Edge cases (special characters in names, zero prices, large product counts)

### ❌ Out of Scope (Excluded)

- Pagination (out of scope)
- Sorting (out of basic GET)
- Searching by product name
- Filtering by price range
- Filtering by category
- Availability status filtering
- Performance or load testing
- Authentication/authorization testing
- UI or frontend testing

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Basic Retrieval:** Test GET with valid restaurant ID
- **Response Validation:** Verify format, status codes, all fields present
- **Filter Handling:** Test with/without filters, valid/invalid values
- **Empty Lists:** Verify returns 200 with empty array for no products
- **Restaurant Validation:** Verify 404 for non-existent restaurant
- **Database Verification:** Verify data matches database
- **Test Fixtures:** Create test data builders and helper methods
- **Edge Cases:** Test boundary conditions, special characters
- **Field Validation:** Verify all product fields present and correct

---

## 👥 User Flow / Logic (High Level)

### GET /api/products Flow
1. Test setup creates restaurant in database
2. Test setup creates products associated with restaurant
3. Test sends GET request to /api/products?restaurant={id}
4. MockMvc captures response
5. Test verifies 200 status code
6. Test verifies response format (ApiResponseDTO with array)
7. Test verifies data is array of products
8. Test verifies all products returned
9. Test verifies each product has all fields
10. Test verifies product data matches database

### GET /api/products - No Products Flow
1. Test setup creates restaurant with no products
2. Test sends GET request to /api/products?restaurant={id}
3. MockMvc captures response
4. Test verifies 200 status code (not 404 or null)
5. Test verifies response includes empty array
6. Test verifies no error message

### GET /api/products - Invalid Restaurant Flow
1. Test sends GET request to /api/products?restaurant={invalidId}
2. MockMvc captures response
3. Test verifies 404 status code (restaurant not found)
4. Or verifies 400 if invalid format

### GET /api/products - Missing Filter Flow
1. Test sends GET request to /api/products (no filter)
2. MockMvc captures response
3. Test verifies 400 status code
4. Test verifies error message about missing restaurant parameter

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Testing Interfaces (JUnit 5 + MockMvc)

#### GET /api/products Test Cases

- `testGetProductsByRestaurantIdValid()` — Retrieve products by valid restaurant ID
- `testGetProductsByRestaurantMultipleProducts()` — Multiple products returned correctly
- `testGetProductsByRestaurantSingleProduct()` — Single product returned correctly
- `testGetProductsByRestaurantNoProducts()` — Restaurant with no products returns empty array
- `testGetProductsByRestaurantNotFound()` — Non-existent restaurant returns 404
- `testGetProductsByRestaurantInvalidId()` — Invalid ID format returns 400
- `testGetProductsByRestaurantNegativeId()` — Negative restaurant ID returns 400
- `testGetProductsByRestaurantZeroId()` — Zero restaurant ID returns 400
- `testGetProductsByRestaurantMissingFilter()` — No filter parameter returns 400
- `testGetProductsByRestaurantNullFilter()` — Null filter parameter returns 400
- `testGetProductsByRestaurantEmptyFilter()` — Empty string filter returns 400
- `testGetProductsByRestaurantResponseFormat()` — Verify ApiResponseDTO structure
- `testGetProductsByRestaurantIncludesAllFields()` — All product fields present
- `testGetProductsByRestaurantPriceValidation()` — Price values correct
- `testGetProductsByRestaurantStockValidation()` — Stock/inventory values correct
- `testGetProductsByRestaurantTimestampValidation()` — CreatedAt and updatedAt present
- `testGetProductsByRestaurantOrderMatches()` — Products match database order
- `testGetProductsByRestaurantStatusCode()` — Correct HTTP status returned

---

## 📊 Data Used or Modified

### Test Data Structure (Valid Requests)

#### Single Restaurant with Multiple Products
```java
Restaurant restaurant = createRestaurant(
    "Pasta Palace", "100 Noodle St", "5551111111"
);

Product product1 = new Product(
    name: "Spaghetti Bolognese",
    description: "Classic meat sauce",
    price: 12.99,
    stock: 50,
    restaurant: restaurant
);

Product product2 = new Product(
    name: "Fettuccine Alfredo",
    description: "Creamy sauce",
    price: 13.99,
    stock: 35,
    restaurant: restaurant
);

Product product3 = new Product(
    name: "Lasagna",
    description: "Baked layers",
    price: 14.99,
    stock: 20,
    restaurant: restaurant
);

// Save all to database
```

#### Test Assertions
```java
mockMvc.perform(get("/api/products")
    .param("restaurant", String.valueOf(restaurantId)))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.data", hasSize(3)))
    .andExpect(jsonPath("$.data[0].id").exists())
    .andExpect(jsonPath("$.data[0].name").exists())
    .andExpect(jsonPath("$.data[0].description").exists())
    .andExpect(jsonPath("$.data[0].price").exists())
    .andExpect(jsonPath("$.data[0].stock").exists())
    .andExpect(jsonPath("$.data[0].createdAt").exists())
    .andExpect(jsonPath("$.data[0].updatedAt").exists());
```

### Test Data Structure (Edge Cases)

#### Empty Product List
```java
Restaurant restaurant = createRestaurant("Empty Restaurant", "200 None St", "5552222222");
// No products created - test retrieval
```

#### Special Characters in Product Names
```java
Product product = new Product(
    name: "Pad Drunken Noodle (หมี่เมา)",
    description: "Thai spicy noodles & special #chars $%^",
    price: 11.50,
    stock: 25,
    restaurant: restaurant
);
```

#### Price Edge Cases
```java
Product expensiveItem = new Product(
    name: "Gold Leaf Steak",
    price: 999.99,
    stock: 1,
    restaurant: restaurant
);

Product cheapItem = new Product(
    name: "Side Salad",
    price: 0.99,
    stock: 100,
    restaurant: restaurant
);
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Testing Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito for dependencies
- **Integration Testing:** MockMvc for HTTP layer
- **Assertions:** AssertJ or JUnit 5 native with JsonPath
- **Test Scope:** Controller through service to repository
- **Data Cleanup:** @DirtiesContext or @Transactional for isolation
- **Test Independence:** Each test independent and repeatable
- **Database Verification:** Verify both response and database state
- **Filter Parameter:** restaurant_id or restaurant (naming convention)
- **Response Format:** ApiResponseDTO with data array

---

## ✅ Acceptance Criteria

### GET /api/products Tests - Valid Cases
- [ ] Test method exists for product retrieval
- [ ] Valid restaurant ID returns 200 status
- [ ] Response includes ApiResponseDTO format
- [ ] Data is array type
- [ ] Single product returns 1 item
- [ ] Multiple products return all items
- [ ] Each product includes: id, name, description, price, stock, createdAt, updatedAt
- [ ] Price is decimal type
- [ ] Stock is integer type
- [ ] Timestamps present and valid

### GET /api/products Tests - No Products
- [ ] Restaurant with no products returns 200
- [ ] Response includes empty array []
- [ ] No error message
- [ ] Response still includes ApiResponseDTO structure

### GET /api/products Tests - Invalid Restaurant
- [ ] Non-existent restaurant returns 404
- [ ] Invalid ID format returns 400
- [ ] Negative restaurant ID returns 400
- [ ] Zero restaurant ID returns 400
- [ ] Error message included in response

### GET /api/products Tests - Missing/Invalid Filters
- [ ] Missing restaurant filter returns 400
- [ ] Null filter returns 400
- [ ] Empty filter returns 400
- [ ] Error message describes missing parameter

### GET /api/products Tests - Response Validation
- [ ] All products from database are returned
- [ ] No extra products returned
- [ ] Product order matches database
- [ ] All fields match database values
- [ ] Price values are accurate
- [ ] Stock values are accurate

---

## 📝 Notes for the AI

- **Filter Parameter Naming:** Use consistent parameter name (restaurant_id or restaurant_param)
- **Empty List Handling:** This is crucial — restaurant exists with no products should return 200 with [], NOT 404 or null
- **Restaurant Validation:** 404 means restaurant not found (search for restaurant in database)
- **Missing Parameter:** 400 means restaurant parameter is required but missing
- **Price Precision:** Ensure prices are returned with correct decimal precision (cents)
- **Test Data Cleanup:** Use @DirtiesContext or @Transactional to ensure clean state between tests
- **Database Verification:** Assert both response content AND verify products in database match
