# 🤖 AI_FEATURE_Users SQL

## 🎯 Feature Identity

- **Feature Name:** Users SQL - User Retrieval by ID
- **Related Area:** Backend / API / Database

---

## 🎪 Feature Goal

Enable retrieval of user information by user ID using native SQL queries. The API must securely fetch user data from the UserEntity base class and return user details (username, email, phone, role) with proper validation and error handling.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL SELECT query with parameterized bindings for retrieving a user by ID
- Request validation (ensure valid numeric user ID)
- Response object containing user data with appropriate fields
- Error handling for invalid queries, non-existent users, or database errors
- Service layer logic to delegate business logic from controller
- Integration with UserRepository using native SQL
- Support for retrieving users of different types (Customer, Courier, Employee all extend UserEntity)
- HTTP endpoint: `GET /api/user/{id}`

### ❌ Out of Scope (Excluded)

- Modifying the UserEntity model
- Creating or deleting users (CRUD is limited to READ only)
- Complex filtering by role, email, or username
- Pagination or sorting of users
- User authentication/login (handled by AuthApiController)
- User activation/deactivation
- Batch user retrieval
- Frontend UI or form validation
- User profile updates or modifications

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL SELECT Query:** Write parameterized SELECT query in UserRepository to fetch a user by ID from UserEntity table
- **Query Parameter Parsing:** Parse `id` from path parameter
- **Service Logic:** Implement method in UserService to handle user retrieval
- **Controller Endpoint:** Implement GET method in a controller to receive request and delegate to service
- **Error Handling:** Handle invalid ID format, non-existent users, and database errors with appropriate exceptions
- **Response Format:** Use ResponseBuilder to construct consistent API response
- **User Type Support:** Query should return UserEntity base class (Customer, Courier, Employee are subtypes)

---

## 👥 User Flow / Logic (High Level)

1. Mobile app sends GET request to `/api/user/7` (user ID)
2. Controller receives and validates `id` is a valid positive number
3. Controller delegates to UserService.getUserById(id)
4. Service calls UserRepository.findById(id) with parameterized SELECT
5. Repository executes: `SELECT * FROM user_entity WHERE id = ?1`
6. Database returns user record (may be Customer, Courier, or Employee based on type column)
7. Service returns user to controller
8. Controller returns HTTP 200 with user data in response body
9. If user not found → return 404 Not Found
10. If ID invalid format → return 400 Bad Request
11. Response includes user ID, username, email, phone, role, and type information

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### GET /api/user/{id} (Retrieve)
- **Path Parameter:**
  - `id` (Long, required) — User ID to retrieve
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (ApiGetAccountDTO) — User details
- **HTTP Status Codes:**
  - 200 OK — User retrieved successfully
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — User does not exist
  - 500 Internal Server Error — Database or server error

---

## 📊 Data Used or Modified

### GET User Input
- `id` (Long) — User ID to retrieve
- **Validations:**
  - `id` must be a positive integer > 0
  - `id` must exist in user_entity table

### GET User Output (ApiGetAccountDTO)
- `id` (Long) — User ID
- `username` (String) — Login username
- `email` (String) — Email address
- `phone` (String) — Contact phone number
- `role` (String) — User role (ADMIN, CUSTOMER, COURIER, EMPLOYEE, RESTAURANT_OWNER)
- `dtype` (String) — Discriminator type (Customer, Courier, Employee) for polymorphic queries
- `createdAt` (LocalDateTime) — When user was created
- `updatedAt` (LocalDateTime) — When user was last updated

### Extended Fields (Based on User Type)

#### If Customer
- `address` (Address) — Customer's delivery address
- `rating` (Double) — Customer rating (0.0-5.0)

#### If Courier
- `vehicle` (String) — Vehicle type (motorcycle, car, bike)
- `availability` (CourierStatus) — Current availability status

#### If Employee
- `restaurantId` (Long) — Associated restaurant

### Data Not Modified
- This is a read-only operation; no data is modified

---

## 🔒 Tech Constraints (Feature-Level)

- **SQL Only:** Use native SQL SELECT query with parameterized bindings (no Hibernate `.findById()` or JPA methods)
- **Parameterized Binding:** Use `?1` notation or named parameters `@Param("id")`
- **No Hibernate Methods:** Do not use `.findById()`, `.getById()`, or other convenience methods
- **Polymorphic Query:** The query should work with UserEntity and its subtypes (Customer, Courier, Employee)
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Response Builder:** All API responses must use ResponseBuilder utility
- **Query Parameter Validation:** Validate `id` is numeric in controller or service before querying
- **Service Pattern:** Controller → Service → Repository
- **No Password Returns:** Ensure password field is never included in response DTO

---

## ✅ Acceptance Criteria

- [ ] UserRepository contains parameterized SQL SELECT query
- [ ] SELECT query uses parameter binding (?1 for ID)
- [ ] Query does not return password field
- [ ] UserService.getUserById(id) method implemented
- [ ] Controller GET /api/user/{id} endpoint implemented
- [ ] GET request with valid user ID returns 200 status with user data
- [ ] GET response includes id, username, email, phone, role
- [ ] GET response does NOT include password
- [ ] GET request with non-existent user ID returns 404 status
- [ ] GET request with invalid ID format (non-numeric) returns 400 status
- [ ] GET request with negative ID returns 400 status
- [ ] GET request with ID of type Customer returns customer-specific fields
- [ ] GET request with ID of type Courier returns courier-specific fields
- [ ] GET request with ID of type Employee returns employee-specific fields
- [ ] Response uses ResponseBuilder for consistent format
- [ ] Response includes all required user fields
- [ ] User is successfully retrieved from database
- [ ] Unit tests pass for service layer
- [ ] Integration tests pass for controller endpoint
- [ ] TDD workflow enforced (tests written before implementation)

---

## 📝 Notes for the AI

- **Native SQL is critical** — Do not use Hibernate's `.findById()` or Spring Data JPA convenience methods. Use `@Query` annotation with parameterized SELECT.
- **Parameter binding example:** `@Query("SELECT * FROM user_entity WHERE id = ?1")`
- **Polymorphic inheritance:** UserEntity is a base class with subtypes (Customer, Courier, Employee). The SELECT query should return the base type, and the DTO mapping should handle the polymorphism. The `dtype` discriminator column determines the actual type.
- **Password security:** The response DTO (ApiGetAccountDTO) must NOT include the password field. Ensure the SQL query or DTO mapping excludes sensitive fields.
- **Service layer pattern:** The service should handle parameter validation before calling the repository, keeping the controller thin.
- **Not found handling:** If a user doesn't exist, return 404 Not Found (not an empty object). Use ResourceNotFoundException.
- **Subtype information:** The response may include additional fields based on user type (customer address/rating, courier vehicle/availability, employee restaurant_id). Ensure the DTO or response handles this polymorphism correctly.
- **Write tests first (TDD):** Test cases should include:
  1. Valid user ID (returns user data)
  2. Non-existent user ID (returns 404)
  3. Invalid ID format (non-numeric, negative, zero)
  4. Different user types (Customer, Courier, Employee)
  5. Verify password is never returned
- **Integration point:** This endpoint is called by mobile apps to retrieve user profile information and may be used by other services to validate user existence.
- **Performance note:** Consider if caching is needed for frequently accessed users (future optimization, not in scope)
