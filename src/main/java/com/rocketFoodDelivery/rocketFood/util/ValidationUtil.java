package com.rocketFoodDelivery.rocketFood.util;

/**
 * Utility class for common validation operations used across API controllers.
 * Provides reusable validation methods to reduce code duplication.
 */
public class ValidationUtil {

    /**
     * Validates if a string parameter is not null or empty.
     * Trims whitespace and checks if the result is non-empty.
     * 
     * @param param the string parameter to validate
     * @return true if parameter is valid (non-null and non-empty after trim), false otherwise
     */
    public static boolean isValidStringParameter(String param) {
        return param != null && !param.trim().isEmpty();
    }

    /**
     * Parses a string to an integer and validates it's positive.
     * 
     * @param param the string parameter to parse (should be numeric)
     * @return the parsed integer if valid (> 0), null if invalid format or ≤ 0
     */
    public static Integer parseAndValidateId(String param) {
        try {
            Integer id = Integer.parseInt(param);
            return (id > 0) ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
