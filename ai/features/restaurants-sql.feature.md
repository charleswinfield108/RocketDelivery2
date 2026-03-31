# 🤖 AI_FEATURE_Restaurants SQL

## 🎯 Feature Identity

- **Feature Name:** Restaurants SQL - Complete CRUD Operations
- **Related Area:** Backend / API / Database

---

## 🎪 Feature Goal

Enable full Create, Read, Update, and Delete (CRUD) operations for restaurants using native SQL queries. The API must securely manage restaurant data (name, address, phone, rating) with comprehensive validation, and support cascade deletion to remove associated products and orders when a restaurant is deleted.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Native SQL INSERT query with parameterized bindings for creating a new restaurant
- Native SQL UPDATE query with parameterized bindings for updating an existing restaurant
- Native SQL SELECT query with parameterized bindings for retrieving a restaurant by ID
- Native SQL DELETE query with parameterized bindings for deleting a restaurant by ID
- Request validation (ensure required fields are provided and valid)
- Response objects containing restaurant data with proper status codes
- Error handling for invalid queries, non-existent restaurants, validation errors, and database errors
- Cascade deletion handling (remove associated products and orders when restaurant is deleted)
- Service layer logic to delegate business logic from controller
- Integration with RestaurantRepository using native SQL
- RestaurantApiController endpoints for all four operations

### ❌ Out of Scope (Excluded)

- Modifying the Restaurant entity model
- Complex search/filtering by rating, cuisine type, or location
- Pagination or sorting of restaurants (simple queries only)
- Geo-location or address validation services
- Bulk restaurant creation or deletion
- Restaurant activation/deactivation (permanent delete only)
- Authentication/authorization logic (assume user is authenticated)
- Restaurant image uploads or media handling

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **SQL INSERT Query:** Write parameterized INSERT query in RestaurantRepository to create a new restaurant with name, address, phone, and initial rating
- **SQL UPDATE Query:** Write parameterized UPDATE query in RestaurantRepository to update restaurant fields (name, address, phone, rating)
- **SQL SELECT Query:** Write parameterized SELECT query in RestaurantRepository to fetch a restaurant by ID with all fields
- **SQL DELETE Query:** Write parameterized DELETE query in RestaurantRepository to remove a restaurant and cascade delete associated products and orders
- **DTO Validation:** Ensure ApiCreateRestaurantDTO and ApiRestaurantDTO contain proper validation annotations
- **Service Methods:** Implement CRUD service methods in RestaurantService
- **Controller Endpoints:** Implement POST, PUT, GET, and DELETE endpoints in RestaurantApiController
- **Error Handling:** Handle validation errors, non-existent records, database errors with appropriate exceptions
- **Response Format:** Use ResponseBuilder for all API responses
- **Cascade Deletion:** Ensure products and product_orders are deleted when restaurant is deleted

---

## 👥 User Flow / Logic (High Level)

### CREATE Restaurant Flow
1. Request: POST /api/restaurants with restaurant data (name, address, phone)
2. Controller validates input and creates ApiCreateRestaurantDTO
3. Service validates DTO constraints
4. Service calls RestaurantRepository.save(restaurant) with parameterized INSERT
5. Repository executes: INSERT INTO restaurant (name, address, phone, rating) VALUES (?1, ?2, ?3, ?4)
6. Database auto-generates restaurant ID and returns new restaurant
7. Service returns created restaurant to controller
8. Controller returns HTTP 201 (Created) with new restaurant object
9. Response includes restaurant ID for future reference

### READ Restaurant Flow
1. Request: GET /api/restaurant/{id} with restaurant ID
2. Controller validates ID is valid positive number
3. Service calls RestaurantRepository.findById(id) with parameterized SELECT
4. Repository executes: SELECT * FROM restaurant WHERE id = ?1
5. Database returns restaurant record
6. Service returns restaurant to controller
7. Controller returns HTTP 200 with restaurant data
8. If not found → return 404 Not Found

### UPDATE Restaurant Flow
1. Request: PUT /api/restaurants/{id} with updated data (name, address, phone, rating)
2. Controller validates input and verifies restaurant exists
3. Service validates DTO constraints
4. Service calls RestaurantRepository.update(id, data) with parameterized UPDATE
5. Repository executes: UPDATE restaurant SET name = ?1, address = ?2, phone = ?3, rating = ?4 WHERE id = ?5
6. Database updates record and returns updated restaurant
7. Service returns updated restaurant to controller
8. Controller returns HTTP 200 with updated restaurant
9. If not found → return 404 Not Found

### DELETE Restaurant Flow
1. Request: DELETE /api/restaurants/{id} with restaurant ID
2. Controller validates ID is valid and verifies restaurant exists
3. Service initiates cascade deletion:
   a. Delete all products for restaurant: DELETE FROM product WHERE restaurant_id = ?1
   b. Delete all product_orders for those products: DELETE FROM product_order WHERE product_id IN (...)
   c. Delete all orders from that restaurant: DELETE FROM order WHERE restaurant_id = ?1
   d. Delete the restaurant: DELETE FROM restaurant WHERE id = ?1
4. Service returns confirmation
5. Controller returns HTTP 200 or 204 (No Content)
6. If not found → return 404 Not Found

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### POST /api/restaurants (Create)
- **Request Body:** ApiCreateRestaurantDTO
  - `name` (String, required) — Restaurant name
  - `address` (String, required) — Restaurant address
  - `phone` (String, required) — Contact phone number
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code (201)
  - `message` (String) — Success message
  - `data` (ApiRestaurantDTO) — Newly created restaurant with ID
- **HTTP Status Codes:**
  - 201 Created — Restaurant successfully created
  - 400 Bad Request — Validation failed (missing/invalid fields)
  - 500 Internal Server Error — Database error

#### GET /api/restaurant/{id} (Retrieve)
- **Path Parameter:**
  - `id` (Long, required) — Restaurant ID to retrieve
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code (200)
  - `message` (String) — Success message
  - `data` (ApiRestaurantDTO) — Restaurant details
- **HTTP Status Codes:**
  - 200 OK — Restaurant retrieved successfully
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

#### PUT /api/restaurants/{id} (Update)
- **Path Parameter:**
  - `id` (Long, required) — Restaurant ID to update
- **Request Body:** ApiRestaurantDTO (may include name, address, phone, rating)
  - `name` (String, optional) — Updated restaurant name
  - `address` (String, optional) — Updated address
  - `phone` (String, optional) — Updated phone
  - `rating` (Double, optional) — Updated rating
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code (200)
  - `message` (String) — Success message
  - `data` (ApiRestaurantDTO) — Updated restaurant
- **HTTP Status Codes:**
  - 200 OK — Restaurant successfully updated
  - 400 Bad Request — Validation failed or invalid ID
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

#### DELETE /api/restaurants/{id} (Delete)
- **Path Parameter:**
  - `id` (Long, required) — Restaurant ID to delete
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code (200/204)
  - `message` (String) — Success or error message
  - `data` (null)
- **HTTP Status Codes:**
  - 200 OK — Restaurant successfully deleted
  - 204 No Content — Restaurant successfully deleted (no body)
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

---

## 📊 Data Used or Modified

### CREATE Input (ApiCreateRestaurantDTO)
- `name` (String) — Restaurant name, 3-200 characters
- `address` (String) — Restaurant address, 5-255 characters
- `phone` (String) — Phone number, valid format (10+ digits)
- **Validations:**
  - All fields required (not null, not empty)
  - Name: minLength=3, maxLength=200
  - Address: minLength=5, maxLength=255
  - Phone: valid phone format, minLength=10

### READ Output (ApiRestaurantDTO)
- `id` (Long) — Restaurant ID (auto-generated)
- `name` (String) — Restaurant name
- `address` (String) — Restaurant address
- `phone` (String) — Contact phone
- `rating` (Double) — Average rating (0.0-5.0)
- `createdAt` (LocalDateTime) — Creation timestamp
- `updatedAt` (LocalDateTime) — Last update timestamp

### UPDATE Input (ApiRestaurantDTO)
- `name` (String, optional) — New restaurant name
- `address` (String, optional) — New address
- `phone` (String, optional) — New phone
- `rating` (Double, optional) — New rating
- **Validations:** Same as CREATE for provided fields

### UPDATE Output
- Same as ApiRestaurantDTO with updated values

### DELETE Input
- `id` (Long) — Restaurant ID to delete
- **Validations:**
  - ID must be positive integer > 0
  - Restaurant must exist

### Data Modified
- **restaurant table:** Row with matching ID is deleted
- **product table:** All products with matching restaurant_id are deleted (cascade)
- **product_order table:** All product_orders for deleted products are deleted (cascade)
- **order table:** All orders for this restaurant are deleted or marked as orphaned (depending on constraint)

---

## 🔒 Tech Constraints (Feature-Level)

- **SQL Only:** Use native SQL queries with parameterized bindings (no Hibernate `.save()`, `.findById()`, or `.delete()`)
- **Parameterized Binding:** Use `?1, ?2, ?3` notation or named parameters `@Param("field")`
- **No Hibernate Methods:** Do not use repository `.save()`, `.findById()`, or `.delete()` convenience methods
- **Cascade Delete Complexity:** When deleting a restaurant, must handle:
  1. Delete all products for restaurant
  2. Delete all product_order entries for those products
  3. Delete all orders from restaurant (or handle as orphaned)
  4. Delete the restaurant itself
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Response Builder:** All API responses must use ResponseBuilder utility
- **DTO Validation:** Use Jakarta/javax validation annotations on DTOs
- **Service Pattern:** Controller → Service → Repository (business logic in service layer)
- **Atomic Operations:** Cascade delete should be atomic (all-or-nothing)

---

## ✅ Acceptance Criteria

### CREATE Tests
- [ ] RestaurantRepository contains parameterized SQL INSERT query
- [ ] INSERT query uses parameter bindings (?1, ?2, ?3, ?4)
- [ ] POST /api/restaurants endpoint implemented
- [ ] POST with valid data creates restaurant and returns 201 status
- [ ] Created restaurant has auto-generated ID
- [ ] POST with missing required field returns 400 status
- [ ] POST with empty string returns 400 status
- [ ] POST with invalid phone format returns 400 status
- [ ] Response includes newly created restaurant with all fields

### READ Tests
- [ ] RestaurantRepository contains parameterized SQL SELECT query
- [ ] SELECT query uses parameter binding (?1 for ID)
- [ ] GET /api/restaurant/{id} endpoint implemented
- [ ] GET with valid ID returns 200 status with restaurant data
- [ ] GET with non-existent ID returns 404 status
- [ ] GET with invalid ID format returns 400 status
- [ ] Response includes all restaurant fields

### UPDATE Tests
- [ ] RestaurantRepository contains parameterized SQL UPDATE query
- [ ] UPDATE query uses parameter bindings for all fields
- [ ] PUT /api/restaurants/{id} endpoint implemented
- [ ] PUT with valid data updates restaurant and returns 200 status
- [ ] Updated restaurant has same ID
- [ ] PUT with missing ID returns 400 status
- [ ] PUT with non-existent ID returns 404 status
- [ ] PUT with invalid field data returns 400 status
- [ ] Response includes updated restaurant with new values

### DELETE Tests
- [ ] RestaurantRepository contains parameterized SQL DELETE query
- [ ] DELETE query uses parameter binding for ID
- [ ] Service implements cascade delete logic
- [ ] DELETE /api/restaurants/{id} endpoint implemented
- [ ] DELETE with valid ID deletes restaurant and returns 200/204 status
- [ ] DELETE cascade removes all associated products
- [ ] DELETE cascade removes all associated product_orders
- [ ] DELETE cascade removes all associated orders
- [ ] DELETE with non-existent ID returns 404 status
- [ ] DELETE with invalid ID format returns 400 status

### General Tests
- [ ] All SQL queries use parameterized bindings (no concatenation)
- [ ] All responses use ResponseBuilder for consistency
- [ ] RestaurantApiController has all 4 endpoints implemented
- [ ] RestaurantService has all 4 CRUD methods implemented
- [ ] Service layer contains all business logic
- [ ] Controller only delegates to service
- [ ] Exception handling works correctly for all error cases
- [ ] Unit tests pass for service layer
- [ ] Integration tests pass for controller endpoints
- [ ] TDD workflow enforced (tests before implementation)

---

## 📝 Notes for the AI

- **Native SQL is critical** — This is NOT a simple repository; use `@Query` annotations exclusively. Do NOT use Spring Data JPA convenience methods like `.save()`, `.findById()`, or `.delete()`.
- **Parameter binding examples:**
  - INSERT: `@Query("INSERT INTO restaurant (name, address, phone, rating) VALUES (?1, ?2, ?3, ?4)")`
  - SELECT: `@Query("SELECT * FROM restaurant WHERE id = ?1")`
  - UPDATE: `@Query("UPDATE restaurant SET name = ?1, address = ?2 WHERE id = ?3")`
  - DELETE: `@Query("DELETE FROM restaurant WHERE id = ?1")`
- **Cascade delete is complex:** The DELETE operation requires careful handling:
  1. Must delete product_order entries BEFORE deleting products (foreign key constraints)
  2. Must delete products BEFORE deleting restaurant
  3. Consider whether to delete orders or leave them orphaned (design decision)
  4. Recommend handling in service layer with multiple repository calls
- **Service layer pattern:** The service should orchestrate the cascade delete, making multiple repository calls in the correct order
- **Return values:** Repository methods should return:
  - CREATE: The newly created restaurant with ID
  - READ: The restaurant object or empty optional
  - UPDATE: The updated restaurant object
  - DELETE: Integer (rows affected) or void
- **RestaurantApiController note:** Pre-written tests exist for GET and CREATE; ensure implementation matches expected behavior
- **Write tests first (TDD):** Test all happy paths and error cases before implementing SQL
- **Note on existing code:** Two restaurant tests are pre-written; implementation must work with these tests
- **Integration point:** These four endpoints are fundamental to the entire API — ensure robust implementation
