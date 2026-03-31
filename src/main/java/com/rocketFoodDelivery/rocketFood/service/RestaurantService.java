package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;


@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

    @Autowired
    public RestaurantService(
        RestaurantRepository restaurantRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository,
        ProductOrderRepository productOrderRepository,
        UserRepository userRepository,
        AddressService addressService
        ) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * Retrieves a restaurant with its details, including the average rating, based on the provided restaurant ID.
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return An Optional containing a RestaurantDTO with details such as id, name, price range, and average rating.
     *         If the restaurant with the given id is not found, an empty Optional is returned.
     *
     * @see RestaurantRepository#findRestaurantWithAverageRatingById(int) for the raw query details from the repository.
     */
    public Optional<ApiRestaurantDTO> findRestaurantWithAverageRatingById(int id) {
        List<Object[]> restaurant = restaurantRepository.findRestaurantWithAverageRatingById(id);

        if (!restaurant.isEmpty()) {
            Object[] row = restaurant.get(0);
            int restaurantId = (int) row[0];
            String name = (String) row[1];
            int priceRange = (int) row[2];
            double rating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
            int roundedRating = (int) Math.ceil(rating);
            ApiRestaurantDTO restaurantDTO = new ApiRestaurantDTO(restaurantId, name, priceRange, roundedRating);
            return Optional.of(restaurantDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds restaurants based on the provided rating and price range.
     *
     * @param rating     The rating for filtering the restaurants.
     * @param priceRange The price range for filtering the restaurants.
     * @return A list of ApiRestaurantDTO objects representing the selected restaurants.
     *         Each object contains the restaurant's ID, name, price range, and a rounded-up average rating.
     */
    public List<ApiRestaurantDTO> findRestaurantsByRatingAndPriceRange(Integer rating, Integer priceRange) {
        List<Object[]> restaurants = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);
        List<ApiRestaurantDTO> restaurantdtos = new ArrayList<>();

            for (Object[] row : restaurants) {
                int restaurantId = (int) row[0];
                String name = (String) row[1];
                int range = (int) row[2];
                double avgRating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
                int roundedAvgRating = (int) Math.ceil(avgRating);
                restaurantdtos.add(new ApiRestaurantDTO(restaurantId, name, range, roundedAvgRating));
            }
            return restaurantdtos;
    }


    /**
     * Creates a new restaurant and returns its information.
     *
     * @param restaurant The data for the new restaurant.
     * @return An Optional containing the created restaurant's information as an ApiCreateRestaurantDTO.
     */
    @Transactional
    @SuppressWarnings("all")
    public Optional<ApiCreateRestaurantDTO> createRestaurant(ApiCreateRestaurantDTO restaurant) {
        // Validate user exists
        if (restaurant.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }

        Optional<com.rocketFoodDelivery.rocketFood.models.UserEntity> userOptional = userRepository.findById(restaurant.getUserId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + restaurant.getUserId() + " not found");
        }

        // Handle address - create and save if provided in the DTO
        com.rocketFoodDelivery.rocketFood.models.Address address = null;
        if (restaurant.getAddress() != null) {
            address = com.rocketFoodDelivery.rocketFood.models.Address.builder()
                    .streetAddress(restaurant.getAddress().getStreetAddress())
                    .city(restaurant.getAddress().getCity())
                    .postalCode(restaurant.getAddress().getPostalCode())
                    .build();
            // Save address first to avoid transient instance error
            addressService.saveAddress(address);
        }

        // Create new restaurant
        Restaurant newRestaurant = Restaurant.builder()
                .name(restaurant.getName())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .priceRange(restaurant.getPriceRange())
                .userEntity(userOptional.get())
                .address(address)
                .build();

        Restaurant saved = restaurantRepository.save(newRestaurant);
        
        ApiCreateRestaurantDTO responseDTO = new ApiCreateRestaurantDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setName(saved.getName());
        responseDTO.setPhone(saved.getPhone());
        responseDTO.setEmail(saved.getEmail());
        responseDTO.setPriceRange(saved.getPriceRange());
        responseDTO.setUserId(saved.getUserEntity().getId());
        
        return Optional.of(responseDTO);
    }

    
    /**
     * Finds a restaurant by its ID.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An Optional containing the restaurant with the specified ID.
     */
    public Optional<Restaurant> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Restaurant ID must be greater than 0");
        }
        return restaurantRepository.findById(id);
    }

    
    /**
     * Updates an existing restaurant by ID with the provided data.
     *
     * @param id                  The ID of the restaurant to update.
     * @param updatedRestaurantDTO The updated data for the restaurant.
     * @return An Optional containing the updated restaurant's information.
     */
    @Transactional
    @SuppressWarnings("all")
    public Optional<ApiCreateRestaurantDTO> updateRestaurant(int id, ApiCreateRestaurantDTO updatedRestaurantDTO) {
        if (id <= 0) {
            throw new IllegalArgumentException("Restaurant ID must be greater than 0");
        }

        Optional<Restaurant> existingRestaurant = restaurantRepository.findById(id);
        if (existingRestaurant.isEmpty()) {
            return Optional.empty();
        }

        Restaurant restaurant = existingRestaurant.get();
        
        // Update fields if provided
        if (updatedRestaurantDTO.getName() != null && !updatedRestaurantDTO.getName().isEmpty()) {
            restaurant.setName(updatedRestaurantDTO.getName());
        }
        if (updatedRestaurantDTO.getPhone() != null && !updatedRestaurantDTO.getPhone().isEmpty()) {
            restaurant.setPhone(updatedRestaurantDTO.getPhone());
        }
        if (updatedRestaurantDTO.getEmail() != null && !updatedRestaurantDTO.getEmail().isEmpty()) {
            restaurant.setEmail(updatedRestaurantDTO.getEmail());
        }
        if (updatedRestaurantDTO.getPriceRange() > 0) {
            restaurant.setPriceRange(updatedRestaurantDTO.getPriceRange());
        }

        Restaurant updated = restaurantRepository.save(restaurant);

        ApiCreateRestaurantDTO responseDTO = new ApiCreateRestaurantDTO();
        responseDTO.setId(updated.getId());
        responseDTO.setName(updated.getName());
        responseDTO.setPhone(updated.getPhone());
        responseDTO.setEmail(updated.getEmail());
        responseDTO.setPriceRange(updated.getPriceRange());
        responseDTO.setUserId(updated.getUserEntity().getId());
        
        return Optional.of(responseDTO);
    }

    
    /**
     * Deletes a restaurant along with its associated data, including its products and orders.
     *
     * @param restaurantId The ID of the restaurant to delete.
     * @throws ResourceNotFoundException if the restaurant is not found.
     */
    @Transactional
    public void deleteRestaurant(int restaurantId) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("Restaurant ID must be greater than 0");
        }

        Optional<Restaurant> existingRestaurant = restaurantRepository.findById(restaurantId);
        if (existingRestaurant.isEmpty()) {
            throw new com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException("Restaurant with ID " + restaurantId + " not found");
        }

        // Cascade delete: delete products first, then orders, then restaurant
        // Delete product_order entries for products of this restaurant
        productOrderRepository.deleteProductOrdersByRestaurant(restaurantId);
        
        // Delete products for this restaurant
        productRepository.deleteProductsByRestaurantId(restaurantId);
        
        // Delete orders for this restaurant
        orderRepository.deleteByRestaurantId(restaurantId);
        
        // Finally, delete the restaurant
        restaurantRepository.deleteById(restaurantId);
    }


}