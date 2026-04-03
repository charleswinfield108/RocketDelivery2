package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for a product item in an order request.
 * Contains product ID and the quantity being ordered.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiProductItemDTO {
    @JsonProperty("id")
    private int id;

    @JsonProperty("quantity")
    private int quantity;
}
