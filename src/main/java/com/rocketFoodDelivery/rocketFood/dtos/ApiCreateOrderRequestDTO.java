package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO for creating a new order via POST /api/orders endpoint.
 * 
 * Contains all required information for order creation:
 * - Customer and restaurant references
 * - Products array with quantities
 * - Total price validation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiCreateOrderRequestDTO {
    @JsonProperty("customer_id")
    @Positive(message = "Customer ID must be greater than 0")
    private int customerId;

    @JsonProperty("restaurant_id")
    @Positive(message = "Restaurant ID must be greater than 0")
    private int restaurantId;

    @NotEmpty(message = "Products list cannot be empty")
    private List<ApiProductItemDTO> products;

    @JsonProperty("total_cost")
    @Positive(message = "Total cost must be greater than 0")
    private long totalCost;
}
