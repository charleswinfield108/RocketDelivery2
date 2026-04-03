package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDTO;
import com.rocketFoodDelivery.rocketFood.exception.BadRequestException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.service.AddressService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * AddressApiController
 * 
 * REST API endpoints for Address operations
 * Handles POST requests to create new addresses
 * 
 * ✅ Acceptance Criteria:
 * - Valid request returns 201 Created
 * - Response includes generated address ID
 * - All fields persisted to database
 * - Missing fields return 400 Bad Request
 * - Invalid data returns 400 Bad Request
 * - Service layer pattern used
 * - Parameterized SQL queries (no concatenation)
 */
@RestController
@RequestMapping("/api")
public class AddressApiController {

    private static final Logger logger = LoggerFactory.getLogger(AddressApiController.class);
    
    @Autowired
    private AddressService addressService;

    /**
     * POST /api/address
     * 
     * Creates a new address with the provided details. Validates all required fields
     * before persisting to the database via the service layer.
     * 
     * @param addressDTO Contains street_address, city, postal_code
     * @return ResponseEntity with 201 Created status and created address data including generated ID
     * @throws BadRequestException if required fields are missing or invalid
     */
    @PostMapping("/address")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> createAddress(@Valid @RequestBody ApiAddressDTO addressDTO, BindingResult result) {
        logger.debug("Creating new address: street={}, city={}, postalCode={}",
                addressDTO.getStreetAddress(), addressDTO.getCity(), addressDTO.getPostalCode());
        
        // Check validation errors
        if (result.hasErrors()) {
            @SuppressWarnings("null")
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Validation failed";
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(errorMessage, "BAD_REQUEST"));
        }

        // Create address using service layer (parameterized queries, no SQL concatenation)
        Address createdAddress = addressService.createAddress(
                addressDTO.getStreetAddress().trim(),
                addressDTO.getCity().trim(),
                addressDTO.getPostalCode().trim()
        );

        logger.info("Address created successfully with ID: {}", createdAddress.getId());
        
        // Build response with created address
        return ResponseBuilder.buildCreatedResponse(createdAddress);
    }

    /**
     * Validate AddressDTO for required fields
     * 
     * Ensures all required fields are present and not empty/whitespace-only.
     * Uses null-safe checks and proper error messages.
     * 
     * @param addressDTO Address DTO to validate
     * @throws BadRequestException if any validation check fails
     */
    private void validateAddressDTO(ApiAddressDTO addressDTO) throws BadRequestException {
        // Validate street address
        if (!isValidField(addressDTO.getStreetAddress())) {
            throw new BadRequestException("Street address is required and cannot be empty");
        }

        // Validate city
        if (!isValidField(addressDTO.getCity())) {
            throw new BadRequestException("City is required and cannot be empty");
        }

        // Validate postal code
        if (!isValidField(addressDTO.getPostalCode())) {
            throw new BadRequestException("Postal code is required and cannot be empty");
        }
    }

    /**
     * Helper method to check if a field is valid (non-null and non-empty after trim)
     * 
     * @param field The field value to check
     * @return true if field is valid (not null and not empty after trim), false otherwise
     */
    private boolean isValidField(String field) {
        return field != null && !field.trim().isEmpty();
    }
}

