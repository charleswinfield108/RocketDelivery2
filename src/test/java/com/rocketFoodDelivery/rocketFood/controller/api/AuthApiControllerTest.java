package com.rocketFoodDelivery.rocketFood.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
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

/**
 * Test suite for Module 12 authentication endpoint (POST /api/auth).
 * Module 12 Specification:
 * - Success (200): { "success": true, "accessToken": "..." }
 * - Failure (401): { "success": false, "accessToken": null }
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
public class AuthApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    @SuppressWarnings("null")
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
    
    // ==================== VALID AUTHENTICATION ====================
    
    @Test
    public void testAuthenticateWithValidCredentials_ShouldReturn200() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithValidCredentials_VerifyTokenFormat() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value(matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.]*$")));
    }
    
    @Test
    public void testAuthenticateWithValidCredentials_VerifyResponseData() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$", hasKey("success")))
                .andExpect(jsonPath("$", hasKey("accessToken")));
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
        
        String token1 = objectMapper.readTree(token1Response).get("accessToken").asText();
        assert !token1.isEmpty();
    }
    
    // ==================== INVALID CREDENTIALS ====================
    
    @Test
    public void testAuthenticateWithInvalidPassword_ShouldReturn401() throws Exception {
        AuthRequestDTO invalidAuth = new AuthRequestDTO("admin@example.com", "wrongpassword");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuth)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.accessToken").doesNotExist());
    }
    
    @Test
    public void testAuthenticateWithNonexistentEmail_ShouldReturn401() throws Exception {
        AuthRequestDTO nonExistentAuth = new AuthRequestDTO("nonexistent@example.com", "anypassword");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentAuth)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
    
    @Test
    public void testAuthenticateWithEmptyEmail_ShouldReturn400() throws Exception {
        AuthRequestDTO emptyEmailAuth = new AuthRequestDTO("", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyEmailAuth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithEmptyPassword_ShouldReturn400() throws Exception {
        AuthRequestDTO emptyPasswordAuth = new AuthRequestDTO("admin@example.com", "");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyPasswordAuth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithNullEmail_ShouldReturn400() throws Exception {
        AuthRequestDTO nullEmailAuth = new AuthRequestDTO(null, "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullEmailAuth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithNullPassword_ShouldReturn400() throws Exception {
        AuthRequestDTO nullPasswordAuth = new AuthRequestDTO("admin@example.com", null);
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullPasswordAuth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
    
    // ==================== EDGE CASES ====================
    
    @Test
    public void testAuthenticateWithEmailWhitespace_ShouldTrim() throws Exception {
        AuthRequestDTO whitespaceEmailAuth = new AuthRequestDTO("  admin@example.com  ", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(whitespaceEmailAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithPasswordCaseSensitive() throws Exception {
        AuthRequestDTO wrongCasePassword = new AuthRequestDTO("admin@example.com", "ADMIN123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongCasePassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
    
    @Test
    public void testAuthenticateWithEmailCaseInsensitive() throws Exception {
        AuthRequestDTO upperCaseEmailAuth = new AuthRequestDTO("ADMIN@EXAMPLE.COM", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upperCaseEmailAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
    
    @Test
    public void testAuthenticateWithPasswordWhitespace() throws Exception {
        AuthRequestDTO passwordWithSpaces = new AuthRequestDTO("admin@example.com", "  admin123  ");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordWithSpaces)))
                .andExpect(status().isUnauthorized());
    }
    
    // ==================== RESPONSE FORMAT VALIDATION ====================
    
    @Test
    public void testAuthenticateResponseHasCorrectStructure() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.accessToken").isString());
    }
    
    @Test
    public void testAuthenticateErrorResponseHasCorrectStructure() throws Exception {
        AuthRequestDTO invalidAuth = new AuthRequestDTO("admin@example.com", "wrongpass");
        
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuth)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.accessToken").doesNotExist());
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        String subject = jwtUtil.getSubject(token);
        
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        long issuedAt = jwtUtil.getIssuedAt(token);
        
        assert issuedAt >= (beforeRequest - 5000) && issuedAt <= (afterRequest + 5000);
    }
    
    @Test
    public void testTokenContainsExpirationClaim_ExpiresIn1Hour() throws Exception {
        AuthRequestDTO validAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String responseBody = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        long expiresAt = jwtUtil.getExpiresAt(token);
        long issuedAt = jwtUtil.getIssuedAt(token);
        long difference = expiresAt - issuedAt;
        
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
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
        
        String userToken = objectMapper.readTree(userResponse).get("accessToken").asText();
        String employeeToken = objectMapper.readTree(employeeResponse).get("accessToken").asText();
        
        assert !userToken.equals(employeeToken);
    }
    
    @Test
    public void testTokenRoleMatchesUserType() throws Exception {
        AuthRequestDTO userAuth = new AuthRequestDTO("admin@example.com", "admin123");
        
        String userResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String userToken = objectMapper.readTree(userResponse).get("accessToken").asText();
        String userRole = jwtUtil.getRole(userToken);
        
        AuthRequestDTO employeeAuth = new AuthRequestDTO("employee@example.com", "employee123");
        
        String employeeResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String employeeToken = objectMapper.readTree(employeeResponse).get("accessToken").asText();
        String employeeRole = jwtUtil.getRole(employeeToken);
        
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        long expiresAt = jwtUtil.getExpiresAt(token);
        
        long expectedExpiration = beforeRequest + (60 * 60 * 1000);
        long variance = Math.abs(expiresAt - expectedExpiration);
        
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
        
        Thread.sleep(1100);
        
        String secondResponse = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String firstToken = objectMapper.readTree(firstResponse).get("accessToken").asText();
        String secondToken = objectMapper.readTree(secondResponse).get("accessToken").asText();
        
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
        
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        
        boolean isValid = jwtUtil.validateAccessToken(token);
        assert isValid;
    }
}
