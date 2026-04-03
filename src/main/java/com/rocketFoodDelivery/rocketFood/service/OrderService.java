package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductForOrderApiDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductItemDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private ProductRepository productRepository;
    private ProductOrderRepository productOrderRepository;
    private OrderStatusRepository orderStatusRepository;
    
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

    /**
     * Creates a new order with the provided request data.
     * 
     * Validates:
     * 1. Customer exists
     * 2. Restaurant exists
     * 3. All products exist and belong to the restaurant
     * 4. Total price matches the sum of (product_cost * quantity) for all products
     * 5. All quantities are positive
     * 
     * Creates ProductOrder junction records for each product.
     * Sets order status to PENDING by default.
     * 
     * @param request the order creation request containing customer, restaurant, products, and total cost
     * @return ApiOrderDTO with created order details
     * @throws ResourceNotFoundException if customer, restaurant, or product not found
     * @throws IllegalArgumentException if validation fails (price mismatch, product from wrong restaurant, etc.)
     */
    @Transactional
    public ApiOrderDTO createOrder(ApiCreateOrderRequestDTO request) {
        logger.debug("OrderService.createOrder() - customer_id: {}, restaurant_id: {}, products size: {}",
                request.getCustomerId(), request.getRestaurantId(), 
                request.getProducts() != null ? request.getProducts().size() : 0);
        
        // Validate request
        validateCreateOrderRequest(request);
        
        // Fetch entities
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    logger.warn("OrderService.createOrder() - Customer not found: {}", request.getCustomerId());
                    return new ResourceNotFoundException("Customer with ID " + request.getCustomerId() + " not found");
                });
        
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> {
                    logger.warn("OrderService.createOrder() - Restaurant not found: {}", request.getRestaurantId());
                    return new ResourceNotFoundException("Restaurant with ID " + request.getRestaurantId() + " not found");
                });
        
        // Get PENDING status (should exist from seeding)
        // Use a stream to get first matching result in case of duplicates
        OrderStatus pendingStatus = orderStatusRepository.findAll().stream()
                .filter(os -> "PENDING".equals(os.getName()))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("OrderService.createOrder() - PENDING status not found in database");
                    return new IllegalStateException("PENDING order status not found");
                });
        
        // Validate and create ProductOrder entries
        long calculatedTotal = 0;
        List<ProductOrder> productOrders = new ArrayList<>();
        
        for (ApiProductItemDTO productItem : request.getProducts()) {
            Product product = productRepository.findById(productItem.getId())
                    .orElseThrow(() -> {
                        logger.warn("OrderService.createOrder() - Product not found: {}", productItem.getId());
                        return new ResourceNotFoundException("Product with ID " + productItem.getId() + " not found");
                    });
            
            // Verify product belongs to the restaurant
            if (product.getRestaurant().getId() != restaurant.getId()) {
                logger.warn("OrderService.createOrder() - Product {} not from restaurant {}", 
                        product.getId(), restaurant.getId());
                throw new IllegalArgumentException(
                        "Product with ID " + product.getId() + " does not belong to restaurant " + restaurant.getId());
            }
            
            int quantity = productItem.getQuantity();
            calculatedTotal += (long) product.getCost() * quantity;
            
            ProductOrder productOrder = ProductOrder.builder()
                    .product(product)
                    .productQuantity(quantity)
                    .productUnitCost(product.getCost())
                    .build();
            productOrders.add(productOrder);
        }
        
        // Validate total price
        if (calculatedTotal != request.getTotalCost()) {
            logger.warn("OrderService.createOrder() - Price mismatch. Expected: {}, Got: {}", 
                    calculatedTotal, request.getTotalCost());
            throw new IllegalArgumentException(
                    "Total price " + request.getTotalCost() + " does not match products total " + calculatedTotal);
        }
        
        // Create and save order
        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .orderStatus(pendingStatus)
                .restaurantRating(1) // Default rating (minimum valid value)
                .build();
        
        order = orderRepository.save(order);
        logger.debug("OrderService.createOrder() - Order saved with id: {}", order.getId());
        
        // Save ProductOrder entries
        for (ProductOrder productOrder : productOrders) {
            productOrder.setOrder(order);
            productOrderRepository.save(productOrder);
        }
        logger.debug("OrderService.createOrder() - {} ProductOrder entries saved", productOrders.size());
        
        // Convert and return
        ApiOrderDTO result = convertToApiOrderDTOWithProducts(order);
        logger.info("OrderService.createOrder() - Order created successfully with id: {}", order.getId());
        return result;
    }
    
    /**
     * Updates the status of an existing order.
     * 
     * Validates that the order exists before updating.
     * Finds or creates an OrderStatus entity with the status name.
     * Updates the order status in the database.
     * 
     * @param orderId the order ID to update
     * @param statusValue the new status value
     * @throws ResourceNotFoundException if order with given ID does not exist
     * @throws IllegalArgumentException if status is null or empty
     */
    @Transactional
    public void updateOrderStatus(int orderId, String statusValue) {
        logger.debug("OrderService.updateOrderStatus() - orderId: {}, status: {}", orderId, statusValue);
        
        // Validate status
        if (statusValue == null || statusValue.trim().isEmpty()) {
            logger.warn("OrderService.updateOrderStatus() - Invalid status: null or empty");
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        // Verify order exists and fetch it
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("OrderService.updateOrderStatus() - Order not found: {}", orderId);
                    return new ResourceNotFoundException("Order with ID " + orderId + " not found");
                });
        
        // Find or create OrderStatus with the given name
        OrderStatus orderStatus = orderStatusRepository.findAll().stream()
                .filter(os -> statusValue.equalsIgnoreCase(os.getName()))
                .findFirst()
                .orElseGet(() -> {
                    // If status doesn't exist, create it
                    logger.debug("OrderService.updateOrderStatus() - Creating new OrderStatus: {}", statusValue);
                    OrderStatus newStatus = OrderStatus.builder().name(statusValue).build();
                    return orderStatusRepository.save(newStatus);
                });
        
        // Update the order status
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        logger.info("OrderService.updateOrderStatus() - Order status updated: id={}, status={}", orderId, statusValue);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Validates create order request for required fields and basic constraints.
     * 
     * @param request the request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateCreateOrderRequest(ApiCreateOrderRequestDTO request) {
        if (request.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid customer ID: must be positive");
        }
        if (request.getRestaurantId() <= 0) {
            throw new IllegalArgumentException("Invalid restaurant ID: must be positive");
        }
        if (request.getProducts() == null) {
            throw new IllegalArgumentException("Products array cannot be null");
        }
        if (request.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Products array cannot be empty");
        }
        if (request.getTotalCost() <= 0) {
            throw new IllegalArgumentException("Total cost must be positive");
        }
        
        // Validate each product item
        for (ApiProductItemDTO item : request.getProducts()) {
            if (item.getId() <= 0) {
                throw new IllegalArgumentException("Invalid product ID: must be positive");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Product quantity must be positive");
            }
        }
    }
    
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
     * Maps relevant fields including customer, restaurant, and courier details.
     * 
     * @param order the Order entity
     * @return ApiOrderDTO with mapped values
     */
    private ApiOrderDTO convertToApiOrderDTO(Order order) {
        ApiOrderDTO dto = new ApiOrderDTO();
        dto.setId(order.getId());
        
        // Customer details
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
            dto.setCustomerName(order.getCustomer().getUserEntity() != null ? order.getCustomer().getUserEntity().getName() : "");
            dto.setCustomerAddress(order.getCustomer().getAddress() != null ? 
                    order.getCustomer().getAddress().getStreetAddress() + ", " + 
                    order.getCustomer().getAddress().getCity() + ", " + 
                    order.getCustomer().getAddress().getPostalCode() : "");
        } else {
            dto.setCustomerId(0);
            dto.setCustomerName("");
            dto.setCustomerAddress("");
        }
        
        // Restaurant details
        if (order.getRestaurant() != null) {
            dto.setRestaurantId(order.getRestaurant().getId());
            dto.setRestaurantName(order.getRestaurant().getName());
            dto.setRestaurantAddress(order.getRestaurant().getAddress() != null ? 
                    order.getRestaurant().getAddress().getStreetAddress() + ", " + 
                    order.getRestaurant().getAddress().getCity() + ", " + 
                    order.getRestaurant().getAddress().getPostalCode() : "");
        } else {
            dto.setRestaurantId(0);
            dto.setRestaurantName("");
            dto.setRestaurantAddress("");
        }
        
        // Courier details
        if (order.getCourier() != null) {
            dto.setCourierId(order.getCourier().getId());
            dto.setCourierName(order.getCourier().getUserEntity() != null ? order.getCourier().getUserEntity().getName() : "");
        } else {
            dto.setCourierId(0);
            dto.setCourierName("");
        }
        
        // Order status
        if (order.getOrderStatus() != null) {
            dto.setStatus(order.getOrderStatus().getName());
        } else {
            dto.setStatus("");
        }
        
        return dto;
    }

    /**
     * Converts Order JPA entity to ApiOrderDTO including products and total cost.
     * Used for POST responses to include complete order details.
     * 
     * @param order the Order entity
     * @return ApiOrderDTO with mapped values and products
     */
    private ApiOrderDTO convertToApiOrderDTOWithProducts(Order order) {
        ApiOrderDTO dto = convertToApiOrderDTO(order);
        
        // Fetch and add products
        List<ProductOrder> productOrders = productOrderRepository.findByOrderId(order.getId());
        List<ApiProductForOrderApiDTO> products = new ArrayList<>();
        long totalCost = 0;
        
        for (ProductOrder po : productOrders) {
            ApiProductForOrderApiDTO productDto = new ApiProductForOrderApiDTO();
            productDto.setProductId(po.getProduct().getId());
            productDto.setProductName(po.getProduct().getName());
            productDto.setUnitCost(po.getProductUnitCost());
            productDto.setQuantity(po.getProductQuantity());
            productDto.setTotalCost((int) ((long) po.getProductUnitCost() * po.getProductQuantity()));
            products.add(productDto);
            totalCost += (long) po.getProductUnitCost() * po.getProductQuantity();
        }
        
        dto.setProducts(products);
        dto.setTotalCost(totalCost);
        return dto;
    }
}
