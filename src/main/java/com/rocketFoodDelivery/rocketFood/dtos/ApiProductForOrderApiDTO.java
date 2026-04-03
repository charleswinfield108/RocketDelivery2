package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiProductForOrderApiDTO {
    @JsonProperty("product_id")
    int productId;

    @JsonProperty("product_name")
    String productName;

    int quantity;

    @JsonProperty("unit_cost")
    int unitCost;

    @JsonProperty("total_cost")
    int totalCost;
}
