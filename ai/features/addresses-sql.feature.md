# 🤖 AI_FEATURE_Address SQL

## 🎯 Feature Identity

- **Feature Name:** Address SQL - POST /api/address
- **Related Area:** Backend / API / Database

---

## 🎪 Feature Goal

Enable the creation of a new address in the database through a REST API endpoint using a native SQL INSERT query with parameterized bindings. The API must securely and reliably persist address data (street, city, state, zip code, country) to the database.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL INSERT query with parameterized bindings for the `address` table
- Request validation (ensure required fields are provided and valid)
- Response object containing the newly created address with its ID
- Proper error handling for invalid input or database errors
- HTTP endpoint: `POST /api/address`
- Service layer logic to delegate business logic from controller
- Integration with the Address repository using native SQL

### ❌ Out of Scope (Excluded)

- Modifying the Address entity model
- Authentication/authorization checks for this specific endpoint
- Address deletion or update endpoints
- Frontend UI or form validation
- Geo-location validation or address formatting
- Integration with external address services

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL Query:** Write a parameterized INSERT query in AddressRepository that adds a new address with all required fields
- **DTO Validation:** Ensure ApiAddressDTO contains all necessary address fields with proper annotations
- **Service Logic:** Create method in AddressService to handle address creation and return the created address
- **Controller Endpoint:** Implement POST method in a controller that receives the request, delegates to service, and returns response
- **Error Handling:** Handle validation errors, database errors, and return appropriate HTTP status codes
- **Response Format:** Use ResponseBuilder to construct consistent API response with the created address

---

## 👥 User Flow / Logic (High Level)

1. Mobile app sends a POST request to `/api/address` with address data (street, city, state, zip, country)
2. Controller receives and parses the request into ApiAddressDTO
3. Controller delegates to AddressService.createAddress(dto)
4. Service validates the DTO (if not already done by validation annotations)
5. Service calls AddressRepository.save(address) with the native SQL INSERT
6. Repository executes parameterized SQL query to insert address into database
7. Database returns the newly created address with generated ID
8. Service returns the created address to the controller
9. Controller returns HTTP 201 (Created) with the new address in response body
10. Mobile app receives the response with the new address ID for future reference

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

- **Endpoint:** `POST /api/address`
- **Request Body:** ApiAddressDTO
  - `street` (String, required)
  - `city` (String, required)
  - `state` (String, required)
  - `zipCode` (String, required)
  - `country` (String, required)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (Address) — The newly created address object with ID
- **HTTP Status Codes:**
  - 201 Created — Address successfully created
  - 400 Bad Request — Validation failed or missing required fields
  - 500 Internal Server Error — Database or server error

---

## 📊 Data Used or Modified

### Input Data (ApiAddressDTO)
- `street` (String) — Street address
- `city` (String) — City name
- `state` (String) — State or province
- `zipCode` (String) — Postal/ZIP code
- `country` (String) — Country name

### Output Data (Address Entity)
- `id` (Long) — Auto-generated primary key
- `street` (String) — Street address
- `city` (String) — City name
- `state` (String) — State or province
- `zipCode` (String) — Postal/ZIP code
- `country` (String) — Country name
- `createdAt` (LocalDateTime) — Timestamp when address was created

### Validations
- All fields are required (not null, not empty)
- `street` length: minimum 5, maximum 255 characters
- `city` length: minimum 2, maximum 100 characters
- `state` length: minimum 2, maximum 100 characters
- `zipCode` length: minimum 3, maximum 20 characters
- `country` length: minimum 2, maximum 100 characters

---

## 🔒 Tech Constraints (Feature-Level)

- **SQL Only:** Use native SQL INSERT query with parameterized bindings (no string concatenation)
- **Parameterized Binding:** Use `?1, ?2, ?3` notation or named parameters `@Param("field")`
- **No Hibernate Save:** Do not use Hibernate's `.save()` method — use native SQL INSERT only
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Response Builder:** All API responses must use ResponseBuilder utility
- **DTO Validation:** Use Jakarta/javax Bean Validation annotations on ApiAddressDTO
- **Service Pattern:** Controller → Service → Repository (business logic in service layer)

---

## ✅ Acceptance Criteria

- [ ] AddressRepository contains a parameterized SQL INSERT query method
- [ ] SQL query uses parameter bindings (no string concatenation)
- [ ] AddressService.createAddress(ApiAddressDTO) method implemented
- [ ] Controller POST /api/address endpoint implemented
- [ ] ApiAddressDTO has validation annotations for all required fields
- [ ] POST request with valid data creates address and returns 201 status
- [ ] POST request with invalid/missing fields returns 400 status
- [ ] POST request with empty string returns 400 status
- [ ] Response includes newly created address object with ID
- [ ] Response uses ResponseBuilder for consistent format
- [ ] Address is successfully persisted to database
- [ ] Unit tests pass for the service layer
- [ ] Integration tests pass for the controller endpoint

---

## 📝 Notes for the AI

- The **native SQL query is critical** — do not use Hibernate `.save()` or JPA methods. Use `@Query` annotation with INSERT statement.
- Always use **parameterized bindings** — example: `@Query("INSERT INTO address (street, city, state, zip_code, country) VALUES (?1, ?2, ?3, ?4, ?5)")`
- **Service layer handles all business logic** — controller should only parse request and delegate
- Write **tests first** (TDD) — test the SQL query, service layer, and controller endpoint before implementation
- The response must include the **newly created address object** with its database-generated ID
- Field names in the database may differ from Java names — use proper SQL column names and mapping
- Consider what happens when the address is created but the response fails — ensure transaction consistency
