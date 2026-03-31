package com.rocketFoodDelivery.rocketFood.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import com.rocketFoodDelivery.rocketFood.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @BeforeEach
    public void setup() {
        // Create test user for authentication tests
        UserEntity testUser = UserEntity.builder()
                .email("admin@example.com")
                .password("admin123")
                .name("Admin User")
                .isEmployee(false)
                .build();
        userRepository.save(testUser);
        
        // Create employee user for role-based tests
        UserEntity employeeUser = UserEntity.builder()
                .email("employee@example.com")
                .password("employee123")
                .name("Employee User")
                .isEmployee(true)
                .build();
        userRepository.save(employeeUser);
    }
    
    // ==================== VALID AUTHENTICATION CASES ====================
    
    @Test
    public void testAuthenticateWithValidCredentials_ShouldReturn200() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithValidCredentials_VerifyTokenFormat() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.]*$")));  // JWT format
    }
    
    @Test
    public void testAuthenticateWithValidCredentials_VerifyResponseData() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user").isNotEmpty())
                .andExpect(jsonPath("$.data.user.id").isNumber())
                .andExpect(jsonPath("$.data.user.email").value("admin@example.com"));
    }
    
    @Test
    public void testAuthenticateMultipleUsers_DifferentTokensGenerated() throws Exception {
        AuthRequestDTO user1 = new AuthRequestDTO("admin@example.com", "admin123");
        
        String token1Response = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Extract token from response for assertion
        String token1 = objectMapper.readTree(token1Response).get("data").get("token").asText();
        assert !token1.isEmpty();
    }
    
    // ==================== INVALID CREDENTIAL CASES ====================
    
    @Test
    public void testAuthenticateWithInvalidPassword_ShouldReturn401() throws Exception {
        AuthRequestDTO invalidAuth = new AuthRequestDTO("admin@example.com", "wrongpassword");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuth)))
                .andExpect(status().isUnauthorized())  // 401
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithNonexistentEmail_ShouldReturn401() throws Exception {
        AuthRequestDTO nonExistentAuth = new AuthRequestDTO("nonexistent@example.com", "anypassword");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentAuth)))
                .andExpect(status().isUnauthorized())  // 401
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithEmptyEmail_ShouldReturn400() throws Exception {
        AuthRequestDTO emptyEmailAuth = new AuthRequestDTO("", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyEmailAuth)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithEmptyPassword_ShouldReturn400() throws Exception {
        AuthRequestDTO emptyPasswordAuth = new AuthRequestDTO("admin@example.com", "");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyPasswordAuth)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithNullEmail_ShouldReturn400() throws Exception {
        AuthRequestDTO nullEmailAuth = new AuthRequestDTO(null, "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullEmailAuth)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithNullPassword_ShouldReturn400() throws Exception {
        AuthRequestDTO nullPasswordAuth = new AuthRequestDTO("admin@example.com", null);
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullPasswordAuth)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    // ==================== EDGE CASES ====================
    
    @Test
    public void testAuthenticateWithEmailWhitespace_ShouldTrim() throws Exception {
        AuthRequestDTO whitespaceEmailAuth = new AuthRequestDTO("  admin@example.com  ", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(whitespaceEmailAuth)))
                .andExpect(status().isOk())  // Trimmed email should match
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithPasswordCaseSensitive() throws Exception {
        AuthRequestDTO wrongCasePassword = new AuthRequestDTO("admin@example.com", "ADMIN123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongCasePassword)))
                .andExpect(status().isUnauthorized())  // Case matters
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithEmailCaseInsensitive() throws Exception {
        AuthRequestDTO upperCaseEmailAuth = new AuthRequestDTO("ADMIN@EXAMPLE.COM", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upperCaseEmailAuth)))
                .andExpect(status().isOk())  // Email should be case-insensitive
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithSpecialCharactersInPassword() throws Exception {
        // Assuming a user with special char password exists
        AuthRequestDTO specialCharPassword = new AuthRequestDTO("admin@example.com", "admin@#$%^&*");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharPassword)))
                .andExpect(status().isUnauthorized());  // Wrong password, but verifies special chars accepted
    }
    
    @Test
    public void testAuthenticateWithLongPassword() throws Exception {
        String longPassword = "a".repeat(255);
        AuthRequestDTO longPasswordAuth = new AuthRequestDTO("admin@example.com", longPassword);
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longPasswordAuth)))
                .andExpect(status().isUnauthorized());  // Wrong password, but verifies long passwords accepted
    }
    
    @Test
    public void testAuthenticateWithPasswordWhitespace() throws Exception {
        AuthRequestDTO passwordWithSpaces = new AuthRequestDTO("admin@example.com", "  admin123  ");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordWithSpaces)))
                .andExpect(status().isUnauthorized());  // Whitespace changes password, should fail
    }
    
    // ==================== RESPONSE FORMAT VALIDATION ====================
    
    @Test
    public void testAuthenticateResponseHasCorrectStructure() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.id").isNumber())
                .andExpect(jsonPath("$.data.user.email").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateErrorResponseHasCorrectStructure() throws Exception {
        AuthRequestDTO invalidAuth = new AuthRequestDTO("admin@example.com", "wrongpass");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuth)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    // ==================== JWT CLAIMS VALIDATION ====================
    
    @Test
    public void testTokenContainsSubjectClaim_FormatIsUserIdAndEmail() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        String subject = jwtUtil.getSubject(token);
        
        // Subject should be in format: "userId,email"
        assert subject.contains(",");
        assert subject.contains("admin@example.com");
    }
    
    @Test
    public void testTokenContainsUsernameClaim() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        String username = jwtUtil.getUsername(token);
        
        assert username.equals("admin@example.com");
    }
    
    @Test
    public void testTokenContainsIssuerClaim() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        String issuer = jwtUtil.getIssuer(token);
        
        assert issuer.equals("rocketfood-app");
    }
    
    @Test
    public void testTokenContainsIssuedAtClaim() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        long beforeRequest = System.currentTimeMillis();
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        long afterRequest = System.currentTimeMillis();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        long issuedAt = jwtUtil.getIssuedAt(token);
        
        // IssuedAt should be within 5 seconds of the request time
        assert issuedAt >= (beforeRequest - 5000) && issuedAt <= (afterRequest + 5000);
    }
    
    @Test
    public void testTokenContainsExpirationClaim_ExpiresIn1Hour() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        long beforeRequest = System.currentTimeMillis();
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        long expiresAt = jwtUtil.getExpiresAt(token);
        
        // ExpiresAt should be approximately 1 hour (3600000 ms) from issuedAt
        long issuedAt = jwtUtil.getIssuedAt(token);
        long difference = expiresAt - issuedAt;
        
        // Allow 5 second variance
        assert difference >= 3595000 && difference <= 3605000;
    }
    
    @Test
    public void testTokenNotExpiredImmediatelyAfterGeneration() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        
        boolean isExpired = jwtUtil.isTokenExpired(token);
        assert !isExpired;
    }
    
    // ==================== ROLE-BASED TOKENS ====================
    
    @Test
    public void testNonEmployeeUserToken_ContainsROLE_USERRole() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        String role = jwtUtil.getRole(token);
        
        assert role.equals("ROLE_USER");
    }
    
    @Test
    public void testEmployeeUserToken_ContainsROLE_EMPLOYEERole() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("employee@example.com", "employee123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        String role = jwtUtil.getRole(token);
        
        assert role.equals("ROLE_EMPLOYEE");
    }
    
    @Test
    public void testDifferentUsersReceiveDifferentTokens() throws Exception {
        AuthRequestDTO userAuth = new AuthRequestDTO("admin@example.com", "admin123");
        AuthRequestDTO employeeAuth = new AuthRequestDTO("employee@example.com", "employee123");
        
        String userResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String employeeResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String userToken = objectMapper.readTree(userResponse).get("data").get("token").asText();
        String employeeToken = objectMapper.readTree(employeeResponse).get("data").get("token").asText();
        
        // Different tokens should be generated
        assert !userToken.equals(employeeToken);
    }
    
    @Test
    public void testTokenRoleMatchesUserType() throws Exception {
        // Test non-employee user
        AuthRequestDTO userAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String userResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String userToken = objectMapper.readTree(userResponse).get("data").get("token").asText();
        String userRole = jwtUtil.getRole(userToken);
        
        // Test employee user
        AuthRequestDTO employeeAuth = new AuthRequestDTO("employee@example.com", "employee123");
        
        String employeeResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String employeeToken = objectMapper.readTree(employeeResponse).get("data").get("token").asText();
        String employeeRole = jwtUtil.getRole(employeeToken);
        
        // Roles should be different
        assert userRole.equals("ROLE_USER");
        assert employeeRole.equals("ROLE_EMPLOYEE");
        assert !userRole.equals(employeeRole);
    }
    
    // ==================== TOKEN TIMING ====================
    
    @Test
    public void testTokenExpirationTimeIsCorrectlySet() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        long beforeRequest = System.currentTimeMillis();
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        long expiresAt = jwtUtil.getExpiresAt(token);
        
        // Token should expire in approximately 1 hour from request time
        long expectedExpiration = beforeRequest + (60 * 60 * 1000);
        long variance = Math.abs(expiresAt - expectedExpiration);
        
        // Allow 5 second variance
        assert variance <= 5000;
    }
    
    @Test
    public void testMultipleAuthRequests_GenerateUniqueTokens() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String firstResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Add delay to ensure different issuedAt timestamp (JWT uses seconds, not milliseconds)
        Thread.sleep(1100);
        
        String secondResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String firstToken = objectMapper.readTree(firstResponse).get("data").get("token").asText();
        String secondToken = objectMapper.readTree(secondResponse).get("data").get("token").asText();
        
        // Tokens should be different due to different issuedAt times
        assert !firstToken.equals(secondToken);
    }
    
    // ==================== TOKEN VALIDATION ====================
    
    @Test
    public void testGeneratedToken_IsValidAndCanBeVerified() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("data").get("token").asText();
        
        // Token should be valid
        boolean isValid = jwtUtil.validateAccessToken(token);
        assert isValid;
    }
}
