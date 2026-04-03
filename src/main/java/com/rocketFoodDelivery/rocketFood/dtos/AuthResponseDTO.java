package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for authentication response following Module 12 specification.
 * 
 * Response format:
 * {
 *   "success": true,
 *   "accessToken": "jwt_token_string"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private boolean success;
    
    @JsonProperty("accessToken")
    private String accessToken;
}
