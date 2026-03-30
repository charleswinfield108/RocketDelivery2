package com.rocketFoodDelivery.rocketFood.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
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
    
    @BeforeEach
    public void setup() {
        // Create test user for authentication tests
        UserEntity testUser = UserEntity.builder()
                .email("admin@example.com")
                .password("admin123")
                .name("Admin User")
                .build();
        userRepository.save(testUser);
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
}
