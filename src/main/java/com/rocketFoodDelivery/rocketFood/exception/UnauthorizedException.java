package com.rocketFoodDelivery.rocketFood.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
