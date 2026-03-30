# 🤖 AI_FEATURE_Auth API - Module 12

## 🎯 Feature Identity

- **Feature Name:** Authentication API - JWT Token Generation
- **Related Area:** Backend / Security / Module 12

---

## 🎪 Feature Goal

Implement a login endpoint that validates user credentials (email and password) and returns a JWT access token if credentials are valid. This is the entry point for all authenticated API operations.

---

## 🎯 Feature Scope

### ✅ In Scope (Included)

- Email and password validation against database users
- Basic credential checking (no additional validation at this stage)
- JWT access token generation on successful login
- HTTP status codes (200 OK for success, 401 Unauthorized for invalid credentials)
- Request body validation (email and password required)
- Response format with success flag and access token
- User lookup by email
- Password verification (hashed comparison)

### ❌ Out of Scope (Excluded)

- Multi-factor authentication (MFA)
- Email verification before login
- Password reset or recovery
- Account lockout after failed attempts
- Login audit logging
- Rate limiting on login attempts
- Refresh token support
- API key authentication

---

## 🔧 Sub-Requirements (Feature Breakdown)

- **Request Body Parsing:** Email and password from JSON body
- **Credential Validation:** Verify email exists and password matches
- **Token Generation:** Create JWT token with user information
- **Response Formatting:** Return success flag and access token
- **Error Handling:** Invalid credentials return 401 without revealing specifics
- **Security:** Password hashing verification (not plaintext)

---

## 👥 User Flow / Logic (High Level)

### Successful Login Flow
1. User sends POST request to /api/auth with email and password
2. Controller receives request body with email and password
3. Service queries database for user with matching email
4. Service verifies password matches hashed password in database
5. Service generates JWT token containing user information
6. Service returns 200 OK with success: true and accessToken
7. Client stores accessToken for future authenticated requests

### Failed Login Flow
1. User sends POST request to /api/auth with invalid credentials
2. Controller receives request body
3. Service queries database for user with email
4. User not found OR password doesn't match
5. Service returns 401 Unauthorized
6. Response includes success: false
7. No token generated

### Missing Parameters Flow
1. User sends POST request missing email or password
2. Controller validates request body
3. Request validation fails
4. Controller returns 400 Bad Request
5. Error message indicates missing parameters

---

## 🖥️ Interfaces (Pages, Endpoints, Screens)

### 🔌 POST /api/auth - Login Endpoint

#### Request

**Method:** POST
**Path:** /api/auth
**Content-Type:** application/json

**Body:**
```json
{
  "email": "john.doe@codeboxx.com",
  "password": "password"
}
```

**Requirements:**
- email: required, must be valid email format
- password: required, non-empty string

#### Success Response (200 OK)

```json
{
  "success": true,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Fields:**
- success: boolean (true on success)
- accessToken: JWT token string containing user claims

#### Unauthorized Response (401 Unauthorized)

```json
{
  "success": false
}
```

**Scenario:** Invalid email or password

#### Bad Request Response (400 Bad Request)

```json
{
  "error": "Invalid or missing parameters",
  "details": null
}
```

**Scenario:** Missing email, missing password, or invalid format

---

## 📊 Data Used or Modified

### Request Data

```java
{
  "email": String,      // Required: user's email address
  "password": String    // Required: plaintext password for verification
}
```

### Database Lookup

```sql
SELECT * FROM users 
WHERE email = ?1
```

### Password Verification

```java
// Pseudocode
User user = usersRepository.findByEmail(email);
if (user != null && passwordEncoder.matches(password, user.getHashedPassword())) {
    // Generate token
} else {
    // Return 401
}
```

### JWT Token Contents

```json
{
  "sub": "<user_id>",
  "email": "<user_email>",
  "iat": <issued_at_timestamp>,
  "exp": <expiration_timestamp>
}
```

---

## 🔒 Tech Constraints (Feature-Level)

- **JWT Library:** Spring Security with Nimbus JOSE+JWT or similar
- **Password Encoding:** BCrypt or similar hashing algorithm
- **Token Expiration:** Define appropriate token lifetime (e.g., 24 hours)
- **Secret Key:** Use environment variable for JWT secret
- **HTTPS:** Should be used in production
- **Request Body:** JSON format only
- **Response Format:** JSON with success flag and token

---

## ✅ Acceptance Criteria

### Successful Authentication
- [ ] POST /api/auth endpoint exists
- [ ] Request body requires email and password
- [ ] Valid email and password returns 200 OK
- [ ] Response includes success: true
- [ ] Response includes accessToken field
- [ ] Access token is valid JWT format
- [ ] Access token contains user information

### Invalid Credentials
- [ ] Invalid email returns 401 Unauthorized
- [ ] Invalid password returns 401 Unauthorized
- [ ] Response includes success: false
- [ ] No accessToken in response
- [ ] No details about why it failed (security)

### Missing Parameters
- [ ] Missing email returns 400 Bad Request
- [ ] Missing password returns 400 Bad Request
- [ ] Response includes error message
- [ ] Details field present in response

### Token Functionality
- [ ] Generated token can be decoded
- [ ] Token contains user email in claims
- [ ] Token contains user id in claims
- [ ] Token has expiration set
- [ ] Token works for subsequent authenticated requests

---

## 📝 Notes for the AI

- **No Complex Validation:** The requirement states "No further validation will be done at this stage". Only check that email exists and password matches.
- **Security:** Do NOT return detailed error messages (e.g., "Email not found" vs "Password incorrect"). Return generic "success: false" for both cases.
- **Token Format:** Return token as string in accessToken field, not as object
- **Password Hashing:** Never compare plaintext passwords. Use passwordEncoder.matches() with hashed values
- **HTTPS Only:** This endpoint should be HTTPS-only in production
- **Token Storage:** Consider token expiration and refresh strategy in notes
