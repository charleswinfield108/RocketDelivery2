package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.repository.CourierRepository;
import com.rocketFoodDelivery.rocketFood.repository.CustomerRepository;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling order operations including retrieval and deletion.
 * 
 * Provides business logic for:
 * - Filtering orders by entity type (restaurant, customer, courier)
 * - Validating entity existence before retrieving orders
 * - Deleting orders with cascade delete of related ProductOrder records
 * 
 * All database operations are transactional to ensure data consistency.
 */
@Service
@AllArgsConstructor
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private OrderRepository orderRepository;
    private RestaurantRepository restaurantRepository;
    private CustomerRepository customerRepository;
    private CourierRepository courierRepository;
    
    /**
     * Retrieves orders by entity type and ID using native SQL queries.
     * 
     * Validates that the entity (restaurant, customer, or courier) exists before
     * retrieving its orders. This ensures 404 responses for non-existent entities.
     * 
     * @param type the filter type: "restaurant", "customer", or "courier"
     * @param id the entity ID to filter by
     * @return list of orders matching the filter, empty list if entity has no orders
     * 
     * @throws IllegalArgumentException if type is null/empty or invalid value
     * @throws ResourceNotFoundException if entity ID does not exist in database
     */
    @Transactional(readOnly = true)
    public List<ApiOrderDTO> getOrdersByType(String type, int id) {
        logger.debug("OrderService.getOrdersByType() - type: {}, id: {}", type, id);
        
        if (type == null || type.isEmpty()) {
            logger.warn("OrderService.getOrdersByType() - Invalid: type is null or empty");
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        
        List<Order> orders = retrieveOrdersByType(type, id);
        List<ApiOrderDTO> result = orders.stream()
                .map(this::convertToApiOrderDTO)
                .collect(Collectors.toList());
        
        logger.info("OrderService.getOrdersByType() - Retrieved {} orders for type: {}, id: {}", 
                result.size(), type, id);
        return result;
    }
    
    /**
     * Deletes an order by ID with cascade delete of related ProductOrder entries.
     * 
     * Ensures data integrity by:
     * 1. Verifying order exists (throws 404 if not)
     * 2. Deleting all ProductOrder entries for this order
     * 3. Deleting the order itself
     * 
     * @param orderId the order ID to delete
     * @throws ResourceNotFoundException if order with given ID does not exist
     */
    @Transactional
    public void deleteOrder(int orderId) {
        logger.debug("OrderService.deleteOrder() - id: {}", orderId);
        
        // Verify order exists
        if (!orderRepository.existsById(orderId)) {
            logger.warn("OrderService.deleteOrder() - Order not found: {}", orderId);
            throw new ResourceNotFoundException("Order with ID " + orderId + " not found");
        }
        
        // Delete associated ProductOrder entries first (cascade cleanup)
        orderRepository.deleteProductOrdersByOrderId(orderId);
        logger.debug("OrderService.deleteOrder() - Deleted ProductOrder entries for order: {}", orderId);
        
        // Delete the order itself
        orderRepository.deleteById(orderId);
        logger.info("OrderService.deleteOrder() - Order deleted successfully: {}", orderId);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Retrieves orders from repository based on entity type.
     * Validates entity existence and throws 404 if not found.
     * 
     * @param type the entity type ("restaurant", "customer", "courier")
     * @param id the entity ID
     * @return list of Order entities
     * @throws IllegalArgumentException for invalid type
     * @throws ResourceNotFoundException if entity doesn't exist
     */
    private List<Order> retrieveOrdersByType(String type, int id) {
        String normalizedType = type.toLowerCase();
        
        switch (normalizedType) {
            case "restaurant":
                validateEntityExists(restaurantRepository.existsById(id), 
                        "Restaurant", id);
                return orderRepository.findOrdersByRestaurantId(id);
                
            case "customer":
                validateEntityExists(customerRepository.existsById(id), 
                        "Customer", id);
                return orderRepository.findOrdersByCustomerId(id);
                
            case "courier":
                validateEntityExists(courierRepository.existsById(id), 
                        "Courier", id);
                return orderRepository.findOrdersByCourierId(id);
                
            default:
                logger.warn("OrderService.retrieveOrdersByType() - Invalid type: {}", type);
                throw new IllegalArgumentException(
                        "Invalid type: " + type + ". Must be 'restaurant', 'customer', or 'courier'");
        }
    }
    
    /**
     * Validates that an entity exists, throws exception if not.
     * 
     * @param exists true if entity exists, false otherwise
     * @param entityType name of entity type for error message
     * @param id the entity ID
     * @throws ResourceNotFoundException if entity doesn't exist
     */
    private void validateEntityExists(boolean exists, String entityType, int id) {
        if (!exists) {
            logger.warn("OrderService.validateEntityExists() - {} not found: {}", entityType, id);
            throw new ResourceNotFoundException(entityType + " with ID " + id + " not found");
        }
    }
    
    /**
     * Converts Order JPA entity to ApiOrderDTO for API responses.
     * Maps relevant fields and handles null references safely.
     * 
     * @param order the Order entity
     * @return ApiOrderDTO with mapped values
     */
    private ApiOrderDTO convertToApiOrderDTO(Order order) {
        ApiOrderDTO dto = new ApiOrderDTO();
        dto.setId(order.getId());
        dto.setCustomer_id(order.getCustomer() != null ? order.getCustomer().getId() : 0);
        dto.setRestaurant_id(order.getRestaurant() != null ? order.getRestaurant().getId() : 0);
        if (order.getOrder_status() != null) {
            dto.setStatus(order.getOrder_status().getName());
        } else {
            dto.setStatus("");
        }
        return dto;
    }
}
