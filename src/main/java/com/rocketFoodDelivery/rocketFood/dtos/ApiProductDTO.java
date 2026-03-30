package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product API responses.
 * 
 * Contains product information to be returned to clients.
 * All fields are nullable to handle variations in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiProductDTO {
    private int id;
    private String name;
    private int cost;
    private String description;
    private Integer restaurantId;
}
