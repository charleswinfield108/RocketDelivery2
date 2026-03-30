package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for order retrieval and deletion operations.
 * Handles GET requests with type-based filtering and DELETE requests for order removal.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class OrdersApiController {
    
    private OrderService orderService;
    
    /**
     * Retrieves orders filtered by type and ID.
     * @param type filter type: "restaurant", "customer", or "courier"
     * @param id entity ID to filter by
     * @return ResponseEntity with list of orders
     */
    @GetMapping("/orders")
    @PreAuthorize("permitAll")
    public ResponseEntity<ApiResponseDTO> getOrders(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "id", required = false) String idParam) {
        
        // Validate type parameter
        if (type == null || type.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("Type parameter is required", "BAD_REQUEST"));
        }
        
        // Validate id parameter
        if (idParam == null || idParam.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("ID parameter is required", "BAD_REQUEST"));
        }
        
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("ID must be a valid integer", "BAD_REQUEST"));
        }
        
        if (id <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("ID must be greater than 0", "BAD_REQUEST"));
        }
        
        // Normalize type to lowercase for comparison
        String normalizedType = type.toLowerCase();
        
        try {
            List<ApiOrderDTO> orders = orderService.getOrdersByType(normalizedType, id);
            return ResponseEntity
                    .ok(ResponseBuilder.success(orders, "Orders retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("Invalid type. Must be 'restaurant', 'customer', or 'courier'", "BAD_REQUEST"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        }
    }
    
    /**
     * Deletes an order by ID.
     * @param id order ID to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/order/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<ApiResponseDTO> deleteOrder(
            @PathVariable(value = "id") String idParam) {
        
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("ID must be a valid integer", "BAD_REQUEST"));
        }
        
        if (id <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseBuilder.error("ID must be greater than 0", "BAD_REQUEST"));
        }
        
        try {
            orderService.deleteOrder(id);
            return ResponseEntity
                    .ok(ResponseBuilder.success("", "Order deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        }
    }
}
