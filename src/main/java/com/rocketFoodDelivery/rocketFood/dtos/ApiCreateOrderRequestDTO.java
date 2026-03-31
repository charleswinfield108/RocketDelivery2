package com.rocketFoodDelivery.rocketFood.dtos;

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
    private int customer_id;
    private int restaurant_id;
    private List<ApiProductItemDTO> products;
    private long total_cost;
}
