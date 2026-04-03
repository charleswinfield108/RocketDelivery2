package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.dtos.UpdateOrderStatusRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.OrderStatusResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for order retrieval and deletion operations.
 * 
 * Endpoints:
 * - GET /api/orders?type={type}&id={id} - Retrieve orders filtered by entity type and ID
 * - DELETE /api/order/{id} - Delete an order by ID with cascade delete of related ProductOrders
 * 
 * Supported filter types for GET:
 * - "restaurant": Retrieve orders for a specific restaurant
 * - "customer": Retrieve orders placed by a specific customer
 * - "courier": Retrieve orders assigned to a specific courier
 * 
 * All endpoints validate input and return appropriate HTTP status codes:
 * - 200 OK: Success
 * - 400 Bad Request: Invalid input (missing/invalid parameters, invalid type)
 * - 404 Not Found: Entity or resource not found
 */
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class OrdersApiController {
    
    private OrderService orderService;
    
    /**
     * Retrieves orders filtered by entity type and ID.
     * 
     * Validates query parameters and delegates to OrderService for business logic.
     * All parameters are required and must meet specific validation criteria.
     * 
     * @param type Filter type (case-insensitive): "restaurant", "customer", or "courier"
     * @param idParam Entity ID as string (must be valid positive integer)
     * @return ResponseEntity with 200/400/404 status and ApiResponseDTO
     */
    @GetMapping("/orders")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> getOrders(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "id", required = false) String idParam) {
        
        log.debug("GET /api/orders - type: {}, id: {}", type, idParam);
        
        // Validate type parameter
        if (!isValidStringParameter(type)) {
            log.warn("GET /api/orders - Missing or empty type parameter");
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Type parameter is required", "BAD_REQUEST"));
        }
        
        // Validate id parameter
        if (!isValidStringParameter(idParam)) {
            log.warn("GET /api/orders - Missing or empty id parameter");
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("ID parameter is required", "BAD_REQUEST"));
        }
        
        // Parse and validate id
        Integer id = parseAndValidateId(idParam);
        if (id == null) {
            log.warn("GET /api/orders - Invalid id format: {}", idParam);
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("ID must be a valid integer greater than 0", "BAD_REQUEST"));
        }
        
        String normalizedType = type.toLowerCase();
        
        try {
            List<ApiOrderDTO> orders = orderService.getOrdersByType(normalizedType, id);
            log.info("GET /api/orders - Retrieved {} orders for type: {}, id: {}", 
                    orders.size(), normalizedType, id);
            return ResponseBuilder.buildOkResponse(orders);
        } catch (IllegalArgumentException e) {
            log.warn("GET /api/orders - Invalid type: {}", type);
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(
                            "Invalid type. Must be 'restaurant', 'customer', or 'courier'", 
                            "BAD_REQUEST"));
        } catch (ResourceNotFoundException e) {
            log.warn("GET /api/orders - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        }
    }

    /**
     * Creates a new order with the provided request data.
     * 
     * Validates customer, restaurant, products, and total price.
     * Creates ProductOrder junction records for each product.
     * Sets order status to PENDING by default.
     * 
     * @param request the order creation request
     * @return ResponseEntity with 201 Created status and ApiOrderDTO with created order details
     */
    @PostMapping("/orders")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody ApiCreateOrderRequestDTO request, BindingResult result) {
        log.debug("POST /api/orders - customer_id: {}, restaurant_id: {}, products size: {}",
                request.getCustomerId(), request.getRestaurantId(), 
                request.getProducts() != null ? request.getProducts().size() : 0);
        
        // Check validation errors
        if (result.hasErrors()) {
            @SuppressWarnings("null")
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Validation failed";
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(errorMessage, "BAD_REQUEST"));
        }
        
        try {
            ApiOrderDTO createdOrder = orderService.createOrder(request);
            log.info("POST /api/orders - Order created successfully with id: {}", createdOrder.getId());
            return ResponseBuilder.buildCreatedResponse(createdOrder);
        } catch (ResourceNotFoundException e) {
            log.warn("POST /api/orders - Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalArgumentException e) {
            log.warn("POST /api/orders - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(e.getMessage(), "BAD_REQUEST"));
        } catch (IllegalStateException e) {
            log.error("POST /api/orders - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBuilder.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * Deletes an order by ID including all associated ProductOrder entries.
     * 
     * Validates the ID parameter and delegates to OrderService for deletion.
     * Performs cascade delete of related ProductOrder records.
     * 
     * @param idParam Order ID as string (must be valid positive integer)
     * @return ResponseEntity with 200/400/404 status and ApiResponseDTO
     */
    @DeleteMapping("/order/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> deleteOrder(
            @PathVariable(value = "id") String idParam) {
        
        log.debug("DELETE /api/order/{id} - id: {}", idParam);
        
        Integer id = parseAndValidateId(idParam);
        if (id == null) {
            log.warn("DELETE /api/order/{id} - Invalid id format: {}", idParam);
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("ID must be a valid integer greater than 0", "BAD_REQUEST"));
        }
        
        try {
            orderService.deleteOrder(id);
            log.info("DELETE /api/order/{id} - Order deleted successfully: {}", id);
            return ResponseBuilder.buildOkResponse("");
        } catch (ResourceNotFoundException e) {
            log.warn("DELETE /api/order/{id} - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        }
    }

    /**
     * Updates the status of an existing order.
     * 
     * Validates the order ID in path and status in request body.
     * Delegates to OrderService to update the status in database.
     * Returns simplified response with just the status field.
     * 
     * @param idParam Order ID as string (must be valid positive integer)
     * @param request the status update request containing the new status
     * @return ResponseEntity with 200/400/404 status and OrderStatusResponseDTO
     */
    @PostMapping("/order/{id}/status")
    @PreAuthorize("permitAll")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable(value = "id") String idParam,
            @RequestBody UpdateOrderStatusRequestDTO request) {
        
        log.debug("POST /api/order/{id}/status - id: {}, status: {}", idParam, 
                request != null ? request.getStatus() : "null");
        
        // Validate ID parameter
        Integer id = parseAndValidateId(idParam);
        if (id == null) {
            log.warn("POST /api/order/{id}/status - Invalid id format: {}", idParam);
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("ID must be a valid integer greater than 0", "BAD_REQUEST"));
        }
        
        // Validate request body and status
        if (request == null || request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            log.warn("POST /api/order/{id}/status - Missing or empty status");
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Status field is required and cannot be empty", "BAD_REQUEST"));
        }
        
        try {
            orderService.updateOrderStatus(id, request.getStatus());
            log.info("POST /api/order/{id}/status - Order status updated: id={}, status={}", id, request.getStatus());
            OrderStatusResponseDTO response = OrderStatusResponseDTO.builder()
                    .status(request.getStatus())
                    .build();
            return ResponseBuilder.buildOkResponse(response);
        } catch (ResourceNotFoundException e) {
            log.warn("POST /api/order/{id}/status - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalArgumentException e) {
            log.warn("POST /api/order/{id}/status - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(e.getMessage(), "BAD_REQUEST"));
        }
    }
    
    // ==================== VALIDATION HELPER METHODS ====================
    
    /**
     * Validates if a string parameter is not null and not empty.
     * 
     * @param param Parameter to validate
     * @return true if parameter is valid (non-null and non-empty), false otherwise
     */
    private boolean isValidStringParameter(String param) {
        return param != null && !param.isEmpty();
    }
    
    /**
     * Parses string ID to integer and validates it's positive.
     * 
     * @param idParam ID as string
     * @return Parsed id if valid, null if invalid
     */
    private Integer parseAndValidateId(String idParam) {
        try {
            int id = Integer.parseInt(idParam);
            return (id > 0) ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
