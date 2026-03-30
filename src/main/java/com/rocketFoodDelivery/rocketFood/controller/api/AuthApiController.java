package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.exception.BadRequestException;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.service.AuthService;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for authentication operations.
 * Handles user login and JWT token generation.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthApiController {
    
    private AuthService authService;
    private JwtUtil jwtUtil;
    
    /**
     * Authenticates a user and returns JWT token.
     * Throws BadRequestException for invalid input (caught by GlobalExceptionHandler → 400)
     * Throws UnauthorizedException for invalid credentials (caught by GlobalExceptionHandler → 401)
     * 
     * @param authRequest containing email and password
     * @return ResponseEntity with token and user details on success
     */
    @PostMapping("/auth")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> authenticate(@RequestBody AuthRequestDTO authRequest) {
        log.debug("Authentication request received for email: {}", 
                authRequest.getEmail() != null ? authRequest.getEmail() : "null");
        
        // Validate input - throws BadRequestException if invalid (400)
        validateAuthRequest(authRequest);
        
        // Authenticate user - throws UnauthorizedException if invalid credentials (401)
        UserEntity user = authService.authenticate(authRequest);
        
        // Generate JWT token
        String token = jwtUtil.generateAccessToken(user);
        
        // Build response
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage("Success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        
        // User data
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        data.put("user", userData);
        
        response.setData(data);
        
        log.info("User authenticated successfully: {}", user.getEmail());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validates authentication request fields.
     * 
     * @param authRequest the authentication request
     * @throws BadRequestException if validation fails
     */
    private void validateAuthRequest(AuthRequestDTO authRequest) {
        if (authRequest == null) {
            throw new BadRequestException("Request body is required");
        }
        
        if (!isValidField(authRequest.getEmail())) {
            throw new BadRequestException("Email is required");
        }
        
        if (!isValidField(authRequest.getPassword())) {
            throw new BadRequestException("Password is required");
        }
    }
    
    /**
     * Checks if a field is valid (not null and not empty/whitespace).
     * 
     * @param field the field to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidField(String field) {
        return field != null && !field.trim().isEmpty();
    }
}
