package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthResponseSuccessDTO {
    private String accessToken;
    private boolean success;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("customer_id")
    private int customerId;

    @JsonProperty("courier_id")
    private int courierId;
}
