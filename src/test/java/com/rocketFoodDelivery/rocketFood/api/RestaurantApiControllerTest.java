package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
public class RestaurantApiControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateRestaurant_Success() throws Exception {
        ApiAddressDTO inputAddress = new ApiAddressDTO(1, "123 Wellington St.", "Montreal", "H1H2H2");
        ApiCreateRestaurantDTO inputRestaurant = new ApiCreateRestaurantDTO(1, 4, "Villa wellington", 2, "5144154415", "reservations@villawellington.com", inputAddress);

        // Mock service behavior - returns Optional with the created restaurant
        when(restaurantService.createRestaurant(any())).thenReturn(Optional.of(inputRestaurant));

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(inputRestaurant)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(inputRestaurant.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phone").value(inputRestaurant.getPhone()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(inputRestaurant.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price_range").value(inputRestaurant.getPriceRange()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user_id").value(inputRestaurant.getUserId()));
    }

    @Test
    public void testUpdateRestaurant_Success() throws Exception {
        // Mock data
        int restaurantId = 1;
        ApiCreateRestaurantDTO updatedData = new ApiCreateRestaurantDTO();
        updatedData.setId(restaurantId);
        updatedData.setName("Updated Name");
        updatedData.setPriceRange(2);
        updatedData.setPhone("555-1234");

        // Mock service behavior
        when(restaurantService.updateRestaurant(restaurantId, updatedData))
                .thenReturn(Optional.of(updatedData));

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Restaurant updated successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Updated Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price_range").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phone", org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    public void testListRestaurants_AllRestaurants_Success() throws Exception {
        // Mock data - list of all restaurants
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        
        ApiRestaurantDTO restaurant1 = new ApiRestaurantDTO(1, "Villa Wellington", 3, 4);
        ApiRestaurantDTO restaurant2 = new ApiRestaurantDTO(2, "Fast Pub", 2, 3);
        
        restaurants.add(restaurant1);
        restaurants.add(restaurant2);

        // Mock service behavior
        when(restaurantService.findRestaurantsByRatingAndPriceRange(null, null)).thenReturn(restaurants);

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Villa Wellington"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].price_range").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value("Fast Pub"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].price_range").value(2));
    }

    @Test
    public void testListRestaurants_FilterByRating_Success() throws Exception {
        // Mock data - restaurants with rating 5
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        ApiRestaurantDTO restaurant1 = new ApiRestaurantDTO(1, "Villa Wellington", 3, 5);
        restaurants.add(restaurant1);

        // Mock service behavior
        when(restaurantService.findRestaurantsByRatingAndPriceRange(5, null)).thenReturn(restaurants);

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Villa Wellington"));
    }

    @Test
    public void testListRestaurants_FilterByPriceRange_Success() throws Exception {
        // Mock data - restaurants with price_range 1
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        ApiRestaurantDTO restaurant1 = new ApiRestaurantDTO(3, "Budget Eat", 1, 3);
        restaurants.add(restaurant1);

        // Mock service behavior
        when(restaurantService.findRestaurantsByRatingAndPriceRange(null, 1)).thenReturn(restaurants);

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("price_range", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].price_range").value(1));
    }

    @Test
    public void testListRestaurants_FilterByBothRatingAndPriceRange_Success() throws Exception {
        // Mock data - restaurants matching both filters
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        ApiRestaurantDTO restaurant1 = new ApiRestaurantDTO(1, "Villa Wellington", 3, 4);
        restaurants.add(restaurant1);

        // Mock service behavior
        when(restaurantService.findRestaurantsByRatingAndPriceRange(4, 3)).thenReturn(restaurants);

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "4")
                .param("price_range", "3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].rating").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].price_range").value(3));
    }

    @Test
    public void testListRestaurants_NoResults_EmptyArray() throws Exception {
        // Mock data - no restaurants match filters
        List<ApiRestaurantDTO> emptyList = new ArrayList<>();

        // Mock service behavior
        when(restaurantService.findRestaurantsByRatingAndPriceRange(5, 1)).thenReturn(emptyList);

        // Validate response code and content
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "5")
                .param("price_range", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    public void testListRestaurants_InvalidRating_BadRequest() throws Exception {
        // Test invalid rating (> 5)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "6"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testListRestaurants_InvalidPriceRange_BadRequest() throws Exception {
        // Test invalid price_range (> 3)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("price_range", "4"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testListRestaurants_RatingBelowMin_BadRequest() throws Exception {
        // Test invalid rating (< 1)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testListRestaurants_PriceRangeBelowMin_BadRequest() throws Exception {
        // Test invalid price_range (< 1)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("price_range", "0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


}