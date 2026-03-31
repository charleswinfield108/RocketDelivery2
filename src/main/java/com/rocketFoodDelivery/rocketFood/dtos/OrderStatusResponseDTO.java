package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order status response.
 * 
 * Contains the updated status value after a successful update.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusResponseDTO {
    private String status;
}
