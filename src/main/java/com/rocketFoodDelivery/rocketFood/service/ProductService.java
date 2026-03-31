package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * Service class for Product operations.
 * 
 * Handles business logic for:
 * - Retrieving products by restaurant ID
 * - Deleting products by restaurant ID (cascade cleanup)
 * 
 * All operations include validation and error handling.
 */
@Slf4j
@Service
public class ProductService {
    @PersistenceContext
    private EntityManager entityManager;
    
    private ProductRepository productRepository;
    private RestaurantRepository restaurantRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, RestaurantRepository restaurantRepository){
        this.productRepository = productRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Retrieves all products for a given restaurant ID.
     * 
     * @param restaurantId the restaurant ID to fetch products for
     * @return list of ApiProductDTO objects for the restaurant (empty if none found)
     * @throws IllegalArgumentException if restaurantId is invalid (≤ 0)
     * @throws ResourceNotFoundException if restaurant doesn't exist
     */
    public List<ApiProductDTO> getProductsByRestaurant(int restaurantId) {
        log.debug("Starting getProductsByRestaurant with restaurantId: {}", restaurantId);
        
        // Validate restaurant ID
        if (restaurantId <= 0) {
            log.warn("getProductsByRestaurant - Invalid restaurantId: {}", restaurantId);
            throw new IllegalArgumentException("Restaurant ID must be a valid integer greater than 0");
        }

        // Verify restaurant exists
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isEmpty()) {
            log.warn("getProductsByRestaurant - Restaurant not found with ID: {}", restaurantId);
            throw new ResourceNotFoundException("Restaurant with ID " + restaurantId + " not found");
        }

        // Retrieve products from repository
        List<Product> products = productRepository.findProductsByRestaurantId(restaurantId);
        log.info("getProductsByRestaurant - Retrieved {} products for restaurant ID: {}", 
                products.size(), restaurantId);

        // Convert to DTO
        return products.stream()
                .map(this::convertProductToDTO)
                .toList();
    }

    /**
     * Deletes all products associated with a restaurant.
     * This is typically called during restaurant deletion to cascade delete products.
     * 
     * @param restaurantId the restaurant ID whose products should be deleted
     * @return count of deleted products
     * @throws IllegalArgumentException if restaurantId is invalid (≤ 0)
     * @throws ResourceNotFoundException if restaurant doesn't exist
     */
    public int deleteProductsByRestaurant(int restaurantId) {
        log.debug("Starting deleteProductsByRestaurant with restaurantId: {}", restaurantId);
        
        // Validate restaurant ID
        if (restaurantId <= 0) {
            log.warn("deleteProductsByRestaurant - Invalid restaurantId: {}", restaurantId);
            throw new IllegalArgumentException("Restaurant ID must be a valid integer greater than 0");
        }

        // Verify restaurant exists
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isEmpty()) {
            log.warn("deleteProductsByRestaurant - Restaurant not found with ID: {}", restaurantId);
            throw new ResourceNotFoundException("Restaurant with ID " + restaurantId + " not found");
        }

        // Delete products
        int deletedCount = productRepository.deleteProductsByRestaurantId(restaurantId);
        log.info("deleteProductsByRestaurant - Deleted {} products for restaurant ID: {}", 
                deletedCount, restaurantId);

        return deletedCount;
    }

    /**
     * Converts a Product entity to ApiProductDTO.
     * 
     * @param product the product entity to convert
     * @return ApiProductDTO representation of the product
     */
    private ApiProductDTO convertProductToDTO(Product product) {
        return ApiProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .cost(product.getCost())
                .description(product.getDescription())
                .restaurantId(product.getRestaurant() != null ? product.getRestaurant().getId() : null)
                .build();
    }
}