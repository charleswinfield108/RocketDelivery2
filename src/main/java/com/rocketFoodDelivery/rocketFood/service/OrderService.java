package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.repository.CourierRepository;
import com.rocketFoodDelivery.rocketFood.repository.CustomerRepository;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling order operations including retrieval and deletion.
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
     * Retrieves orders by type and ID using native SQL queries.
     * 
     * @param type the filter type: "restaurant", "customer", or "courier"
     * @param id the entity ID to filter by
     * @return list of orders matching the filter
     * @throws IllegalArgumentException if type is invalid
     * @throws ResourceNotFoundException if entity ID not found
     */
    @Transactional(readOnly = true)
    public List<ApiOrderDTO> getOrdersByType(String type, int id) {
        logger.debug("Getting orders by type: {}, id: {}", type, id);
        
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        
        List<Order> orders;
        
        switch (type.toLowerCase()) {
            case "restaurant":
                // Verify restaurant exists
                if (!restaurantRepository.existsById(id)) {
                    throw new ResourceNotFoundException("Restaurant with ID " + id + " not found");
                }
                orders = orderRepository.findOrdersByRestaurantId(id);
                break;
                
            case "customer":
                // Verify customer exists
                if (!customerRepository.existsById(id)) {
                    throw new ResourceNotFoundException("Customer with ID " + id + " not found");
                }
                orders = orderRepository.findOrdersByCustomerId(id);
                break;
                
            case "courier":
                // Verify courier exists
                if (!courierRepository.existsById(id)) {
                    throw new ResourceNotFoundException("Courier with ID " + id + " not found");
                }
                orders = orderRepository.findOrdersByCourierId(id);
                break;
                
            default:
                throw new IllegalArgumentException("Invalid type: " + type + ". Must be 'restaurant', 'customer', or 'courier'");
        }
        
        // Convert Order entities to ApiOrderDTO
        List<ApiOrderDTO> result = orders.stream()
                .map(this::convertToApiOrderDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} orders for type: {}, id: {}", result.size(), type, id);
        return result;
    }
    
    /**
     * Deletes an order by ID with cascade delete for related ProductOrder entries.
     * 
     * @param orderId the order ID to delete
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional
    public void deleteOrder(int orderId) {
        logger.debug("Deleting order with ID: {}", orderId);
        
        // Verify order exists
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order with ID " + orderId + " not found");
        }
        
        // Delete associated ProductOrder entries first (cascade)
        orderRepository.deleteProductOrdersByOrderId(orderId);
        
        // Then delete the order itself
        orderRepository.deleteById(orderId);
        
        logger.info("Order deleted successfully: {}", orderId);
    }
    
    /**
     * Convert Order entity to ApiOrderDTO
     */
    private ApiOrderDTO convertToApiOrderDTO(Order order) {
        ApiOrderDTO dto = new ApiOrderDTO();
        dto.setId(order.getId());
        dto.setCustomer_id(order.getCustomer() != null ? order.getCustomer().getId() : 0);
        dto.setRestaurant_id(order.getRestaurant() != null ? order.getRestaurant().getId() : 0);
        // order_status is the field name with underscore, Lombok generates get/set for it
        if (order.getOrder_status() != null) {
            dto.setStatus(order.getOrder_status().getName());
        } else {
            dto.setStatus("");
        }
        // Note: Product list and other fields would be populated if needed
        return dto;
    }
}
