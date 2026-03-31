# 🤖 AI_FEATURE_Authentication API

**Alignment:** Follows [🤖 AI_SPEC — Rocket Food Delivery REST API](../ai-spec.md)

## 🎯 Feature Identity

- **Feature Name:** Authentication API - JWT Token Generation
- **Related Area:** Backend / API / Security
- **Endpoint:** `POST /api/auth` and `POST /api/auth/login`
- **Priority:** Critical (foundation for all secure endpoints)

---

## 🎪 Feature Goal

Enable user authentication through a REST API endpoint that validates credentials (username and password) and returns a JWT (JSON Web Token) for authenticated requests. The authentication system must securely verify user credentials, generate time-limited tokens with role-based claims, and provide proper error handling for invalid credentials.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- POST endpoint for user login: `POST /api/auth/login` or `POST /api/auth`
- Request validation for username and password fields
- User credential verification against database
- JWT token generation with user claims (ID, username, role)
- Token expiration handling (configurable lifetime)
- Role-based authorization claims embedded in token
- Response object containing JWT token and user information
- Proper error handling for invalid credentials or missing fields
- HTTP status codes for success (200) and authentication failures (401, 400)
- Pre-implemented AuthApiController (DO NOT MODIFY)
- Integration with Spring Security and JWT libraries

### ❌ Out of Scope (Excluded)

- Modifying AuthApiController or authentication logic
- User registration or account creation
- Password reset or recovery flows
- Token refresh endpoints (outside scope)
- Logout or token blacklisting
- Multi-factor authentication (MFA)
- OAuth 2.0 or third-party authentication
- LDAP or Active Directory integration
- Session management (stateless JWT only)
- Rate limiting or brute force protection

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Login Endpoint:** POST /api/auth/login that accepts username and password credentials
- **Request Validation:** Ensure username and password are provided and valid format
- **Credential Verification:** Validate username exists and password matches database record
- **User Role Resolution:** Determine user role (ADMIN, CUSTOMER, COURIER, EMPLOYEE, RESTAURANT_OWNER) from UserEntity
- **JWT Token Generation:** Create JWT with user claims including ID, username, role, and expiration time
- **Token Payload:** Include necessary claims for authorization in subsequent requests
- **Response Format:** Use ResponseBuilder to construct consistent API response with token
- **Error Handling:** Handle invalid credentials, missing fields, and user not found scenarios

---

## 👥 User Flow / Logic (High Level)

1. Mobile app launches and displays login screen
2. User enters username and password
3. App sends POST request to `/api/auth/login` with credentials in JSON body
4. AuthApiController receives request with AuthRequestDTO (username, password)
5. Controller validates that username and password are not empty
6. Controller delegates to authentication service (Spring Security or custom AuthService)
7. Service queries database to find user by username
8. Service verifies password matches (using password encoder for hashed password comparison)
9. If credentials invalid → return 401 Unauthorized with error message
10. If credentials valid → service generates JWT token:
    - Token includes user ID claim
    - Token includes username claim
    - Token includes role claim
    - Token includes token type claim (Bearer)
    - Token includes issued-at time
    - Token includes expiration time (typically 24 hours or configurable)
11. Service constructs response with JWT token and user information
12. Controller returns HTTP 200 with AuthResponseSuccessDTO containing:
    - JWT token string
    - User ID
    - Username
    - Role
    - Token expiration timestamp (optional)
13. Mobile app receives token and stores it (typically in secure storage or SharedPreferences)
14. Mobile app includes token in Authorization header for all subsequent API requests
15. Other controllers/services validate token using Spring Security or custom filter

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 Backend / API

#### POST /api/auth/login (Authenticate)
- **Request Body:** AuthRequestDTO
  - `username` (String, required) — Username for login
  - `password` (String, required) — Password for login
- **Response (Success):** ApiResponseDTO with:
  - `statusCode` (int) — HTTP status code (200)
  - `message` (String) — Success message
  - `data` (AuthResponseSuccessDTO) — Authentication response containing:
    - `token` (String) — JWT Bearer token
    - `type` (String) — Token type (e.g., "Bearer")
    - `id` (Long) — Authenticated user ID
    - `username` (String) — Authenticated username
    - `role` (String) — User role
    - `expiresIn` (Long) — Token expiration time in seconds (optional)
- **Response (Failure):** ApiErrorDTO with:
  - `statusCode` (int) — HTTP status code (401/400)
  - `message` (String) — Error message
  - `data` (null or AuthResponseErrorDTO)
- **HTTP Status Codes:**
  - 200 OK — Authentication successful, token returned
  - 400 Bad Request — Missing username or password field
  - 401 Unauthorized — Invalid username or password
  - 500 Internal Server Error — Server or database error

---

## 📊 Data Used or Modified

### REQUEST: POST /api/auth Login Input (AuthRequestDTO)

**Request Body Structure:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Field Definitions:**
| Field | Type | Required | Constraints | Example |
|-------|------|----------|-------------|---------|
| `username` | String | Yes | Non-empty, 3-50 chars, alphanumeric + underscore | `"john_doe"` |
| `password` | String | Yes | Non-empty, minimum 6 chars | `"SecurePass123"` |

**Validation Rules:**
- ✅ Username must not be null or empty
- ✅ Username must be between 3-50 characters
- ✅ Password must not be null or empty
- ✅ Password must be at least 6 characters
- ✅ Both fields are case-sensitive
- ✅ Invalid format returns HTTP 400 Bad Request

### RESPONSE: POST /api/auth Success Response (HTTP 200)

**Response Body Structure:**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "john_doe",
    "role": "CUSTOMER",
    "expiresIn": 86400
  }
}
```

**Field Definitions:**
| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `statusCode` | Integer | HTTP response status code | `200` |
| `message` | String | Success message | `"Login successful"` |
| `data.token` | String | JWT Bearer token (base64-encoded) | `"eyJhbGciOi..."` |
| `data.type` | String | Token type identifier | `"Bearer"` |
| `data.id` | Long | Authenticated user unique ID | `1` |
| `data.username` | String | Username of authenticated user | `"john_doe"` |
| `data.role` | String | User authorization role | `"ADMIN"`, `"CUSTOMER"`, `"COURIER"`, `"EMPLOYEE"`, `"RESTAURANT_OWNER"` |
| `data.expiresIn` | Long | Token expiration in seconds from now | `86400` (24 hours) |

**JWT Token Structure:**
The token is a standard JWT with three parts separated by dots:
```
header.payload.signature
```

**Token Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Token Payload (Claims):**
```json
{
  "sub": "1",
  "username": "john_doe",
  "role": "CUSTOMER",
  "iat": 1704067200,
  "exp": 1704153600,
  "iss": "rocket-food-delivery"
}
```

**Token Claims Reference:**
| Claim | Type | Description | Example |
|-------|------|-------------|---------|
| `sub` (subject) | String | User ID from database | `"1"` |
| `username` | String | Username claim | `"john_doe"` |
| `role` | String | User role for authorization | `"CUSTOMER"` |
| `iat` (issued-at) | Long | Unix timestamp of creation | `1704067200` |
| `exp` (expiration) | Long | Unix timestamp of expiration | `1704153600` |
| `iss` (issuer) | String | Token issuer identifier | `"rocket-food-delivery"` |

### RESPONSE: POST /api/auth Failure Responses

**Case 1: Missing or Empty Username (HTTP 400)**
```json
{
  "statusCode": 400,
  "message": "Username is required",
  "data": null
}
```

**Case 2: Missing or Empty Password (HTTP 400)**
```json
{
  "statusCode": 400,
  "message": "Password is required",
  "data": null
}
```

**Case 3: Invalid Credentials - User Not Found (HTTP 401)**
```json
{
  "statusCode": 401,
  "message": "Invalid username or password",
  "data": null
}
```

**Case 4: Invalid Credentials - Wrong Password (HTTP 401)**
```json
{
  "statusCode": 401,
  "message": "Invalid username or password",
  "data": null
}
```

**Case 5: Server Error (HTTP 500)**
```json
{
  "statusCode": 500,
  "message": "Internal server error",
  "data": null
}
```

### Data Validations & Expected Behavior

| Scenario | Input | Expected Output | Status | Notes |
|----------|-------|-----------------|--------|-------|
| **Valid credentials** | `username: "john_doe"`, `password: "SecurePass123"` | JWT token with user data | 200 ✅ | Token is valid, claims contain user info |
| **Invalid username** | `username: "nonexistent"`, `password: "anypass"` | Generic error message | 401 | Do NOT reveal if user exists |
| **Invalid password** | `username: "john_doe"`, `password: "wrongpass"` | Generic error message | 401 | Do NOT reveal if password is wrong |
| **Missing username** | `password: "SecurePass123"` | Validation error | 400 | Field is required |
| **Empty username** | `username: ""`, `password: "SecurePass123"` | Validation error | 400 | Field cannot be empty |
| **Missing password** | `username: "john_doe"` | Validation error | 400 | Field is required |
| **Empty password** | `username: "john_doe"`, `password: ""` | Validation error | 400 | Field cannot be empty |
| **Username too short** | `username: "ab"`, `password: "password"` | Validation error | 400 | Minimum 3 characters |
| **Password too short** | `username: "john"`, `password: "pass"` | Validation error | 400 | Minimum 6 characters |
| **User with ADMIN role** | Valid admin credentials | Token with role: "ADMIN" | 200 ✅ | Role correctly embedded in token |
| **User with CUSTOMER role** | Valid customer credentials | Token with role: "CUSTOMER" | 200 ✅ | Role correctly embedded in token |

### Data NOT Modified

- No user records are created, updated, or deleted
- Password is NOT included in any response
- No user profile changes occur
- No session data is persisted (stateless JWT)

---

## 🔒 Tech Constraints (Feature-Level)

- **Authorization:** Use Spring Security framework for authentication and JWT validation
- **JWT Library:** Use library like io.jsonwebtoken:jjwt or spring-security-oauth2-jose
- **Password Encoding:** Use BCryptPasswordEncoder or similar for password hashing (never plaintext)
- **Token Expiration:** Configure reasonable token lifetime (suggested: 24 hours for mobile, shorter for web)
- **HTTPS Only:** All authentication requests must use HTTPS in production (passwords transmitted in plaintext in request body)
- **CORS Handling:** Configure Cross-Origin Resource Sharing (CORS) to allow login from mobile apps
- **Error Messages:** Do not reveal whether username exists (use generic "Invalid credentials" message)
- **Token Storage:** Clients should store tokens securely (encrypted storage on mobile)
- **No Password Returns:** Ensure password field is never included in response
- **Response Format:** Use provided ResponseBuilder and exception handlers
- **Do Not Modify:** AuthApiController, Spring Security configuration, or authentication logic are pre-implemented

---

## ✅ Acceptance Criteria (Test-Driven Development)

### 🧪 Test Suite: Request Validation Tests
These tests verify that the endpoint properly validates input fields.

- [x] **TEST_001: Missing Username Field**
  - **Given:** POST /api/auth request with no `username` field
  - **When:** Request is submitted
  - **Then:** Response status is 400 Bad Request
  - **And:** Response contains error message "Username is required"

- [x] **TEST_002: Missing Password Field**
  - **Given:** POST /api/auth request with no `password` field
  - **When:** Request is submitted
  - **Then:** Response status is 400 Bad Request
  - **And:** Response contains error message "Password is required"

- [x] **TEST_003: Empty Username String**
  - **Given:** POST /api/auth request with `username: ""`
  - **When:** Request is submitted
  - **Then:** Response status is 400 Bad Request
  - **And:** Response contains validation error

- [x] **TEST_004: Empty Password String**
  - **Given:** POST /api/auth request with `password: ""`
  - **When:** Request is submitted
  - **Then:** Response status is 400 Bad Request
  - **And:** Response contains validation error

- [x] **TEST_005: Username Too Short**
  - **Given:** POST /api/auth request with `username: "ab"` (< 3 chars)
  - **When:** Request is submitted
  - **Then:** Response status is 400 Bad Request

- [x] **TEST_006: Password Too Short**
  - **Given:** POST /api/auth request with `password: "pass"` (< 6 chars)
  - **When:** Request is submitted
  - **Then:** Response status is 400 Bad Request

- [x] **TEST_007: Valid Format Acceptance**
  - **Given:** POST /api/auth request with valid format (username: "john_doe", password: "password123")
  - **When:** Request is submitted to authenticate
  - **Then:** Request passes validation (no validation errors)

### 🧪 Test Suite: Authentication & Token Generation Tests
These tests verify that authentication works correctly and tokens are properly generated.

- [x] **TEST_008: Valid Credentials Return 200**
  - **Given:** POST /api/auth with valid existing user credentials
  - **When:** Request is submitted
  - **Then:** Response status is 200 OK
  - **And:** Response contains JWT token in `data.token` field

- [x] **TEST_009: Invalid Username Returns 401**
  - **Given:** POST /api/auth with non-existent username
  - **When:** Request is submitted
  - **Then:** Response status is 401 Unauthorized
  - **And:** Response message is generic "Invalid username or password"

- [x] **TEST_010: Invalid Password Returns 401**
  - **Given:** POST /api/auth with valid username but wrong password
  - **When:** Request is submitted
  - **Then:** Response status is 401 Unauthorized
  - **And:** Response message does not reveal if password is incorrect

- [x] **TEST_011: Generic Error Message**
  - **Given:** POST /api/auth with either invalid username or invalid password
  - **When:** Request is submitted
  - **Then:** Response message is identical for both cases (security best practice)
  - **And:** Message does not reveal whether username exists

### 🧪 Test Suite: JWT Token Format & Claims Tests
These tests verify that the generated JWT token is properly formatted and contains correct claims.

- [x] **TEST_012: Token Format is Valid JWT**
  - **Given:** POST /api/auth returns successfully
  - **When:** Token is extracted from response
  - **Then:** Token has three parts separated by dots (format: `header.payload.signature`)
  - **And:** Token can be decoded without errors

- [x] **TEST_013: Token Contains User ID in Claims**
  - **Given:** POST /api/auth with valid credentials for user ID 1
  - **When:** Token payload is decoded
  - **Then:** Token contains claim `sub`: "1" (user ID in subject field)

- [x] **TEST_014: Token Contains Username in Claims**
  - **Given:** POST /api/auth with username "john_doe"
  - **When:** Token payload is decoded
  - **Then:** Token contains claim `username`: "john_doe"

- [x] **TEST_015: Token Contains User Role in Claims**
  - **Given:** POST /api/auth with user having role "CUSTOMER"
  - **When:** Token payload is decoded
  - **Then:** Token contains claim `role`: "CUSTOMER"
  - **And:** Token role matches user's actual role in database

- [x] **TEST_016: Token Contains Expiration Time**
  - **Given:** POST /api/auth returns successfully
  - **When:** Token payload is decoded
  - **Then:** Token contains `exp` (expiration) claim
  - **And:** `exp` is in the future

- [x] **TEST_017: Token Signature is Valid**
  - **Given:** POST /api/auth generates token
  - **When:** Token signature is verified with secret key
  - **Then:** Signature validation succeeds
  - **And:** Token has not been tampered with

- [x] **TEST_018: Token Issued-At Timestamp**
  - **Given:** POST /api/auth returns token at time T
  - **When:** Token payload is decoded
  - **Then:** Token contains `iat` (issued-at) claim
  - **And:** `iat` is approximately equal to current timestamp

### 🧪 Test Suite: Response Format Tests
These tests verify that the response structure matches specification.

- [x] **TEST_019: Success Response Structure**
  - **Given:** POST /api/auth returns 200
  - **When:** Response body is parsed as JSON
  - **Then:** Response contains `statusCode: 200`
  - **And:** Response contains `message` field
  - **And:** Response contains `data` object with token information

- [x] **TEST_020: Response Data Object Contains All Fields**
  - **Given:** POST /api/auth succeeds
  - **When:** Response `data` object is examined
  - **Then:** Contains `token` (String)
  - **And:** Contains `type: "Bearer"` (String)
  - **And:** Contains `id` (Long/Integer)
  - **And:** Contains `username` (String)
  - **And:** Contains `role` (String)
  - **And:** Contains `expiresIn` (Long/Integer)

- [x] **TEST_021: Token Type is Bearer**
  - **Given:** POST /api/auth succeeds
  - **When:** Response is examined
  - **Then:** Response contains `data.type: "Bearer"`

- [x] **TEST_022: User ID Matches Database**
  - **Given:** POST /api/auth with user ID 5
  - **When:** Response is received
  - **Then:** Response contains `data.id: 5`

- [x] **TEST_023: Username Matches Request**
  - **Given:** POST /api/auth with user "jane_smith"
  - **When:** Response is received
  - **Then:** Response contains `data.username: "jane_smith"`

- [x] **TEST_024: Password NOT in Response**
  - **Given:** POST /api/auth succeeds
  - **When:** Response body is examined
  - **Then:** Response does NOT contain any `password` field
  - **And:** Password is never exposed in response data

### 🧪 Test Suite: Role-Based Token Tests
These tests verify that different user roles are correctly encoded in tokens.

- [x] **TEST_025: Admin User Token Contains ADMIN Role**
  - **Given:** POST /api/auth with admin user credentials
  - **When:** Token is generated and payload decoded
  - **Then:** Contains claim `role: "ADMIN"`

- [x] **TEST_026: Customer User Token Contains CUSTOMER Role**
  - **Given:** POST /api/auth with customer user credentials
  - **When:** Token is generated and payload decoded
  - **Then:** Contains claim `role: "CUSTOMER"`

- [x] **TEST_027: Courier User Token Contains COURIER Role**
  - **Given:** POST /api/auth with courier user credentials
  - **When:** Token is generated and payload decoded
  - **Then:** Contains claim `role: "COURIER"`

- [x] **TEST_028: Employee User Token Contains EMPLOYEE Role**
  - **Given:** POST /api/auth with employee user credentials
  - **When:** Token is generated and payload decoded
  - **Then:** Contains claim `role: "EMPLOYEE"`

- [x] **TEST_029: Restaurant Owner Token Contains RESTAURANT_OWNER Role**
  - **Given:** POST /api/auth with restaurant owner credentials
  - **When:** Token is generated and payload decoded
  - **Then:** Contains claim `role: "RESTAURANT_OWNER"`

### 🧪 Test Suite: Token Timing Tests
These tests verify that token expiration and lifetime are correctly calculated.

- [x] **TEST_030: Token Expires In 24 Hours**
  - **Given:** Token generated at timestamp `now`
  - **When:** Token payload is decoded
  - **Then:** `exp - iat` equals approximately 86400 seconds (24 hours)

- [x] **TEST_031: ExpiresIn Field Matches Token Expiration**
  - **Given:** POST /api/auth generates token with expiration
  - **When:** Response is examined
  - **Then:** `data.expiresIn` is approximately 86400 seconds
  - **And:** Equals `exp - iat` from token claims

## 📋 Test Implementation Status

| Category | Total Tests | Implemented | Status |
|----------|------------|-------------|--------|
| Request Validation | 7 | 7 | ✅ Complete |
| Authentication | 4 | 4 | ✅ Complete |
| Token Format & Claims | 7 | 7 | ✅ Complete |
| Response Format | 6 | 6 | ✅ Complete |
| Role-Based | 5 | 5 | ✅ Complete |
| Token Timing | 2 | 2 | ✅ Complete |
| **TOTAL** | **31** | **31** | **✅ Complete** |

### How to Verify Tests Pass
1. Run test suite: `mvn test -Dtest=AuthApiControllerTest`
2. All 31 test cases should pass with green checkmarks
3. Code coverage should include all branches in authentication logic
4. No compiler warnings or errors

### Response Format Tests
- [ ] Response uses ApiResponseDTO format
- [ ] Response includes statusCode = 200 for success
- [ ] Response includes message field with success text
- [ ] Response includes data field with AuthResponseSuccessDTO
- [ ] AuthResponseSuccessDTO includes token, type, id, username, role
- [ ] Response does NOT include password field
- [ ] Token type is "Bearer" (or configured type)
- [ ] User ID matches authenticated user
- [ ] Username matches authenticated user
- [ ] Role matches user's role from database

### Token Validity Tests
- [ ] Generated token can be used in Authorization header for other endpoints
- [ ] Token format is: Authorization: Bearer <token>
- [ ] Token is accepted by Spring Security filter chain
- [ ] Token claims are correctly parsed by authentication filters

### Integration Tests
- [ ] Login endpoint is accessible at /api/auth/login
- [ ] Endpoint accepts POST method
- [ ] Endpoint rejects GET, PUT, DELETE methods
- [ ] Endpoint returns proper CORS headers for mobile apps
- [ ] Response Content-Type is application/json
- [ ] AuthApiController is not modified from provided implementation

---

## 📝 Notes for the AI

- **Pre-Implemented:** AuthApiController and authentication logic are already provided in the template. DO NOT MODIFY this feature. This spec documents the existing functionality for reference.
- **JWT Structure:** Tokens consist of three parts separated by dots:
  - Header: Contains algorithm and token type
  - Payload: Contains claims (user info, role, expiration)
  - Signature: Cryptographic signature to verify authenticity
- **Password Handling:** Passwords are hashed in database using BCryptPasswordEncoder. The login endpoint compares plaintext input against stored hash using encoder.matches().
- **Token Claims:** Standard JWT claims to include:
  - `sub`: User ID (identifies the subject of the token)
  - `username`: Username for logging/auditing
  - `role`: Authorization role for access control
  - `iat`: Issued-at timestamp (when token was created)
  - `exp`: Expiration timestamp (when token becomes invalid)
- **Double-Check:** This is a pre-written endpoint that should not be modified. Use it as-is. Any tests provided should pass without changes to AuthApiController.
- **Security Note:** Ensure tokens are transmitted only over HTTPS. Tokens should be stored securely on client (mobile should use secure storage, not SharedPreferences).
- **Token Validation:** Other endpoints should validate tokens using Spring Security's @PreAuthorize or custom filters. Token validation is handled by framework, not this endpoint.
- **Expiration Handling:** Configure token lifetime in application.properties or configuration class (e.g., `app.jwtExpirationMs=86400000` for 24 hours).
- **CORS Configuration:** If mobile app is on different domain, configure CORS to allow /api/auth/login from mobile origin.
- **Testing:** Pre-written tests likely exist for this endpoint. Ensure they pass without modification to AuthApiController.
