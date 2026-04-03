package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// This object is used to return the a responce to the api calls.// This is  not needed i have created to make the code more organized.
public class ApiResponseDTO {
    private String message;
    private Object data;
    private String error;
}
