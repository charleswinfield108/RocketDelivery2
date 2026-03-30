# 🤖 AI_FEATURE_Authentication API

## 🎯 Feature Identity

- **Feature Name:** Authentication API - JWT Token Generation
- **Related Area:** Backend / API / Security

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

### LOGIN Request Input (AuthRequestDTO)
- `username` (String) — Username to authenticate
- `password` (String) — Password in plaintext (transmitted over HTTPS only)
- **Validations:**
  - Username is required (not null, not empty)
  - Password is required (not null, not empty)
  - Username length: typically between 3-50 characters
  - Password length: typically minimum 6 characters

### LOGIN Response Output (AuthResponseSuccessDTO)
- `token` (String) — JWT Bearer token
  - Contains base64-encoded header, payload, and signature
  - Header includes algorithm (e.g., HS256 or RS256)
  - Payload includes claims: sub (subject/user ID), username, role, iat (issued-at), exp (expiration)
  - Signature verifies token integrity
- `type` (String) — Token type identifier (typically "Bearer")
- `id` (Long) — User ID of authenticated user
- `username` (String) — Username of authenticated user
- `role` (String) — Authorization role (ADMIN, CUSTOMER, COURIER, EMPLOYEE, RESTAURANT_OWNER)
- `expiresIn` (Long) — Seconds until token expires (optional, e.g., 86400 for 24 hours)

### JWT Token Claims
Standard claims in token:
- `sub` (subject) — User ID
- `username` — Username
- `role` — User role
- `iat` (issued at) — Unix timestamp when token was created
- `exp` (expiration) — Unix timestamp when token expires
- `iss` (issuer) — Application name or identifier (optional)

### Data NOT Modified
- Login is read-only; no user data is modified or created
- No fields are updated or deleted from database

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

## ✅ Acceptance Criteria

### Request Validation Tests
- [ ] POST request with missing username field returns 400 status
- [ ] POST request with missing password field returns 400 status
- [ ] POST request with empty username string returns 400 status
- [ ] POST request with empty password string returns 400 status
- [ ] POST request with username/password accepted as valid format

### Authentication Tests
- [ ] POST request with valid credentials returns 200 status
- [ ] POST request with invalid username returns 401 status
- [ ] POST request with invalid password returns 401 status
- [ ] POST request with correct username but wrong password returns 401 status
- [ ] Error message does not reveal if username exists (generic message)

### Token Generation Tests
- [ ] Response includes JWT token in `token` field
- [ ] Token is valid JWT format (header.payload.signature)
- [ ] Token can be decoded and verified
- [ ] Token includes user ID in payload
- [ ] Token includes username in payload
- [ ] Token includes role in payload
- [ ] Token includes expiration time
- [ ] Token signature is valid and verifiable

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
