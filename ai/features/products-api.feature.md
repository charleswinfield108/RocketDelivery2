# 🤖 AI_FEATURE_Products API

## 🎯 Feature Identity

- **Feature Name:** Products API - Product Retrieval by Restaurant
- **Related Area:** Backend / API / Controller

---

## 🎪 Feature Goal

Provide a REST API interface for retrieving products filtered by restaurant, enabling mobile apps and clients to browse available menu items for a specific restaurant. The API must return properly formatted product data including name, description, price, and category, with support for query parameter filtering and consistent error handling.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- GET /api/products endpoint with optional restaurant query parameter for filtering
- GET /api/products?restaurant={id} to retrieve products for a specific restaurant
- GET /api/products/{id} to retrieve a single product by ID (optional enhancement)
- Request validation for restaurant ID parameter
- Response consistency using ResponseBuilder utility
- Proper HTTP status codes (200 for success, 400 for invalid input, 404 for not found, 500 for errors)
- Error handling with appropriate error messages
- ProductApiController implementation
- Integration with ProductService and ProductRepository
- Support for returning empty list when restaurant has no products

### ❌ Out of Scope (Excluded)

- Modifying Product entity model
- Complex filtering by price range, category, or availability
- Search by product name across restaurants
- Pagination or sorting (basic queries only)
- Product image/file uploads
- Product rating or review system
- Inventory management or stock levels
- Bulk product creation
- Authentication/authorization logic (assume pre-authenticated)
- Product recommendations or suggestions

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **GET Products by Restaurant:** Implement endpoint to retrieve all products for a specific restaurant ID
- **Query Parameter Parsing:** Parse `restaurant` ID from query string and validate it's numeric
- **Service Logic:** Implement method in ProductService to handle product retrieval by restaurant
- **Repository Integration:** Use ProductRepository with native SQL SELECT query
- **Request Validation:** Validate restaurant ID is positive integer and exists
- **Response Formatting:** Use ResponseBuilder to construct consistent ApiResponseDTO
- **Error Handling:** Return appropriate errors for invalid restaurant ID, non-existent restaurant, or database errors
- **Controller Implementation:** Keep controller thin, delegate all logic to service layer
- **Status Codes:** Return correct HTTP status codes (200 OK, 400 Bad Request, 404 Not Found, 500 Error)
- **Empty Results Handling:** Return 200 with empty array if restaurant exists but has no products

---

## 👥 User Flow / Logic (High Level)

1. Mobile app displays restaurant detail page for restaurant ID 5
2. App sends GET request to `/api/products?restaurant=5`
3. Controller receives query parameter and validates restaurant ID is numeric
4. Controller validates restaurant ID is positive integer > 0
5. Controller delegates to ProductService.getProductsByRestaurant(5)
6. Service validates restaurant exists (calls restaurant repository or service)
7. Service calls ProductRepository.findAllByRestaurantId(5)
8. Repository executes parameterized SQL SELECT query
9. Query: SELECT * FROM product WHERE restaurant_id = ?1
10. Database returns list of products for that restaurant (may be empty)
11. Service returns list of products to controller
12. Controller constructs response using ResponseBuilder with list of ApiProductDTOs
13. Controller returns HTTP 200 with products array in response body
14. If restaurant not found → return 404 Not Found
15. If restaurant ID invalid → return 400 Bad Request
16. If zero products → return 200 with empty array (not an error)

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### GET /api/products (Filter by Restaurant)
- **Request Parameters:**
  - `restaurant` (Long, required) — Restaurant ID to filter products
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code
  - `message` (String) — Success or error message
  - `data` (List<ApiProductDTO>) — Array of product objects for the restaurant
- **Response Fields per Product:**
  - `id` (Long) — Product ID
  - `name` (String) — Product name
  - `description` (String) — Product description
  - `price` (BigDecimal) — Product price
  - `restaurantId` (Long) — Associated restaurant ID
  - `category` (String) — Product category (optional)
  - `createdAt` (LocalDateTime) — Creation timestamp
  - `updatedAt` (LocalDateTime) — Last update timestamp
- **HTTP Status Codes:**
  - 200 OK — Products retrieved successfully (may be empty array)
  - 400 Bad Request — Missing or invalid restaurant parameter
  - 404 Not Found — Restaurant does not exist
  - 500 Internal Server Error — Database error

#### GET /api/products/{id} (Retrieve One - Optional)
- **Path Parameter:**
  - `id` (Long, required) — Product ID
- **Response:** ApiResponseDTO with:
  - `statusCode` (int) — HTTP 200
  - `message` (String) — Success message
  - `data` (ApiProductDTO) — Single product object with all fields
- **HTTP Status Codes:**
  - 200 OK — Product retrieved
  - 400 Bad Request — Invalid ID format
  - 404 Not Found — Product does not exist
  - 500 Internal Server Error — Database error

---

## 📊 Data Used or Modified

### GET Products Request Input
- `restaurant` (Long) — Restaurant ID to filter products
- **Validations:**
  - `restaurant` must be a positive integer > 0
  - `restaurant` must exist in restaurants table
  - Parameter must be provided (not optional)

### GET Products Response Output (List of ApiProductDTO)
- `id` (Long) — Database-generated product ID
- `name` (String) — Product name
- `description` (String) — Detailed product description
- `price` (BigDecimal) — Price per item (with 2 decimal places)
- `restaurantId` (Long) — Associated restaurant ID
- `category` (String) — Product category (e.g., Appetizer, Main, Dessert, Beverage)
- `available` (Boolean) — Whether product is available for ordering (optional)
- `createdAt` (LocalDateTime) — ISO 8601 timestamp when product was added
- `updatedAt` (LocalDateTime) — ISO 8601 timestamp of last update

### Data Constraints
- `name`: 1-255 characters, required
- `description`: 0-1000 characters, optional
- `price`: Decimal with 2 places, minimum 0.00, required
- `category`: 1-100 characters, optional
- `restaurantId`: Positive integer, must reference valid restaurant
- `id`: Auto-generated by database

### Expected Behavior
- GET with valid restaurant ID → Returns 200 with products array (may be empty)
- GET with restaurant ID that has no products → Returns 200 with empty array []
- GET with non-existent restaurant ID → Returns 404 Not Found
- GET with invalid restaurant parameter → Returns 400 Bad Request
- GET with missing restaurant parameter → Returns 400 Bad Request
- Returned products are sorted by creation date or name (optional)

---

## 🔒 Tech Constraints (Feature-Level)

- **Controller Pattern:** Thin controller that parses query parameters and delegates to service
- **Service Pattern:** Service layer contains business logic and validation
- **Response Format:** ALL responses must use ResponseBuilder utility
- **SQL Integration:** Service calls ProductRepository with parameterized SQL SELECT
- **Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException)
- **Query Parameters:** Parse and validate query parameters before service call
- **DTO Usage:** Use ApiProductDTO for responses (never expose entity directly)
- **Service Calls:** Controllers only call service methods, never directly call repositories
- **Status Codes:** Follow REST conventions (200, 400, 404, 500)
- **Empty Results:** Empty array with 200 status (not an error)
- **Restaurant Validation:** Service should verify restaurant exists before querying products

---

## ✅ Acceptance Criteria

### GET Products by Restaurant Tests
- [ ] GET /api/products?restaurant={id} endpoint exists
- [ ] GET request with valid restaurant ID returns HTTP 200
- [ ] Response includes statusCode, message, data fields
- [ ] Response data is array of products
- [ ] Each product includes: id, name, description, price, restaurantId, category, createdAt, updatedAt
- [ ] All products for the restaurant are returned
- [ ] GET with restaurant that has no products returns 200 with empty array []
- [ ] GET with restaurant ID that exists but has no products returns empty array (not 404)
- [ ] GET with non-existent restaurant ID returns HTTP 404
- [ ] GET with invalid restaurant parameter (non-numeric) returns HTTP 400
- [ ] GET with negative restaurant ID returns HTTP 400
- [ ] GET with restaurant ID = 0 returns HTTP 400
- [ ] GET with missing restaurant parameter returns HTTP 400
- [ ] Product prices are formatted with 2 decimal places
- [ ] Timestamps are in ISO 8601 format
- [ ] Response uses ResponseBuilder format

### Single Product Tests (Optional)
- [ ] GET /api/products/{id} endpoint exists (optional)
- [ ] GET with valid product ID returns HTTP 200
- [ ] Response includes complete product data
- [ ] GET with non-existent product ID returns HTTP 404
- [ ] GET with invalid ID format returns HTTP 400
- [ ] Response uses ResponseBuilder format

### Integration Tests
- [ ] Controller delegates to service (not directly to repository)
- [ ] Service calls ProductRepository with parameterized SQL query
- [ ] Error handling works correctly for all error cases
- [ ] Response format is consistent across endpoints
- [ ] All responses use ResponseBuilder
- [ ] Empty results return array, not null
- [ ] Non-existent restaurant returns 404 (not empty array)
- [ ] Invalid input returns 400 (not other error codes)

### Data Consistency Tests
- [ ] Returned products match database records
- [ ] Prices are accurate and complete
- [ ] All required fields are present
- [ ] Timestamps are consistent
- [ ] Restaurant ID in response matches request parameter

---

## 📝 Notes for the AI

- **Query Parameter Parsing:** Extract and validate `restaurant` parameter in controller or service before database call. Common mistakes:
  - Not handling missing parameter (must return 400)
  - Not validating numeric format (must return 400 for non-numeric)
  - Not validating positive number (must return 400 for negative/zero)
- **Restaurant Existence Check:** Before querying products, verify restaurant exists. This is important to distinguish between:
  - Restaurant has no products → return 200 with []
  - Restaurant doesn't exist → return 404
  - Invalid restaurant ID → return 400
- **Service Layer Pattern:** Service should:
  1. Validate restaurant ID format (numeric, positive)
  2. Verify restaurant exists
  3. Call repository with valid ID
  4. Return list of products
- **Empty Results Handling:** Returning empty array with 200 OK is correct behavior for "restaurant exists but has no products"
- **SQL Integration:** The underlying SQL query is defined in products-sql.feature.md. Service should call the repository method that uses parameterized SELECT query
- **Controller Responsibility:** Controller should:
  - Extract query parameter
  - Parse and validate (or let Spring validation do it)
  - Call service
  - Return response with ResponseBuilder
- **Thin Controller:** Avoid putting validation logic in controller if possible. Let service handle it for reusability
- **Response Consistency:** Ensure all product fields are present in response. Verify price, dates, and IDs are formatted correctly
- **Testing Strategy:** Write tests for:
  1. Valid restaurant with products (returns list)
  2. Valid restaurant with no products (returns empty array)
  3. Non-existent restaurant (returns 404)
  4. Invalid restaurant ID format (returns 400)
  5. Missing restaurant parameter (returns 400)
- **Integration with SQL Feature:** This endpoint uses the native SQL SELECT query documented in products-sql.feature.md. Ensure service calls correct repository method
- **Future Enhancement:** Consider adding optional parameters like:
  - `sort` (by name, price, date)
  - `category` (filter by category)
  - But keep initial scope simple (just restaurant filtering)
