package com.rocketFoodDelivery.rocketFood.util;

import org.springframework.http.ResponseEntity;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;
import org.springframework.http.HttpStatus;

/**
 * Custom utility class for handling API responses.
 * Manages both success and error responses.
 */
public class ResponseBuilder {

    public static ResponseEntity<Object> buildOkResponse(Object data) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage("Success");
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Object> buildCreatedResponse(Object data) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage("Success");
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Build a success response with "Success" message
     */
    public static ApiResponseDTO success(Object data, String message) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage("Success");  // Always use "Success" for success responses
        response.setData(data);
        return response;
    }

    /**
     * Build an error response
     */
    public static ApiResponseDTO error(String message, String status) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setError(message);  // Set error field for error responses
        response.setMessage(null);
        response.setData(null);
        return response;
    }
}