# 🤖 AI_FEATURE_Restaurants API

## 🎯 Feature Identity

- **Feature Name:** Restaurants API - Complete CRUD REST Endpoints
- **Related Area:** Backend / API / Controller

---

## 🎪 Feature Goal

Provide a complete REST API interface for restaurant management that allows mobile apps and clients to retrieve, create, update, and delete restaurants. The API must return properly formatted responses with restaurant data, handle validation errors gracefully, support filtering and pagination where applicable, and maintain consistency across all endpoints using the ResponseBuilder utility.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- GET /api/restaurants endpoint to retrieve all restaurants (with optional pagination/filtering)
- GET /api/restaurants/{id} endpoint to retrieve a single restaurant by ID
- POST /api/restaurants endpoint to create a new restaurant
- PUT /api/restaurants/{id} endpoint to update an existing restaurant
- DELETE /api/restaurants/{id} endpoint to delete a restaurant
- Request validation for input data (name, address, phone)
- Response consistency using ResponseBuilder utility
- Proper HTTP status codes for all outcomes (200, 201, 400, 404, 500)
- Error handling with appropriate error messages
- RestaurantApiController implementation with all 5 endpoints
- Integration with RestaurantService and RestaurantRepository

### ❌ Out of Scope (Excluded)

- Modifying Restaurant entity model
- Search by restaurant name or cuisine type
- Filtering by rating or location
- Advanced pagination (beyond basic implementation)
- Restaurant image/file uploads
- Complex business logic (handled in service layer)
- Authentication/authorization (assumed pre-authenticated)
- Batch operations (create/delete multiple restaurants)

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **GET All Restaurants:** Implement endpoint to retrieve list of all restaurants with basic metadata
- **GET Single Restaurant:** Implement endpoint to retrieve detailed info for one restaurant by ID
- **CREATE Restaurant:** Implement endpoint to accept restaurant data, validate, and persist new restaurant
- **UPDATE Restaurant:** Implement endpoint to modify existing restaurant and return updated data
- **DELETE Restaurant:** Implement endpoint to remove restaurant and handle cascade deletion
- **Request Parsing:** Parse and validate JSON request bodies for POST/PUT operations
- **Response Formatting:** Use ResponseBuilder to construct consistent ApiResponseDTO for all responses
- **Error Handling:** Return appropriate error responses for validation failures and edge cases
- **Controller Logic:** Controllers delegate all business logic to service layer (stay thin)
- **Status Codes:** Return correct HTTP status codes (200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Error)

---

## 👥 User Flow / Logic (High Level)

### GET All Restaurants Flow
1. Mobile app requests GET /api/restaurants
2. Controller receives request and delegates to RestaurantService.getAllRestaurants()
3. Service calls RestaurantRepository to fetch all restaurants from database
4. Repository returns list of restaurants
5. Service optionally filters or processes list
6. Controller constructs response using ResponseBuilder with list of ApiRestaurantDTOs
7. Controller returns HTTP 200 with restaurants array in response body

### GET Single Restaurant Flow
1. Mobile app requests GET /api/restaurants/3
2. Controller receives restaurant ID and validates it's numeric
3. Controller delegates to RestaurantService.getRestaurantById(3)
4. Service calls RestaurantRepository.findById(3)
5. Repository executes SQL query and returns restaurant
6. If not found → Service throws ResourceNotFoundException
7. Service returns restaurant to controller
8. Controller constructs response using ResponseBuilder with single ApiRestaurantDTO
9. Controller returns HTTP 200 with restaurant data
10. If not found → Controller returns HTTP 404 Not Found

### CREATE Restaurant Flow
1. Mobile app sends POST /api/restaurants with name, address, phone in JSON body
2. Controller deserializes JSON into ApiCreateRestaurantDTO
3. Spring validation annotations validate DTO fields
4. If validation fails → return 400 Bad Request with errors
5. Controller delegates to RestaurantService.createRestaurant(dto)
6. Service validates business rules (if any)
7. Service creates Restaurant entity from DTO
8. Service calls RestaurantRepository.save(restaurant) (native SQL INSERT)
9. Repository executes parameterized INSERT query
10. Database generates restaurant ID and persists record
11. Service returns created restaurant to controller
12. Controller constructs response using ResponseBuilder
13. Controller returns HTTP 201 Created with new restaurant data
14. Response includes newly generated restaurant ID

### UPDATE Restaurant Flow
1. Mobile app sends PUT /api/restaurants/3 with updated fields (name, address, phone, rating)
2. Controller receives restaurant ID and DTO
3. Controller verifies restaurant exists (calls service or validates)
4. Spring validation validates DTO fields against constraints
5. If validation fails → return 400 Bad Request
6. Controller delegates to RestaurantService.updateRestaurant(id, dto)
7. Service retrieves current restaurant
8. Service applies updates from DTO to entity
9. Service validates updated entity
10. Service calls RestaurantRepository.update(id, restaurant)
11. Repository executes parameterized UPDATE query
12. Database updates record and persists changes
13. Service returns updated restaurant to controller
14. Controller constructs response using ResponseBuilder
15. Controller returns HTTP 200 OK with updated restaurant data

### DELETE Restaurant Flow
1. Mobile app sends DELETE /api/restaurants/3
2. Controller receives restaurant ID and validates it's numeric
3. Controller verifies restaurant exists
4. Controller delegates to RestaurantService.deleteRestaurant(3)
5. Service initiates cascade deletion:
   - Delete all products for restaurant
   - Delete all product_orders for those products
   - Delete all orders from restaurant (or handle as orphaned)
   - Delete the restaurant
6. Service calls RestaurantRepository.delete(3)
7. Repository executes parameterized DELETE query(ies)
8. Database removes records
9. Service returns confirmation to controller
10. Controller constructs response using ResponseBuilder
11. Controller returns HTTP 200 OK or 204 No Content
12. If restaurant not found → return 404 Not Found

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### GET /api/restaurants (List All)
- **Request Parameters:** (Optional)
  - `page` (Integer, optional) — Page number for pagination (0-based)
  - `size` (Integer, optional) — Number of restaurants per page
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (List<ApiRestaurantDTO>) — Array of restaurant objects
- **Response Fields per Restaurant:**
  - `id` (Long) — Restaurant ID
  - `name` (String) — Restaurant name
  - `address` (String) — Restaurant address
  - `phone` (String) — Contact phone
  - `rating` (Double) — Average rating (0.0-5.0)
  - `createdAt` (LocalDateTime) — Creation timestamp
  - `updatedAt` (LocalDateTime) — Last update timestamp
- **HTTP Status Codes:**
  - 200 OK — Restaurants retrieved successfully
  - 500 Internal Server Error — Database error

#### GET /api/restaurants/{id} (Retrieve One)
- **Path Parameter:**
  - `id` (Long, required) — Restaurant ID
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiRestaurantDTO) — Single restaurant object with all fields
- **HTTP Status Codes:**
  - 200 OK — Restaurant retrieved
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

#### POST /api/restaurants (Create)
- **Request Body:** ApiCreateRestaurantDTO
  - `name` (String, required) — Restaurant name (3-200 characters)
  - `address` (String, required) — Address (5-255 characters)
  - `phone` (String, required) — Phone number (10+ characters)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 201
  - `message` (String) — Success message
  - `data` (ApiRestaurantDTO) — Newly created restaurant with assigned ID
- **HTTP Status Codes:**
  - 201 Created — Restaurant successfully created
  - 400 Bad Request — Validation failed (missing/invalid fields)
  - 500 Internal Server Error — Database error

#### PUT /api/restaurants/{id} (Update)
- **Path Parameter:**
  - `id` (Long, required) — Restaurant ID to update
- **Request Body:** ApiRestaurantDTO (partial update)
  - `name` (String, optional) — Updated name
  - `address` (String, optional) — Updated address
  - `phone` (String, optional) — Updated phone
  - `rating` (Double, optional) — Updated rating (0.0-5.0)
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiRestaurantDTO) — Updated restaurant object
- **HTTP Status Codes:**
  - 200 OK — Restaurant successfully updated
  - 400 Bad Request — Validation failed or invalid ID
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

#### DELETE /api/restaurants/{id} (Delete)
- **Path Parameter:**
  - `id` (Long, required) — Restaurant ID to delete
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200 or 204
  - `message` (String) — Success message
  - `data` (null or deletion confirmation)
- **HTTP Status Codes:**
  - 200 OK — Restaurant successfully deleted
  - 204 No Content — Restaurant successfully deleted (no body)
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

---

## 📊 Data Used or Modified

### Request Data (Inbound)

#### ApiCreateRestaurantDTO (POST)
- `name` (String) — Restaurant name
  - Validation: @NotBlank, @Size(min=3, max=200)
- `address` (String) — Street address
  - Validation: @NotBlank, @Size(min=5, max=255)
- `phone` (String) — Contact phone
  - Validation: @NotBlank, @Pattern (valid phone format), @Size(min=10)

#### ApiRestaurantDTO (PUT - Partial Update)
- `name` (String, optional)
- `address` (String, optional)
- `phone` (String, optional)
- `rating` (Double, optional)
  - Validation: @Min(0), @Max(5) if provided

### Response Data (Outbound)

#### ApiRestaurantDTO (GET/POST/PUT Responses)
- `id` (Long) — Database-generated restaurant ID
- `name` (String) — Restaurant name
- `address` (String) — Full address
- `phone` (String) — Contact phone
- `rating` (Double) — Rating between 0.0 and 5.0
- `createdAt` (LocalDateTime) — ISO 8601 timestamp
- `updatedAt` (LocalDateTime) — ISO 8601 timestamp

#### ApiResponseDTO (All Responses)
- `statusCode` (int) — HTTP status code (200, 201, 400, 404, 500)
- `message` (String) — Human-readable message
- `data` (Object) — Response data (LIST, SINGLE, or null)
- `timestamp` (LocalDateTime) — When response was generated

### Data Constraints
- `name`: 3-200 characters, must be unique (optional)
- `address`: 5-255 characters
- `phone`: Minimum 10 characters, valid format
- `rating`: Double between 0.0 and 5.0
- `id`: Generated by database, positive integer

---

## 🔒 Tech Constraints (Feature-Level)

- **Controller Pattern:** Thin controllers that parse requests and delegate to services
- **Service Pattern:** Service layer contains all business logic and validation
- **Response Format:** ALL responses must use ResponseBuilder utility
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException) with GlobalExceptionHandler
- **Validation:** Use Jakarta validation annotations (@NotBlank, @Size, @Min, @Max, @Pattern)
- **REST Principles:** Follow REST conventions for HTTP methods and status codes
- **DTO Usage:** Use proper DTOs for request/response serialization (do not expose entities directly)
- **Service Calls:** Controllers only call service methods, never directly call repositories
- **Status Codes:** Must return correct codes (200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Error)
- **No Pagination Complexity:** Basic pagination only (page, size parameters)

---

## ✅ Acceptance Criteria

### GET All Restaurants Tests
- [ ] GET /api/restaurants endpoint exists and responds
- [ ] GET request returns HTTP 200 status
- [ ] Response includes statusCode, message, data fields
- [ ] Response data is array (even if empty)
- [ ] Each restaurant in array includes: id, name, address, phone, rating, createdAt, updatedAt
- [ ] All restaurants from database are returned
- [ ] Empty database returns 200 with empty array (not error)
- [ ] Response uses ResponseBuilder format
- [ ] Response Content-Type is application/json

### GET Single Restaurant Tests
- [ ] GET /api/restaurants/{id} endpoint exists
- [ ] GET with valid ID returns HTTP 200
- [ ] Response includes complete restaurant data
- [ ] Response includes all required fields (id, name, address, phone, rating, timestamps)
- [ ] GET with non-existent ID returns HTTP 404
- [ ] GET with invalid ID format (non-numeric) returns HTTP 400
- [ ] GET with ID = 0 or negative returns HTTP 400
- [ ] Response uses ResponseBuilder format

### POST Create Restaurant Tests
- [ ] POST /api/restaurants endpoint exists
- [ ] POST with valid data returns HTTP 201 Created status
- [ ] Response includes newly created restaurant object
- [ ] Created restaurant has auto-generated ID
- [ ] Created restaurant persists to database
- [ ] POST with missing name field returns HTTP 400
- [ ] POST with missing address field returns HTTP 400
- [ ] POST with missing phone field returns HTTP 400
- [ ] POST with empty strings returns HTTP 400
- [ ] POST with name < 3 characters returns HTTP 400
- [ ] POST with address < 5 characters returns HTTP 400
- [ ] POST with phone < 10 characters returns HTTP 400
- [ ] POST with invalid phone format returns HTTP 400
- [ ] Response includes restaurant with all fields populated
- [ ] Response uses ResponseBuilder format
- [ ] New restaurant is queryable via GET /api/restaurants/{id}

### PUT Update Restaurant Tests
- [ ] PUT /api/restaurants/{id} endpoint exists
- [ ] PUT with valid data returns HTTP 200
- [ ] PUT updates restaurant in database
- [ ] Response includes updated restaurant object
- [ ] PUT with non-existent ID returns HTTP 404
- [ ] PUT with invalid ID format returns HTTP 400
- [ ] PUT with invalid field data returns HTTP 400
- [ ] PUT with empty string field returns HTTP 400
- [ ] PUT can update individual fields (partial update)
- [ ] PUT with only name field updates only name
- [ ] PUT does not lose other fields (idempotent)
- [ ] Updated timestamp reflects the update time
- [ ] Restaurant ID does not change after update
- [ ] Response uses ResponseBuilder format
- [ ] Updated restaurant is immediately queryable

### DELETE Restaurant Tests
- [ ] DELETE /api/restaurants/{id} endpoint exists
- [ ] DELETE with valid ID returns HTTP 200 or 204
- [ ] DELETE removes restaurant from database
- [ ] DELETE with non-existent ID returns HTTP 404
- [ ] DELETE with invalid ID format returns HTTP 400
- [ ] DELETE cascades to remove products (if implemented)
- [ ] DELETE cascades to remove product_orders (if implemented)
- [ ] Deleted restaurant cannot be retrieved via GET
- [ ] Response uses ResponseBuilder format

### Integration Tests
- [ ] All 5 endpoints use same response format (ResponseBuilder)
- [ ] Error messages are consistent across endpoints
- [ ] Status codes follow REST conventions
- [ ] All endpoints return application/json Content-Type
- [ ] Controller does not directly access repository (only through service)
- [ ] Service layer is called by controller, not repository
- [ ] All validation annotations work correctly
- [ ] GlobalExceptionHandler catches all exceptions and formats responses

---

## 📝 Notes for the AI

- **Pre-written Tests:** Two tests are pre-written for this controller (GET /api/restaurants and GET /api/restaurants/{id}). Implementation must pass these tests without modification. Examine test file to understand expected behavior.
- **Controller Responsibilities:** Controllers should:
  - Receive HTTP request
  - Parse path parameters and request body
  - Validate inputs (or rely on validation annotations)
  - Delegate to service layer
  - Return response using ResponseBuilder
  - Handle exceptions from service layer
- **Service Responsibilities:** Services should:
  - Contain all business logic
  - Call repository methods
  - Handle data transformation
  - Execute cascade operations (like cascade delete)
  - Throw appropriate exceptions
- **Thin Controllers:** Controllers should have minimal logic — validation and service delegation only
- **Exception Flow:** If service throws exception, GlobalExceptionHandler catches it and formats response. Controller doesn't need try-catch unless handling specific logic.
- **Data Transfer:** Always use DTOs for request/response. Never expose entity classes directly in API responses.
- **Cascade Delete Complexity:** When DELETE is called, service must coordinate deletion of related products, product_orders, and orders. This may require multiple repository calls in correct order.
- **Response Consistency:** Every endpoint uses ResponseBuilder — examine existing endpoints or tests to understand expected response structure.
- **HTTP Status Codes Matter:** Mobile apps depend on correct status codes (201 for created, 404 for not found, 400 for validation, etc.)
- **Idempotence:** GET and HEAD requests should be safe (no side effects). PUT should be idempotent (multiple calls produce same result).
- **Test Coverage:** Write tests for all happy paths and error cases:
  - Valid requests (all success)
  - Invalid data (validation)
  - Non-existent resources (404)
  - Invalid ID format (400)
  - Database errors (500)
- **Integration with SQL Feature:** Each endpoint uses corresponding SQL queries documented in restaurants-sql.feature.md. Ensure service calls correct repository methods with parameterized SQL.
