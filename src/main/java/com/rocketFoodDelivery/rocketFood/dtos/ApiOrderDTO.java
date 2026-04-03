package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiOrderDTO {
    int id;

    @JsonProperty("customer_id")
    int customerId;

    @JsonProperty("customer_name")
    String customerName;

    @JsonProperty("customer_address")
    String customerAddress;

    @JsonProperty("restaurant_id")
    int restaurantId;

    @JsonProperty("restaurant_name")
    String restaurantName;

    @JsonProperty("restaurant_address")
    String restaurantAddress;

    @JsonProperty("courier_id")
    int courierId;

    @JsonProperty("courier_name")
    String courierName;

    String status;

    List<ApiProductForOrderApiDTO> products;

    @JsonProperty("total_cost")
    long totalCost;
}
