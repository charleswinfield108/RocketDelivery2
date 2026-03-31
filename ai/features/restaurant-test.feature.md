# 🤖 AI_FEATURE_Restaurant Tests - POST & PUT

## 🎯 Feature Identity

- **Feature Name:** Restaurant API Tests - POST and PUT Endpoint Coverage
- **Related Area:** Backend / Testing / Quality Assurance
- **Module:** Module 12 — Restaurant Management API

---

## 🎪 Feature Goal

Establish comprehensive test coverage for restaurant creation (POST /api/restaurants) and update (PUT /api/restaurants/{id}) endpoints using JUnit 5 and MockMvc. The tests must validate data persistence, validation enforcement, error handling, and response formatting to ensure the restaurant management API meets all specification requirements.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Unit tests for RestaurantService POST/PUT methods
- Integration tests for RestaurantApiController POST /api/restaurants endpoint
- Integration tests for RestaurantApiController PUT /api/restaurants/{id} endpoint
- Data validation testing (required fields, format, lengths, special characters)
- HTTP status code validation (201 Created, 200 OK, 400 Bad Request, 404 Not Found, 500 Error)
- Request/response payload validation and structure verification
- Database persistence verification (data actually saved)
- Cascade behavior validation (products, orders associated correctly)
- Error message validation and clarity
- Edge cases (boundary values, special characters, empty strings, null values)
- Uniqueness validation (phone numbers, business names)
- MockMvc setup and JSON path assertions
- Test fixtures and data builders
- Request body serialization/deserialization

### ❌ Out of Scope (Excluded)

- Authentication/authorization testing
- Filtering or search on POST/PUT
- Bulk operations
- UI or frontend testing
- Performance or load testing
- Deployment testing
- Asynchronous operations
- File uploads
- Image handling

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **POST Creation Tests:** Write tests for creating new restaurants with valid data
- **POST Validation Tests:** Write tests for all validation rules (required fields, lengths, formats)
- **POST Error Handling:** Write tests for invalid data, duplicate values, missing fields
- **POST Response Format:** Verify response includes created restaurant with all fields and correct status (201)
- **POST Persistence:** Verify created restaurant is actually saved to database
- **PUT Update Tests:** Write tests for updating existing restaurants with valid data
- **PUT Partial Updates:** Write tests for updating single fields
- **PUT Full Updates:** Write tests for updating all fields simultaneously
- **PUT Error Handling:** Write tests for invalid updates, non-existent resources, constraint violations
- **PUT Response Format:** Verify response includes updated restaurant with correct status (200)
- **PUT Persistence:** Verify updated restaurant data is saved to database
- **Database Verification:** Verify data persisted correctly through direct database queries
- **Cascade Operations:** Verify products and orders remain associated after restaurant update
- **Edge Cases:** Test boundary conditions, special characters, unicode in names/addresses

---

## 👥 User Flow / Logic (High Level)

### POST Create New Restaurant Flow
1. Test setup prepares valid restaurant data (name, address, phone)
2. Test sends POST request with JSON body to /api/restaurants
3. MockMvc captures response
4. Test verifies 201 Created status code
5. Test verifies response includes created restaurant
6. Test verifies all fields returned (id, name, address, phone, rating, createdAt, updatedAt)
7. Test verifies restaurant ID is assigned
8. Test verifies timestamp fields are populated
9. Test queries database to verify restaurant actually persisted
10. Test verifies created restaurant can be retrieved via GET

### POST Validation Failed Flow
1. Test setup prepares invalid restaurant data (missing name, invalid phone, etc.)
2. Test sends POST request with invalid JSON body
3. MockMvc captures response
4. Test verifies 400 Bad Request status code
5. Test verifies error message is clear and actionable
6. Test verifies restaurant is NOT created in database
7. Test verifies response includes field-specific error details

### PUT Update Existing Restaurant Flow
1. Test setup creates restaurant in database
2. Test sends PUT request with updated data to /api/restaurants/{id}
3. MockMvc captures response
4. Test verifies 200 OK status code
5. Test verifies response includes updated restaurant
6. Test verifies specific fields were updated
7. Test verifies other fields unchanged (if partial update)
8. Test queries database to verify updates actually persisted
9. Test verifies updated restaurant reflects changes when retrieved

### PUT Validation Failed Flow
1. Test setup creates restaurant in database
2. Test sends PUT request with invalid data
3. MockMvc captures response
4. Test verifies 400 Bad Request status code
5. Test verifies error message is clear
6. Test verifies restaurant data NOT changed in database
7. Test verifies GET still returns original data

### PUT Not Found Flow
1. Test setup uses non-existent restaurant ID
2. Test sends PUT request to /api/restaurants/{invalid-id}
3. MockMvc captures response
4. Test verifies 404 Not Found status code
5. Test verifies error message indicates resource not found

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Testing Interfaces (JUnit 5 + MockMvc)

#### POST /api/restaurants Test Cases (Create)

**Basic Creation Tests**
- `testCreateRestaurantValid()` — Create restaurant with all valid data
- `testCreateRestaurantMinimalData()` — Create with only required fields
- `testCreateRestaurantResponseStatus()` — Verify 201 Created returned
- `testCreateRestaurantResponseIncludesData()` — Verify response has complete restaurant
- `testCreateRestaurantResponseHasId()` — Verify restaurant ID assigned
- `testCreateRestaurantResponseHasTimestamps()` — Verify createdAt/updatedAt set
- `testCreateRestaurantPersisted()` — Verify saved to database

**Validation Tests - Required Fields**
- `testCreateRestaurantMissingName()` — Name required, returns 400
- `testCreateRestaurantMissingAddress()` — Address required, returns 400
- `testCreateRestaurantMissingPhone()` — Phone required, returns 400
- `testCreateRestaurantNullName()` — Null name returns 400
- `testCreateRestaurantNullAddress()` — Null address returns 400
- `testCreateRestaurantNullPhone()` — Null phone returns 400

**Validation Tests - Field Formats**
- `testCreateRestaurantEmptyName()` — Empty string name returns 400
- `testCreateRestaurantEmptyAddress()` — Empty string address returns 400
- `testCreateRestaurantEmptyPhone()` — Empty string phone returns 400
- `testCreateRestaurantBlankName()` — Whitespace-only name returns 400
- `testCreateRestaurantBlankAddress()` — Whitespace-only address returns 400
- `testCreateRestaurantBlankPhone()` — Whitespace-only phone returns 400

**Validation Tests - Field Lengths**
- `testCreateRestaurantNameTooShort()` — Name < 3 chars returns 400
- `testCreateRestaurantNameTooLong()` — Name > 100 chars returns 400
- `testCreateRestaurantAddressTooShort()` — Address < 5 chars returns 400
- `testCreateRestaurantAddressTooLong()` — Address > 200 chars returns 400
- `testCreateRestaurantPhoneInvalidFormat()` — Invalid phone format returns 400
- `testCreateRestaurantPhoneTooShort()` — Phone < 10 digits returns 400
- `testCreateRestaurantPhoneTooLong()` — Phone > 15 digits returns 400

**Validation Tests - Uniqueness**
- `testCreateRestaurantDuplicatePhone()` — Duplicate phone rejected with 400
- `testCreateRestaurantErrorMessageClear()` — Error message specifies duplicate field

**Edge Cases and Special Characters**
- `testCreateRestaurantNameWithUnicode()` — Unicode characters in name OK
- `testCreateRestaurantNameWithNumbers()` — Numbers in name OK
- `testCreateRestaurantNameWithHyphens()` — Hyphens in name OK
- `testCreateRestaurantAddressWithNumbers()` — Address with numbers OK
- `testCreateRestaurantAddressWithSpecialChars()` — Valid special chars OK
- `testCreateRestaurantPhoneWithDashes()` — Phone with dashes OK (formatted)
- `testCreateRestaurantNameAtMinLength()` — Name exactly 3 chars OK
- `testCreateRestaurantNameAtMaxLength()` — Name exactly 100 chars OK
- `testCreateRestaurantAddressAtMinLength()` — Address exactly 5 chars OK
- `testCreateRestaurantAddressAtMaxLength()` — Address exactly 200 chars OK

**Response Format Tests**
- `testCreateRestaurantResponseFormatApiResponse()` — Response is ApiResponseDTO
- `testCreateRestaurantResponseHasSuccessFlag()` — "success": true
- `testCreateRestaurantResponseHasMessage()` — Message field present
- `testCreateRestaurantResponseDataStructure()` — Data has all restaurant fields
- `testCreateRestaurantResponseFieldTypes()` — Fields have correct types

#### PUT /api/restaurants/{id} Test Cases (Update)

**Basic Update Tests**
- `testUpdateRestaurantValid()` — Update restaurant with all valid data
- `testUpdateRestaurantName()` — Update name only
- `testUpdateRestaurantAddress()` — Update address only
- `testUpdateRestaurantPhone()` — Update phone only
- `testUpdateRestaurantMultipleFields()` — Update multiple fields at once
- `testUpdateRestaurantResponseStatus()` — Verify 200 OK returned
- `testUpdateRestaurantResponseIncludesData()` — Verify response has updated restaurant
- `testUpdateRestaurantPersisted()` — Verify saved to database
- `testUpdateRestaurantUpdatedAtChanged()` — Verify updatedAt timestamp updated

**Partial Update Tests**
- `testUpdateRestaurantPartialName()` — Update name, other fields unchanged
- `testUpdateRestaurantPartialAddress()` — Update address, other fields unchanged
- `testUpdateRestaurantPartialPhone()` — Update phone, other fields unchanged
- `testUpdateRestaurantKeepCreatedAt()` — createdAt never changes on update
- `testUpdateRestaurantSameValues()` — Update with same values OK (idempotent)

**Validation Tests - Required Fields**
- `testUpdateRestaurantNameToEmpty()` — Cannot set name to empty string
- `testUpdateRestaurantNameToNull()` — Cannot set name to null
- `testUpdateRestaurantAddressToEmpty()` — Cannot set address to empty
- `testUpdateRestaurantAddressToNull()` — Cannot set address to null
- `testUpdateRestaurantPhoneToEmpty()` — Cannot set phone to empty
- `testUpdateRestaurantPhoneToNull()` — Cannot set phone to null

**Validation Tests - Field Lengths**
- `testUpdateRestaurantNameTooShort()` — Name < 3 chars returns 400
- `testUpdateRestaurantNameTooLong()` — Name > 100 chars returns 400
- `testUpdateRestaurantAddressTooShort()` — Address < 5 chars returns 400
- `testUpdateRestaurantAddressTooLong()` — Address > 200 chars returns 400
- `testUpdateRestaurantPhoneInvalidFormat()` — Invalid phone format returns 400
- `testUpdateRestaurantPhoneTooShort()` — Phone < 10 digits returns 400
- `testUpdateRestaurantPhoneTooLong()` — Phone > 15 digits returns 400

**Validation Tests - Uniqueness**
- `testUpdateRestaurantPhoneToDuplicate()` — Cannot update to existing phone number
- `testUpdateRestaurantViableAlternativePhone()` — Can update to non-duplicate phone

**Error Handling Tests - Not Found**
- `testUpdateRestaurantNotFound()` — Non-existent ID returns 404
- `testUpdateRestaurantInvalidFormat()` — Non-numeric ID returns 400
- `testUpdateRestaurantNegativeId()` — Negative ID returns 400/404
- `testUpdateRestaurantZeroId()` — Zero ID returns 400/404
- `testUpdateRestaurantErrorMessage()` — Clear 404 error message

**Edge Cases and Special Characters**
- `testUpdateRestaurantNameWithUnicode()` — Unicode in name OK
- `testUpdateRestaurantNameWithNumbers()` — Numbers in name OK
- `testUpdateRestaurantAddressWithSpecialChars()` — Valid special chars OK
- `testUpdateRestaurantPhoneFormatted()` — Phone with dashes OK
- `testUpdateRestaurantNameAtMinLength()` — Name exactly 3 chars OK
- `testUpdateRestaurantNameAtMaxLength()` — Name exactly 100 chars OK

**Cascade Relationship Tests**
- `testUpdateRestaurantWithProducts()` — Products remain associated
- `testUpdateRestaurantWithOrders()` — Orders remain associated
- `testUpdateRestaurantChangeNameWithProducts()` — Products link still valid
- `testUpdateRestaurantDoesNotAffectOtherRestaurants()` — Other restaurants unchanged

**Response Format Tests**
- `testUpdateRestaurantResponseFormat()` — Response is ApiResponseDTO
- `testUpdateRestaurantResponseParseable()` — JSON response valid and parseable
- `testUpdateRestaurantResponseFieldTypes()` — All fields have correct types

---

## 📊 Data Used or Modified

### Test Data Structure (POST Tests)

#### Valid Test Data - Complete Restaurant
```java
RestaurantCreateDTO validRestaurant = new RestaurantCreateDTO();
validRestaurant.setName("Pizza Palace");
validRestaurant.setAddress("123 Main Street, Springfield");
validRestaurant.setPhone("5551234567");
```

#### Valid Variations
```java
// Minimal data (only required fields)
RestaurantCreateDTO minimalRestaurant = new RestaurantCreateDTO();
minimalRestaurant.setName("Two");  // 3 char minimum
minimalRestaurant.setAddress("Sweet");  // 5 char minimum
minimalRestaurant.setPhone("1234567890");

// With unicode and special characters
RestaurantCreateDTO unicodeRestaurant = new RestaurantCreateDTO();
unicodeRestaurant.setName("Café Délice");
unicodeRestaurant.setAddress("456 Rue de la Paix, France");
unicodeRestaurant.setPhone("33-123-456789");

// At maximum lengths
RestaurantCreateDTO maxLengthRestaurant = new RestaurantCreateDTO();
maxLengthRestaurant.setName("A".repeat(100));  // Exactly 100 chars
maxLengthRestaurant.setAddress("B".repeat(200));  // Exactly 200 chars
maxLengthRestaurant.setPhone("12345678901234");  // Long format
```

#### Valid Test Assertions
```java
mockMvc.perform(post("/api/restaurants")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(validRestaurant)))
    .andExpect(status().isCreated())  // 201
    .andExpect(jsonPath("$.success").value(true))
    .andExpect(jsonPath("$.data.id").exists())
    .andExpect(jsonPath("$.data.name").value("Pizza Palace"))
    .andExpect(jsonPath("$.data.address").value("123 Main Street, Springfield"))
    .andExpect(jsonPath("$.data.phone").value("5551234567"))
    .andExpect(jsonPath("$.data.rating").exists())
    .andExpect(jsonPath("$.data.createdAt").exists())
    .andExpect(jsonPath("$.data.updatedAt").exists());

// Verify persistence
Restaurant saved = restaurantRepository.findAll().stream()
    .filter(r -> r.getName().equals("Pizza Palace"))
    .findFirst()
    .orElseThrow();
assertEquals(saved.getAddress(), "123 Main Street, Springfield");
```

#### Invalid Test Data - Missing Fields
```java
// Missing name
RestaurantCreateDTO noName = new RestaurantCreateDTO();
noName.setAddress("123 Main");
noName.setPhone("5551234567");

// Missing address
RestaurantCreateDTO noAddress = new RestaurantCreateDTO();
noAddress.setName("Pizza Palace");
noAddress.setPhone("5551234567");

// Missing phone
RestaurantCreateDTO noPhone = new RestaurantCreateDTO();
noPhone.setName("Pizza Palace");
noPhone.setAddress("123 Main");
```

#### Invalid Test Data - Format/Length Violations
```java
// Name too short
RestaurantCreateDTO shortName = new RestaurantCreateDTO();
shortName.setName("AB");  // Only 2 chars
shortName.setAddress("123 Main Street");
shortName.setPhone("5551234567");

// Name too long
RestaurantCreateDTO longName = new RestaurantCreateDTO();
longName.setName("A".repeat(101));  // 101 chars, exceeds 100 max
longName.setAddress("123 Main Street");
longName.setPhone("5551234567");

// Invalid phone
RestaurantCreateDTO badPhone = new RestaurantCreateDTO();
badPhone.setName("Pizza Palace");
badPhone.setAddress("123 Main Street");
badPhone.setPhone("NOTANUMBER");  // Non-numeric
```

#### Invalid Test Assertions
```java
mockMvc.perform(post("/api/restaurants")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(noName)))
    .andExpect(status().isBadRequest())  // 400
    .andExpect(jsonPath("$.success").value(false))
    .andExpect(jsonPath("$.message").exists())
    .andExpect(jsonPath("$.data").doesNotExist());

// Verify NOT persisted
List<Restaurant> allRestaurants = restaurantRepository.findAll();
assertTrue(allRestaurants.size() == 0 || 
    allRestaurants.stream().noneMatch(r -> r.getName() == null));
```

### Test Data Structure (PUT Tests)

#### Valid Update Data
```java
RestaurantUpdateDTO updateData = new RestaurantUpdateDTO();
updateData.setName("Pizza Palace 2.0");
updateData.setAddress("456 Oak Avenue");
updateData.setPhone("5559876543");
```

#### Partial Update Data
```java
// Update only name
RestaurantUpdateDTO nameOnly = new RestaurantUpdateDTO();
nameOnly.setName("New Pizza Palace");
// address and phone remain as null/unchanged

// Update only phone
RestaurantUpdateDTO phoneOnly = new RestaurantUpdateDTO();
phoneOnly.setPhone("5551111111");
```

#### Update with Same Values (Idempotency Test)
```java
RestaurantUpdateDTO sameValues = new RestaurantUpdateDTO();
sameValues.setName(originalRestaurant.getName());
sameValues.setAddress(originalRestaurant.getAddress());
sameValues.setPhone(originalRestaurant.getPhone());
// Should succeed with 200 OK, updatedAt changed
```

#### Update Validation Failure Data
```java
RestaurantUpdateDTO emptyName = new RestaurantUpdateDTO();
emptyName.setName("");  // Empty string — invalid
emptyName.setAddress(originalRestaurant.getAddress());
emptyName.setPhone(originalRestaurant.getPhone());
```

#### Update Test Assertions
```java
// Successful update
mockMvc.perform(put("/api/restaurants/{id}", restaurantId)
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(updateData)))
    .andExpect(status().isOk())  // 200
    .andExpect(jsonPath("$.success").value(true))
    .andExpect(jsonPath("$.data.id").value(restaurantId))
    .andExpect(jsonPath("$.data.name").value("Pizza Palace 2.0"))
    .andExpect(jsonPath("$.data.address").value("456 Oak Avenue"))
    .andExpect(jsonPath("$.data.phone").value("5559876543"));

// Verify in database
Restaurant updated = restaurantRepository.findById(restaurantId).orElseThrow();
assertEquals(updated.getName(), "Pizza Palace 2.0");
// Verify createdAt unchanged
assertEquals(updated.getCreatedAt(), original.getCreatedAt());
// Verify updatedAt changed
assertTrue(updated.getUpdatedAt().isAfter(original.getUpdatedAt()));

// Failed update (validation error)
mockMvc.perform(put("/api/restaurants/{id}", restaurantId)
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(emptyName)))
    .andExpect(status().isBadRequest());  // 400

// Verify NOT changed in database
Restaurant unchanged = restaurantRepository.findById(restaurantId).orElseThrow();
assertEquals(unchanged, original);  // All fields unchanged
```

---

## 🔒 Tech Constraints (Feature-Level)

- **Testing Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito for service dependencies
- **Integration Testing:** MockMvc for HTTP layer
- **Assertions:** AssertJ or JUnit 5 native
- **Test Scope:** Controller through service to repository
- **Data Cleanup:** @DirtiesContext or @Transactional for isolation
- **JSON Processing:** Jackson ObjectMapper for serialization/deserialization
- **HTTP Status Codes:** Follow REST conventions (201 for POST, 200 for PUT, 400 for validation, 404 for not found)
- **Request/Response Format:** ApiResponseDTO wrapper with success flag, message, and data
- **Validation Framework:** javax.validation or spring-boot-starter-validation annotations
- **Database Assertions:** Direct repository queries to verify persistence

---

## ✅ Acceptance Criteria

### POST Create Tests
- [ ] Test method exists for creating new restaurant
- [ ] POST returns 201 Created status
- [ ] Response includes complete restaurant object
- [ ] Response includes all fields: id, name, address, phone, rating, createdAt, updatedAt
- [ ] Restaurant ID is auto-assigned
- [ ] Timestamps (createdAt, updatedAt) are set to current time
- [ ] Rating defaults correctly (if applicable)
- [ ] Created restaurant persists to database
- [ ] All required field validations work (name, address, phone)
- [ ] Field length validations enforced (min/max lengths)
- [ ] Field format validations enforced (phone format)
- [ ] Invalid data returns 400 Bad Request
- [ ] Validation errors have clear, actionable messages
- [ ] Empty/null values rejected with proper error
- [ ] Whitespace-only values rejected with proper error
- [ ] Duplicate phone numbers rejected with clear error
- [ ] Special characters in name/address handled correctly
- [ ] Unicode characters supported in name/address
- [ ] Response format is valid ApiResponseDTO structure
- [ ] Error responses include error details

### PUT Update Tests  
- [ ] Test method exists for updating restaurant
- [ ] PUT returns 200 OK status
- [ ] Response includes complete updated restaurant
- [ ] Response includes all fields (id, name, address, phone, rating, createdAt, updatedAt)
- [ ] Updated values reflected in response
- [ ] Unchanged values preserved if partial update
- [ ] Updated restaurant persists to database
- [ ] Database query confirms update successful
- [ ] createdAt timestamp never changes on update
- [ ] updatedAt timestamp updates on every change
- [ ] Partial updates supported (can update single field)
- [ ] All required field validations work
- [ ] Field length validations enforced on updates
- [ ] Field format validations enforced on updates
- [ ] Cannot update to empty/null required fields
- [ ] Cannot update to duplicate phone number
- [ ] Invalid data returns 400 Bad Request
- [ ] Non-existent restaurant ID returns 404 Not Found
- [ ] Invalid ID format returns 400 Bad Request
- [ ] Validation errors have clear, actionable messages
- [ ] Error responses structured correctly
- [ ] Associated products remain linked after update
- [ ] Associated orders remain linked after update
- [ ] Update with same values succeeds (idempotent)
- [ ] Special characters supported on update
- [ ] Unicode characters supported on update
- [ ] Response format is valid ApiResponseDTO structure

### Overall Quality Tests
- [ ] All tests follow naming convention: `testMethod[Scenario][Expected]`
- [ ] Each test is independent and can run in any order
- [ ] Tests use proper setup/teardown (clean database between tests)
- [ ] Test data is realistic and covers edge cases
- [ ] All assertions are specific and meaningful
- [ ] Error messages are clear and help debugging
- [ ] Tests don't depend on database state from other tests
- [ ] Test suite runs in < 60 seconds
- [ ] All tests pass consistently (no flakiness)

---

## 📝 Implementation Notes for Developers

### Test Setup Pattern
```java
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class RestaurantApiControllerPostPutTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();  // Clean state for each test
    }
}
```

### POST Test Template
```java
@Test
void testCreateRestaurant[Scenario]() throws Exception {
    // Given: Prepare valid/invalid test data
    RestaurantCreateDTO requestData = new RestaurantCreateDTO();
    requestData.setName("Test Restaurant");
    requestData.setAddress("123 Test St");
    requestData.setPhone("5551234567");
    
    // When: Send POST request
    mockMvc.perform(post("/api/restaurants")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestData)))
    
    // Then: Verify response
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").exists())
        .andExpect(jsonPath("$.data.name").value("Test Restaurant"));
    
    // AND: Verify database persistence
    Restaurant saved = restaurantRepository.findAll().get(0);
    assertEquals(saved.getName(), "Test Restaurant");
}
```

### PUT Test Template
```java
@Test
void testUpdateRestaurant[Scenario]() throws Exception {
    // Given: Create a restaurant to update
    Restaurant original = restaurantRepository.save(
        new Restaurant("Original Name", "123 Main", "5551234567"));
    Long restaurantId = original.getId();
    
    // And: Prepare update data
    RestaurantUpdateDTO updateData = new RestaurantUpdateDTO();
    updateData.setName("Updated Name");
    updateData.setAddress("456 New St");
    updateData.setPhone("5559876543");
    
    // When: Send PUT request
    mockMvc.perform(put("/api/restaurants/{id}", restaurantId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateData)))
    
    // Then: Verify response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.name").value("Updated Name"));
    
    // AND: Verify database update
    Restaurant updated = restaurantRepository.findById(restaurantId).orElseThrow();
    assertEquals(updated.getName(), "Updated Name");
    assertEquals(updated.getCreatedAt(), original.getCreatedAt());  // Unchanged
    assertTrue(updated.getUpdatedAt().isAfter(original.getUpdatedAt()));  // Changed
}
```

### Validation Test Strategy
For each validation rule:
1. **Happy path:** Valid data succeeds with 201/200
2. **Violation:** Invalid data fails with 400
3. **Boundary:** Test min and max allowed values
4. **Edge cases:** Test empty strings, nulls, special characters, unicode

### Database Verification Best Practices
- Always query the database directly to verify persistence
- Use repository methods to confirm data state
- Test after each HTTP call to ensure atomicity
- For updates, verify both changed AND unchanged fields
- Check timestamps (createdAt should never change, updatedAt should always change)

### Error Message Testing
```java
@Test
void testCreateRestaurantMissingName() throws Exception {
    RestaurantCreateDTO noName = new RestaurantCreateDTO();
    noName.setAddress("123 Main");
    noName.setPhone("5551234567");
    
    mockMvc.perform(post("/api/restaurants")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(noName)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").exists())
        // Verify error message mentions which field is wrong
        .andExpect(jsonPath("$.message").value(containsString("name")));
}
```

### MockMvc Common Assertions Reference
```java
// Status
.andExpect(status().isCreated())    // 201
.andExpect(status().isOk())         // 200
.andExpect(status().isBadRequest()) // 400
.andExpect(status().isNotFound())   // 404

// JSON Path
.andExpect(jsonPath("$.success").value(true))
.andExpect(jsonPath("$.data.id").exists())
.andExpect(jsonPath("$.data.name").value("expected"))
.andExpect(jsonPath("$.data.id", allOf(notNullValue(), instanceOf(Number.class))))

// Collections
.andExpect(jsonPath("$.data", hasSize(2)))

// Matchers
.andExpect(jsonPath("$.message", containsString("name")))
.andExpect(jsonPath("$.timestamp").exists())
```
