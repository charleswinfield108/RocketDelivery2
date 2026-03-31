package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.exception.UnauthorizedException;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling authentication operations.
 * Validates user credentials and generates JWT tokens.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    
    private UserRepository userRepository;
    
    /**
     * Authenticates a user with email and password.
     * 
     * @param authRequest containing email and password
     * @return UserEntity if credentials valid
     * @throws UnauthorizedException if credentials invalid or user not found
     */
    @Transactional(readOnly = true)
    public UserEntity authenticate(AuthRequestDTO authRequest) {
        log.debug("Authenticating user with email: {}", authRequest.getEmail());
        
        // Trim email for comparison
        String trimmedEmail = authRequest.getEmail().replaceAll("\\s+", "").toLowerCase();
        
        // Find user by email (case-insensitive)
        UserEntity user = userRepository.findByEmail(trimmedEmail)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", trimmedEmail);
                    throw new UnauthorizedException("Invalid email or password");
                });
        
        // Validate password
        if (!isPasswordValid(authRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", trimmedEmail);
            throw new UnauthorizedException("Invalid email or password");
        }
        
        log.info("User authenticated successfully: {}", trimmedEmail);
        return user;
    }
    
    /**
     * Validates password against stored password.
     * Password is case-sensitive.
     * 
     * @param providedPassword the password provided by user
     * @param storedPassword the password stored in database
     * @return true if passwords match
     */
    private boolean isPasswordValid(String providedPassword, String storedPassword) {
        if (providedPassword == null || storedPassword == null) {
            return false;
        }
        // Direct comparison (in production would use BCrypt)
        return providedPassword.equals(storedPassword);
    }
}

