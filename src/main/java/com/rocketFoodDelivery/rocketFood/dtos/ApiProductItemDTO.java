package com.rocketFoodDelivery.rocketFood.dtos;

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
    private int product_id;
    private int product_quantity;
}
