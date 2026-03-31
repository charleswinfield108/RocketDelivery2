package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RestaurantApiController {
    private RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Creates a new restaurant.
     *
     * @param restaurant The data for the new restaurant.
     * @return ResponseEntity with the created restaurant's data (HTTP 201), or error if validation fails.
     */
    @PostMapping("/api/restaurants") 
    public ResponseEntity<Object> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDTO restaurant, BindingResult result) {
        // Check validation errors
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(errorMessage, "BAD_REQUEST"));
        }

        try {
            Optional<ApiCreateRestaurantDTO> createdRestaurant = restaurantService.createRestaurant(restaurant);
            if (createdRestaurant.isPresent()) {
                return ResponseEntity.status(201)
                        .body(ResponseBuilder.success(createdRestaurant.get(), "Restaurant created successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ResponseBuilder.error("Failed to create restaurant", "BAD_REQUEST"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(e.getMessage(), "BAD_REQUEST"));
        }
    }
    
    /**
     * Deletes a restaurant by ID and cascades deletion to associated products and orders.
     *
     * @param id The ID of the restaurant to delete.
     * @return ResponseEntity with no content (HTTP 204), or error if not found or invalid ID.
     */
    @DeleteMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> deleteRestaurant(@PathVariable String id){
        // Validate ID format
        try {
            int restaurantId = Integer.parseInt(id);
            if (restaurantId <= 0) {
                return ResponseEntity.badRequest()
                        .body(ResponseBuilder.error("Restaurant ID must be greater than 0", "BAD_REQUEST"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant ID must be a valid integer", "BAD_REQUEST"));
        }

        try {
            restaurantService.deleteRestaurant(Integer.parseInt(id));
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseBuilder.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }

    /**
     * Updates an existing restaurant by ID.
     *
     * @param id                    The ID of the restaurant to update.
     * @param restaurantUpdateData  The updated data for the restaurant.
     * @return ResponseEntity with the updated restaurant's data (HTTP 200), or error if not found or validation fails.
     */
    @PutMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable("id") String id, @RequestBody ApiRestaurantDTO restaurantUpdateData) {
        // Validate ID format
        try {
            int restaurantId = Integer.parseInt(id);
            if (restaurantId <= 0) {
                return ResponseEntity.badRequest()
                        .body(ResponseBuilder.error("Restaurant ID must be greater than 0", "BAD_REQUEST"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant ID must be a valid integer", "BAD_REQUEST"));
        }

        // Manual validation for provided fields
        if (restaurantUpdateData.getPriceRange() > 0 && (restaurantUpdateData.getPriceRange() < 1 || restaurantUpdateData.getPriceRange() > 3)) {
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Price range must be between 1 and 3", "BAD_REQUEST"));
        }

        try {
            ApiCreateRestaurantDTO createDTO = new ApiCreateRestaurantDTO();
            createDTO.setName(restaurantUpdateData.getName());
            createDTO.setPriceRange(restaurantUpdateData.getPriceRange());

            Optional<ApiCreateRestaurantDTO> updatedRestaurant = restaurantService.updateRestaurant(Integer.parseInt(id), createDTO);
            if (updatedRestaurant.isPresent()) {
                // Convert back to ApiRestaurantDTO for response
                ApiRestaurantDTO responseDTO = new ApiRestaurantDTO();
                responseDTO.setId(updatedRestaurant.get().getId());
                responseDTO.setName(updatedRestaurant.get().getName());
                responseDTO.setPriceRange(updatedRestaurant.get().getPriceRange());
                responseDTO.setRating(restaurantUpdateData.getRating());

                return ResponseEntity.ok(ResponseBuilder.success(responseDTO, "Restaurant updated successfully"));
            } else {
                return ResponseEntity.status(404)
                        .body(ResponseBuilder.error("Restaurant not found", "NOT_FOUND"));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(ResponseBuilder.error(e.getMessage(), "NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error(e.getMessage(), "BAD_REQUEST"));
        }
    }

    /**
     * Retrieves details for a restaurant, including its average rating, based on the provided restaurant ID.
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return ResponseEntity with HTTP 200 OK if the restaurant is found, HTTP 404 Not Found otherwise.
     */
    @GetMapping("/api/restaurant/{id}")
    public ResponseEntity<Object> getRestaurant(@PathVariable String id) {
        // Validate ID format
        try {
            int restaurantId = Integer.parseInt(id);
            if (restaurantId <= 0) {
                return ResponseEntity.badRequest()
                        .body(ResponseBuilder.error("Restaurant ID must be greater than 0", "BAD_REQUEST"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseBuilder.error("Restaurant ID must be a valid integer", "BAD_REQUEST"));
        }

        Optional<ApiRestaurantDTO> restaurantWithRatingOptional = restaurantService.findRestaurantWithAverageRatingById(Integer.parseInt(id));
        if (!restaurantWithRatingOptional.isPresent()) {
            return ResponseEntity.status(404)
                    .body(ResponseBuilder.error("Restaurant with ID " + id + " not found", "NOT_FOUND"));
        }
        return ResponseEntity.ok(ResponseBuilder.success(restaurantWithRatingOptional.get(), "Success"));
    }

    /**
     * Retrieves details for a restaurant by ID (alias for /api/restaurant/{id}).
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return ResponseEntity with HTTP 200 OK if the restaurant is found, HTTP 404 Not Found otherwise.
     */
    @GetMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDTO> restaurantWithRatingOptional = restaurantService.findRestaurantWithAverageRatingById(id);
        if (!restaurantWithRatingOptional.isPresent()) throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    }

    /**
     * Returns a list of restaurants given a rating and price range
     *
     * @param rating integer from 1 to 5 (optional)
     * @param priceRange integer from 1 to 3 (optional)
     * @return A list of restaurants that match the specified criteria
     * 
     * @see RestaurantService#findRestaurantsByRatingAndPriceRange(Integer, Integer) for details on retrieving restaurant information.
     */
    @GetMapping("/api/restaurants")
    public ResponseEntity<Object> getAllRestaurants(
        @RequestParam(name = "rating", required = false) Integer rating,
        @RequestParam(name = "price_range", required = false) Integer priceRange) {
        return ResponseBuilder.buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    }
}
