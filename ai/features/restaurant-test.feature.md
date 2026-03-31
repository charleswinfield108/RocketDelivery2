# 🤖 AI_FEATURE_Restaurant Tests

## 🎯 Feature Identity

- **Feature Name:** Restaurant API Tests - POST and PUT Endpoint Coverage
- **Related Area:** Backend / Testing / Quality Assurance

---

## 🎪 Feature Goal

Establish comprehensive test coverage for restaurant creation (POST /api/restaurants) and update (PUT /api/restaurants/{id}) endpoints using JUnit 5 and MockMvc. The tests must validate request handling, response formatting, business logic, error handling, and database persistence to ensure the REST API endpoints work correctly and prevent regressions during future changes.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Unit tests for RestaurantService methods (create, update)
- Integration tests for RestaurantApiController POST endpoint
- Integration tests for RestaurantApiController PUT endpoint
- Request validation testing (missing fields, invalid formats)
- Response validation testing (correct format, all fields present)
- HTTP status code validation (201 Created, 200 OK, 400 Bad Request, 404 Not Found, 500 Error)
- Database persistence verification (data saved correctly)
- Exception handling testing (validation errors, not found, server errors)
- MockMvc setup and configuration
- Test data builders and fixtures
- Parameterized tests for multiple scenarios
- Edge case coverage (boundary values, empty strings, special characters)

### ❌ Out of Scope (Excluded)

- Testing Authentication/Authorization (assume authenticated)
- Performance or load testing
- Test coverage for GET, DELETE endpoints (separate test classes)
- UI or frontend testing
- Integration with external services
- Database migration testing
- Deployment or CI/CD testing
- Manual test procedures
- Test reporting or metrics analysis

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **POST Endpoint Tests:** Write JUnit 5 tests for restaurant creation with valid and invalid data
- **PUT Endpoint Tests:** Write JUnit 5 tests for restaurant updates with valid and invalid data
- **Request Validation Tests:** Test missing required fields, invalid formats, boundary values
- **Response Validation Tests:** Verify response format, status codes, and data completeness
- **MockMvc Setup:** Configure MockMvc for controller testing
- **Service Mocking:** Mock repository and service dependencies in tests
- **Database Persistence Tests:** Verify data is saved correctly to database
- **Error Handling Tests:** Test error responses and exception handling
- **Test Data:** Create test fixtures and builder patterns for test data
- **Parameterized Tests:** Use @ParameterizedTest for multiple input scenarios
- **Assertion Methods:** Use appropriate assertions (assertEquals, assertTrue, assertNotNull, etc.)

---

## 👥 User Flow / Logic (High Level)

### Test Execution Flow (POST Restaurant)
1. Test class loads Spring context with @SpringBootTest or uses MockMvc
2. MockMvc is configured to test RestaurantApiController
3. Test case creates request object (ApiCreateRestaurantDTO)
4. Test sends POST request to /api/restaurants with test data
5. MockMvc captures HTTP request and response
6. Test verifies response status code (201 for success, 400 for validation error)
7. Test verifies response format using JsonPath assertions
8. Test verifies all required fields in response
9. Test verifies restaurant was saved to database
10. Test verifies generated ID is present
11. Test verifies timestamps are set correctly

### Test Execution Flow (PUT Restaurant)
1. Test class loads Spring context and sets up MockMvc
2. Test creates existing restaurant in database (test fixture)
3. Test creates update request object (ApiRestaurantDTO with changes)
4. Test sends PUT request to /api/restaurants/{id} with modified data
5. MockMvc captures HTTP request and response
6. Test verifies response status code (200 for success, 404 for not found)
7. Test verifies response includes updated data
8. Test verifies database record is updated correctly
9. Test verifies unchanged fields remain unchanged
10. Test verifies updated timestamp reflects change

### Validation Test Flow
1. Test case creates request with missing required field
2. Test sends request with invalid data
3. MockMvc captures response
4. Test verifies status code is 400 Bad Request
5. Test verifies error message indicates which field failed
6. Test verifies no data is persisted

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Testing Interfaces (JUnit 5 + MockMvc)

#### RestaurantApiControllerTest Class Structure
```java
@SpringBootTest
@AutoConfigureMockMvc
class RestaurantApiControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired RestaurantRepository repository;
    // Test methods...
}
```

#### POST /api/restaurants Test Cases
- `testCreateRestaurantWithValidData()` — Successfully create restaurant
- `testCreateRestaurantMissingName()` — 400 when name missing
- `testCreateRestaurantMissingAddress()` — 400 when address missing
- `testCreateRestaurantMissingPhone()` — 400 when phone missing
- `testCreateRestaurantWithEmptyName()` — 400 when name empty
- `testCreateRestaurantNameTooShort()` — 400 when name < 3 chars
- `testCreateRestaurantNameTooLong()` — 400 when name > 200 chars
- `testCreateRestaurantWithInvalidPhone()` — 400 when phone invalid
- `testCreateRestaurantResponseFormat()` — Verify response structure
- `testCreateRestaurantPersistence()` — Verify saved to database
- `testCreateRestaurantGeneratesId()` — Verify ID auto-generation
- `testCreateRestaurantInitialRating()` — Verify initial rating is 0.0

#### PUT /api/restaurants/{id} Test Cases
- `testUpdateRestaurantWithValidData()` — Successfully update restaurant
- `testUpdateRestaurantMissingName()` — 400 when name empty
- `testUpdateRestaurantAddressUpdate()` — Update only address field
- `testUpdateRestaurantPhoneUpdate()` — Update only phone field
- `testUpdateRestaurantRatingUpdate()` — Update rating field
- `testUpdateRestaurantNonExistent()` — 404 when restaurant doesn't exist
- `testUpdateRestaurantInvalidId()` — 400 when ID format invalid
- `testUpdateRestaurantResponseFormat()` — Verify response structure
- `testUpdateRestaurantPersistence()` — Verify updated in database
- `testUpdateRestaurantPreservesUnchangedFields()` — Verify only changed fields update
- `testUpdateRestaurantUpdatesTimestamp()` — Verify updatedAt timestamp changes
- `testUpdateRestaurantPreservesCreatedTimestamp()` — Verify createdAt unchanged

---

## 📊 Data Used or Modified

### Test Data Structure (POST Tests)

#### Valid Test Data
```java
ApiCreateRestaurantDTO validRequest = new ApiCreateRestaurantDTO(
    "Test Restaurant",           // name (3-200 chars)
    "123 Main Street",          // address (5-255 chars)
    "1234567890"                // phone (10+ chars)
);
```

#### Invalid Test Data Examples
```java
// Missing fields
new ApiCreateRestaurantDTO(null, "addr", "phone")        // null name
new ApiCreateRestaurantDTO("name", null, "phone")        // null address
new ApiCreateRestaurantDTO("name", "addr", null)         // null phone

// Invalid formats
new ApiCreateRestaurantDTO("", "addr", "phone")          // empty name
new ApiCreateRestaurantDTO("AB", "123 Main", "phone")    // name too short
new ApiCreateRestaurantDTO("X".repeat(201), "addr", "phone") // name too long
new ApiCreateRestaurantDTO("name", "1234", "phone")     // address too short
new ApiCreateRestaurantDTO("name", "addr", "123")        // phone too short
```

### Test Data Structure (PUT Tests)

#### Valid Update Data
```java
ApiRestaurantDTO updateRequest = new ApiRestaurantDTO(
    "Updated Name",             // name (optional)
    "456 Oak Avenue",          // address (optional)
    "0987654321",              // phone (optional)
    4.5                         // rating (optional, 0.0-5.0)
);
```

#### Partial Update Scenarios
```java
// Update only name
new ApiRestaurantDTO("New Name", null, null, null)

// Update only address
new ApiRestaurantDTO(null, "New Address", null, null)

// Update only rating
new ApiRestaurantDTO(null, null, null, 5.0)
```

### Test Assertions

#### Response Content Assertions
```java
mockMvc.perform(post("/api/restaurants")
    .contentType(MediaType.APPLICATION_JSON)
    .content(json))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.statusCode").value(201))
    .andExpect(jsonPath("$.message").exists())
    .andExpect(jsonPath("$.data.id").exists())
    .andExpect(jsonPath("$.data.name").value("Test Restaurant"))
    .andExpect(jsonPath("$.data.address").value("123 Main Street"))
    .andExpect(jsonPath("$.data.createdAt").exists());
```

#### Database Assertions
```java
Restaurant saved = repository.findById(createdId);
assertNotNull(saved);
assertEquals("Test Restaurant", saved.getName());
assertEquals("123 Main Street", saved.getAddress());
assertNotNull(saved.getCreatedAt());
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Testing Framework:** JUnit 5 (Jupiter) with Spring Boot Test
- **Mocking:** Mockito for service/repository mocking
- **Integration Testing:** MockMvc for controller testing
- **Assertions:** AssertJ or JUnit 5 native assertions
- **Test Scope:** Test from controller down through service layer
- **Data Cleanup:** Use @DirtiesContext or test transactions to reset database
- **Test Isolation:** Each test should be independent (no shared state)
- **Parameterized Tests:** Use @ParameterizedTest for multiple scenarios
- **Test Data:** Use factory methods or builders for consistent test data
- **Annotations:** Use @Test, @DisplayName, @BeforeEach, @AfterEach appropriately
- **No Manual Assertions:** Use framework assertions, not manual string equality checks

---

## ✅ Acceptance Criteria

### POST Endpoint Test Coverage
- [ ] Test class exists: RestaurantApiControllerTest
- [ ] Test method exists for valid POST request
- [ ] Valid POST returns 201 status code
- [ ] Valid POST returns ApiResponseDTO format
- [ ] Valid POST response includes statusCode = 201
- [ ] Valid POST response includes message field
- [ ] Valid POST response includes data object with created restaurant
- [ ] Created restaurant includes auto-generated ID
- [ ] Created restaurant includes all input fields (name, address, phone)
- [ ] Created restaurant has createdAt timestamp
- [ ] Created restaurant has updatedAt timestamp
- [ ] Created restaurant has initial rating (0.0 or null)
- [ ] Restaurant is persisted to database after POST
- [ ] Test for missing name field → 400 status
- [ ] Test for missing address field → 400 status
- [ ] Test for missing phone field → 400 status
- [ ] Test for empty name string → 400 status
- [ ] Test for name too short (< 3 chars) → 400 status
- [ ] Test for name too long (> 200 chars) → 400 status
- [ ] Test for address too short (< 5 chars) → 400 status
- [ ] Test for address too long (> 255 chars) → 400 status
- [ ] Test for phone too short (< 10 chars) → 400 status
- [ ] Test for invalid phone format → 400 status
- [ ] All error tests include error message in response
- [ ] All error tests return 400 status code
- [ ] MockMvc is properly configured
- [ ] Content-Type is application/json in requests
- [ ] Response Content-Type is application/json

### PUT Endpoint Test Coverage
- [ ] Test method exists for valid PUT request
- [ ] Valid PUT returns 200 status code
- [ ] Valid PUT returns ApiResponseDTO format
- [ ] Valid PUT response includes updated restaurant
- [ ] Valid PUT response includes all current restaurant fields
- [ ] Valid PUT updates database record
- [ ] Valid PUT preserves restaurant ID (unchanged)
- [ ] Valid PUT preserves createdAt timestamp (unchanged)
- [ ] Valid PUT updates updatedAt timestamp (to current time)
- [ ] Test for updating only name field
- [ ] Test for updating only address field
- [ ] Test for updating only phone field
- [ ] Test for updating only rating field
- [ ] Test for partial update does not overwrite other fields
- [ ] Test for non-existent restaurant ID → 404 status
- [ ] Test for invalid ID format (non-numeric) → 400 status
- [ ] Test for ID = 0 or negative → 400 status
- [ ] Test for empty name string → 400 status
- [ ] Test for name too short → 400 status
- [ ] Test for name too long → 400 status
- [ ] Test for invalid phone format → 400 status
- [ ] Test for invalid rating (negative or > 5) → 400 status
- [ ] All error tests return appropriate status codes
- [ ] All error tests include error messages

### Test Quality Criteria
- [ ] All tests are independent (no test dependencies)
- [ ] All tests use descriptive @DisplayName annotations
- [ ] All tests clean up database after execution
- [ ] All tests use MockMvc for integration testing
- [ ] All tests verify both response and database state
- [ ] Assertions are clear and specific (not generic)
- [ ] Test data is created using consistent methods (builders/factories)
- [ ] No hard-coded magic numbers or strings (use constants for magic values)
- [ ] All test methods follow naming convention: test<Feature><Scenario>
- [ ] Each test method tests exactly one thing (single responsibility)
- [ ] Tests run successfully and pass (./mvnw test)
- [ ] Test coverage includes all happy paths
- [ ] Test coverage includes all error paths
- [ ] Test coverage includes boundary conditions

---

## 📝 Notes for the AI

- **Test Class Structure:** Create RestaurantApiControllerTest with:
  - @SpringBootTest to load full application context
  - @AutoConfigureMockMvc to configure MockMvc
  - @Autowired MockMvc and Repository for testing
  - Setup method (@BeforeEach) to create test data
  - Cleanup method (@AfterEach) if needed
- **MockMvc Usage:** For testing controller:
  ```java
  mockMvc.perform(post("/api/restaurants")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.statusCode").value(201));
  ```
- **Assertions:** Use:
  - `status().isCreated()` and `status().is4xxClientError()` for status codes
  - `jsonPath("$.path.to.field").value(expected)` for JSON assertions
  - `objectMapper` to serialize/deserialize JSON
  - Database assertions to verify persistence
- **Test Data Builders:** Create helper methods or factory classes for test data:
  ```java
  private static ApiCreateRestaurantDTO validRequest() {
      return new ApiCreateRestaurantDTO("Test Restaurant", "123 Main", "1234567890");
  }
  ```
- **Parameterized Tests:** Use @ParameterizedTest with @ValueSource or @MethodSource for multiple scenarios:
  ```java
  @ParameterizedTest
  @ValueSource(strings = {"", "A", "AB"})
  void testNameTooShort(String name) { ... }
  ```
- **Database Cleanup:** After each test, database should be cleaned. Options:
  - Use @DirtiesContext annotation
  - Use @Transactional with rollback
  - Manually delete test data in @AfterEach
- **TDD Approach:** Write tests BEFORE implementation:
  1. Write test with expected behavior
  2. Test fails (expected)
  3. Implement code to make test pass
  4. Test passes
  5. Refactor if needed
- **Pre-written Tests:** Two tests are pre-written for restaurant endpoints (GET all, GET by ID). Do NOT modify these. Add tests for POST and PUT.
- **Testing Philosophy:**
  - Test the contract (what API clients see)
  - Test error cases (validation, not found)
  - Test persistence (database integration)
  - Test format (response structure)
  - Avoid testing implementation details
- **Coverage Goals:** Aim for:
  - All happy paths (valid requests)
  - All validation failures (invalid inputs)
  - All error cases (not found, server errors)
  - Boundary conditions (min/max lengths)
  - Edge cases (empty strings, special characters)
- **Avoid Common Mistakes:**
  - Not cleaning up test data (causes test pollution)
  - Testing implementation instead of contracts
  - Not verifying database changes (only checking response)
  - Fragile tests that break on unrelated changes
  - Tests that are too brittle (overly specific assertions)
- **Integration with CI/CD:** These tests will be run in:
  - Local development: `./mvnw test`
  - Pull request checks: Automated test runs
  - Pre-merge validation: Must pass all tests
