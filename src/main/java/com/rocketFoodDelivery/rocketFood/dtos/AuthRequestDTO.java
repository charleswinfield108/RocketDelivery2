package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Setter
    private String password;

    /**
     * Custom setter for email that trims whitespace.
     * This allows input like "  admin@example.com  " to be normalized to "admin@example.com".
     * Emails should have whitespace trimmed to normalize input.
     */
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }
}
