package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseDTO;
import com.rocketFoodDelivery.rocketFood.exception.BadRequestException;
import com.rocketFoodDelivery.rocketFood.exception.UnauthorizedException;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.service.AuthService;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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
     * Returns Module 12 specification format: { "success": true, "accessToken": "..." }
     * 
     * @param authRequest containing email and password
     * @return ResponseEntity with success status and access token
     */
    @PostMapping("/auth")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> authenticate(@Valid @RequestBody AuthRequestDTO authRequest, BindingResult result) {
        log.debug("Authentication request received for email: {}", 
                authRequest.getEmail() != null ? authRequest.getEmail() : "null");
        
        // Check validation errors
        if (result.hasErrors()) {
            @SuppressWarnings("null")
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Validation failed";
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(errorMessage, "BAD_REQUEST"));
        }
        
        try {
            // Authenticate user - throws UnauthorizedException if invalid credentials (401)
            UserEntity user = authService.authenticate(authRequest);
            
            // Generate JWT token
            String token = jwtUtil.generateAccessToken(user);
            
            // Return Module 12 spec format
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .success(true)
                    .accessToken(token)
                    .build();
            
            log.info("User authenticated successfully: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            // Return Module 12 spec format for 401
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .success(false)
                    .accessToken(null)
                    .build();
            return ResponseEntity.status(401).body(response);
        }
    }
}
