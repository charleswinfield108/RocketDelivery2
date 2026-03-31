# 🤖🛠️ AI Feature Specification - Restaurants API

**Feature ID:** RESTAURANTS-API  
**Priority:** High  
**Status:** In Development  
**Release Version:** Module 12  
**Last Updated:** March 31, 2026

---

## 📋 1. Feature Goal & Scope

### Feature Goal
Implement a comprehensive REST API for managing restaurants in the Rocket Food Delivery application. This API provides complete CRUD (Create, Read, Update, Delete) functionality for restaurant management, enabling restaurant owners to manage their restaurant information and enabling customers to retrieve and filter restaurant data.

### In-Scope
- ✅ **GET /api/restaurants** - List all restaurants with optional filtering by rating and price range
- ✅ **GET /api/restaurants/{id}** - Retrieve a specific restaurant by ID
- ✅ **POST /api/restaurants** - Create a new restaurant (requires authentication)
- ✅ **PUT /api/restaurants/{id}** - Update an existing restaurant (requires authentication)
- ✅ **DELETE /api/restaurants/{id}** - Delete a restaurant (requires authentication)
- ✅ Request validation (required fields, format validation, business rule validation)
- ✅ Error handling (400, 401, 403, 404, 500 status codes)
- ✅ Response standardization (ApiResponseDTO wrapper for all endpoints)
- ✅ Data persistence (SQL database with JPA/Hibernate)
- ✅ Relationship handling (Addresses, Products, Orders, Users)
- ✅ Authorization checks (only restaurant owner can update/delete)
- ✅ Cascade deletion of related entities (Products, Orders)

### Out-of-Scope
- ❌ Restaurant image upload/management
- ❌ Restaurant ratings/reviews calculations
- ❌ Advanced search/full-text search
- ❌ Bulk import/export functionality
- ❌ Analytics and reporting
- ❌ Restaurant analytics dashboard
- ❌ Advanced filtering (location search, distance filtering)
- ❌ Multi-tenant support
- ❌ Modifying Restaurant entity model structure


---

## 🔄 2. Requirements Breakdown & User Flow

### Functional Requirements

#### FR1: List Restaurants
- User can fetch a list of all restaurants
- Results can be filtered by minimum rating (minRating >= value)
- Results can be filtered by maximum price (priceRange <= value)
- Results support filtering combination (minRating AND maxPrice)
- Response includes pagination metadata (total count, page number, size, total pages)
- Response is sorted by restaurant name in ascending order

#### FR2: Get Restaurant Details
- User can fetch detailed information about a specific restaurant
- System validates restaurant existence (returns 404 if not found)
- Response includes all restaurant information and relationships
- Restaurant must be retrievable regardless of active status

#### FR3: Create Restaurant
- Authenticated user can create a new restaurant
- All required fields must be provided (name, description, address, ownerEmail, rating, priceRange)
- System validates field formats and business rules
- New restaurant is created with ACTIVE status
- Response includes the created restaurant with generated ID
- Restaurant must be linked to a valid address

#### FR4: Update Restaurant
- Authenticated user can update an existing restaurant
- User must be the restaurant owner (authorization check)
- Partial updates allowed (not all fields required)
- System validates updated field formats and business rules
- Response includes updated restaurant data
- Cannot update restaurant ID or createdAt timestamp

#### FR5: Delete Restaurant
- Authenticated user can delete a restaurant
- User must be the restaurant owner (authorization check)
- System validates restaurant existence before deletion
- Cascade delete related data (products, product orders, orders)
- Response confirms successful deletion
- Deleted restaurant cannot be retrieved

### User Flows

#### Flow 1: Browse Restaurants (Public)
```
1. Customer accesses homepage
2. System calls GET /api/restaurants (no filters)
3. API returns list of all restaurants with pagination
4. Customer views restaurant list, sorted by name
5. Customer optionally filters by minRating and/or maxPrice
6. System calls GET /api/restaurants?minRating=4&maxPrice=25
7. API returns filtered restaurants matching criteria
8. Customer selects a restaurant from the list
9. Customer's browser shows GET /api/restaurants/{id}
10. API returns detailed restaurant information
11. Customer views restaurant details, menu, and products
```

#### Flow 2: Create Restaurant (Authenticated)
```
1. Restaurant owner logs in (authenticated)
2. Owner navigates to "Create Restaurant" form
3. Owner fills in restaurant details
4. Owner submits form with POST /api/restaurants
5. API validates all required fields are present
6. API validates field formats and business rules
7. API creates restaurant in database with ACTIVE status
8. API returns 201 Created with new restaurant data including generated ID
9. Restaurant is now visible in the restaurant list
```

#### Flow 3: Update Restaurant (Authenticated)
```
1. Restaurant owner logs in and navigates to restaurant settings
2. Owner views current restaurant details
3. Owner modifies some fields (description, priceRange, etc.)
4. Owner submits update with PUT /api/restaurants/{id}
5. API verifies owner authorization (must be the restaurant owner)
6. API validates updated fields
7. API updates restaurant in database
8. API returns 200 OK with updated restaurant data
9. Changes are reflected across the application
```

#### Flow 4: Delete Restaurant (Authenticated)
```
1. Restaurant owner logs in and navigates to restaurant settings
2. Owner clicks "Delete Restaurant" button
3. System shows confirmation dialog
4. Owner confirms deletion
5. Browser sends DELETE /api/restaurants/{id}
6. API verifies owner authorization
7. API performs cascade delete of related data
8. API returns 200 OK with success message
9. Restaurant is removed from system
10. Owner is redirected to dashboard
```

---

## 🖥️ 3. Interfaces Involved (Endpoints & DTOs)

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
