package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for product retrieval and deletion operations.
 * 
 * Endpoints:
 * - GET /api/products?restaurant={id} - Retrieve products for a specific restaurant
 * - DELETE /api/products?restaurant={id} - Delete all products for a specific restaurant
 * 
 * All endpoints validate input and return appropriate HTTP status codes:
 * - 200 OK: Success
 * - 400 Bad Request: Invalid input (missing/invalid parameters)
 * - 404 Not Found: Restaurant not found
 */
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductsApiController {
    
    private ProductService productService;
    
    /**
     * Retrieves all products for a specific restaurant.
     * 
     * Validates query parameters and delegates to ProductService for business logic.
     * Restaurant parameter is required and must be a valid positive integer.
     * 
     * @param restaurantParam restaurant ID as string (must be valid positive integer)
     * @return ResponseEntity with 200/400/404 status and ApiResponseDTO
     */
    @GetMapping("/products")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> getProducts(
            @RequestParam(value = "restaurant", required = false) String restaurantParam) {
        
        log.debug("GET /api/products - restaurant: {}", restaurantParam);
        
        // Validate restaurant parameter exists
        if (!ValidationUtil.isValidStringParameter(restaurantParam)) {
            log.warn("GET /api/products - Missing or empty restaurant parameter");
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant parameter is required", "BAD_REQUEST"));
        }
        
        // Parse and validate restaurant ID
        Integer restaurantId = ValidationUtil.parseAndValidateId(restaurantParam);
        if (restaurantId == null) {
            log.warn("GET /api/products - Invalid restaurant format: {}", restaurantParam);
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant ID must be a valid integer greater than 0", "BAD_REQUEST"));
        }
        
        try {
            List<ApiProductDTO> products = productService.getProductsByRestaurant(restaurantId);
            log.info("GET /api/products - Retrieved {} products for restaurant ID: {}", 
                    products.size(), restaurantId);
            return ResponseBuilder.buildOkResponse(products);
        } catch (ResourceNotFoundException e) {
            log.warn("GET /api/products - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalArgumentException e) {
            log.warn("GET /api/products - Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(e.getMessage(), "BAD_REQUEST"));
        }
    }

    /**
     * Deletes all products for a specific restaurant.
     * This is typically used in cascade delete when a restaurant is being deleted.
     * 
     * Validates query parameters and delegates to ProductService.
     * Restaurant parameter is required and must be a valid positive integer.
     * 
     * @param restaurantParam restaurant ID as string (must be valid positive integer)
     * @return ResponseEntity with 200/400/404 status and ApiResponseDTO
     */
    @DeleteMapping("/products")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> deleteProducts(
            @RequestParam(value = "restaurant", required = false) String restaurantParam) {
        
        log.debug("DELETE /api/products - restaurant: {}", restaurantParam);
        
        // Validate restaurant parameter exists
        if (!ValidationUtil.isValidStringParameter(restaurantParam)) {
            log.warn("DELETE /api/products - Missing or empty restaurant parameter");
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant parameter is required", "BAD_REQUEST"));
        }
        
        // Parse and validate restaurant ID
        Integer restaurantId = ValidationUtil.parseAndValidateId(restaurantParam);
        if (restaurantId == null) {
            log.warn("DELETE /api/products - Invalid restaurant format: {}", restaurantParam);
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant ID must be a valid integer greater than 0", "BAD_REQUEST"));
        }
        
        try {
            int deletedCount = productService.deleteProductsByRestaurant(restaurantId);
            log.info("DELETE /api/products - Deleted {} products for restaurant ID: {}", 
                    deletedCount, restaurantId);
            return ResponseBuilder.buildOkResponse(
                    "Deleted " + deletedCount + " products for restaurant " + restaurantId);
        } catch (ResourceNotFoundException e) {
            log.warn("DELETE /api/products - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalArgumentException e) {
            log.warn("DELETE /api/products - Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(e.getMessage(), "BAD_REQUEST"));
        }
    }
}
