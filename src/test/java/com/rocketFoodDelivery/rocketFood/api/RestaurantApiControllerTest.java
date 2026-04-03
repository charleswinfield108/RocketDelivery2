package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
public class RestaurantApiControllerTest {

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private MockMvc mockMvc;

    private ApiAddressDTO testAddress;
    private ApiCreateRestaurantDTO testCreateDTO;
    private ApiRestaurantDTO testDTO;

    @BeforeEach
    public void setUp() {
        testAddress = new ApiAddressDTO(1, "123 Wellington St.", "Montreal", "H1H2H2");
        testCreateDTO = new ApiCreateRestaurantDTO(1, 5, "Villa Wellington", 2, "5144154415", "reservations@villa.com", testAddress);
        testDTO = new ApiRestaurantDTO(1, "Villa Wellington", 2, 4);
    }



    // ============================================================
    // GET /api/restaurants - List All Restaurants Tests (16 tests)
    // ============================================================

    @Test
    public void testGetAllRestaurants_Success() throws Exception {
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        restaurants.add(new ApiRestaurantDTO(1, "Villa Wellington", 2, 4));
        restaurants.add(new ApiRestaurantDTO(2, "Fast Pub", 1, 3));
        
        when(restaurantService.findRestaurantsByRatingAndPriceRange(null, null)).thenReturn(restaurants);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Villa Wellington"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(2));
    }

    @Test
    public void testGetAllRestaurants_EmptyList() throws Exception {
        when(restaurantService.findRestaurantsByRatingAndPriceRange(null, null)).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.hasSize(0)));
    }

    @Test
    public void testGetAllRestaurants_FilterByMinRating() throws Exception {
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        restaurants.add(new ApiRestaurantDTO(1, "Villa Wellington", 2, 5));
        
        when(restaurantService.findRestaurantsByRatingAndPriceRange(4, null)).thenReturn(restaurants);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("rating", "4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].rating").value(5));
    }

    @Test
    public void testGetAllRestaurants_FilterByMaxPrice() throws Exception {
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        restaurants.add(new ApiRestaurantDTO(3, "Budget Eat", 1, 3));
        
        when(restaurantService.findRestaurantsByRatingAndPriceRange(null, 1)).thenReturn(restaurants);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("price_range", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].price_range").value(1));
    }

    @Test
    public void testGetAllRestaurants_FilterByBothRatingAndPrice() throws Exception {
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        restaurants.add(new ApiRestaurantDTO(1, "Villa Wellington", 2, 4));
        
        when(restaurantService.findRestaurantsByRatingAndPriceRange(4, 2)).thenReturn(restaurants);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "4").param("price_range", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].price_range").value(2));
    }

    @Test
    public void testGetAllRestaurants_InvalidRating_TooHigh() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("rating", "6"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllRestaurants_InvalidRating_TooLow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("rating", "0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllRestaurants_InvalidRating_Negative() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("rating", "-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllRestaurants_InvalidPrice_TooHigh() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("price_range", "4"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllRestaurants_InvalidPrice_TooLow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("price_range", "0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllRestaurants_InvalidPrice_Negative() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants").param("price_range", "-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllRestaurants_ResponseFormat() throws Exception {
        List<ApiRestaurantDTO> restaurants = new ArrayList<>();
        restaurants.add(new ApiRestaurantDTO(1, "Test Restaurant", 2, 4));
        
        when(restaurantService.findRestaurantsByRatingAndPriceRange(null, null)).thenReturn(restaurants);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    public void testGetAllRestaurants_MultipleFilters_NoResults() throws Exception {
        when(restaurantService.findRestaurantsByRatingAndPriceRange(5, 1)).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants")
                .param("rating", "5").param("price_range", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.hasSize(0)));
    }

    // ============================================================
    // GET /api/restaurants/{id} - Get Single Restaurant Tests (14 tests)
    // ============================================================

    @Test
    public void testGetRestaurantById_Success() throws Exception {
        ApiRestaurantDTO restaurant = new ApiRestaurantDTO(1, "Villa Wellington", 2, 4);
        
        when(restaurantService.findRestaurantWithAverageRatingById(1)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Villa Wellington"));
    }

    @Test
    public void testGetRestaurantById_NotFound() throws Exception {
        when(restaurantService.findRestaurantWithAverageRatingById(999)).thenThrow(new ResourceNotFoundException("Restaurant not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetRestaurantById_InvalidId_Negative() throws Exception {
        when(restaurantService.findRestaurantWithAverageRatingById(-1)).thenThrow(new ResourceNotFoundException("Restaurant not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetRestaurantById_InvalidId_Zero() throws Exception {
        when(restaurantService.findRestaurantWithAverageRatingById(0)).thenThrow(new ResourceNotFoundException("Restaurant not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/0"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetRestaurantById_InvalidId_NonNumeric() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/abc"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetRestaurantById_ResponseIncludesAllFields() throws Exception {
        ApiRestaurantDTO restaurant = new ApiRestaurantDTO(1, "Test Restaurant", 3, 4);
        
        when(restaurantService.findRestaurantWithAverageRatingById(1)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price_range").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.rating").value(4));
    }

    @Test
    public void testGetRestaurantById_AlternateEndpoint() throws Exception {
        ApiRestaurantDTO restaurant = new ApiRestaurantDTO(1, "Villa Wellington", 2, 4);
        
        when(restaurantService.findRestaurantWithAverageRatingById(1)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurant/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1));
    }

    @Test
    public void testGetRestaurantById_LargeId() throws Exception {
        ApiRestaurantDTO restaurant = new ApiRestaurantDTO(999999, "Large ID Restaurant", 1, 3);
        
        when(restaurantService.findRestaurantWithAverageRatingById(999999)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/999999"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(999999));
    }

    @Test
    public void testGetRestaurantById_VerifyRatingCalculation() throws Exception {
        ApiRestaurantDTO restaurant = new ApiRestaurantDTO(1, "Test", 2, 4);
        
        when(restaurantService.findRestaurantWithAverageRatingById(1)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.rating").value(4));
    }

    // ============================================================
    // POST /api/restaurants - Create Restaurant Tests (16 tests)
    // ============================================================

    @Test
    public void testCreateRestaurant_Success() throws Exception {
        when(restaurantService.createRestaurant(any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Villa Wellington"));
    }

    @Test
    public void testCreateRestaurant_MissingName() throws Exception {
        ApiCreateRestaurantDTO invalidDTO = new ApiCreateRestaurantDTO();
        invalidDTO.setUserId(1);
        invalidDTO.setPriceRange(2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_InvalidPriceRange() throws Exception {
        testCreateDTO.setPriceRange(4);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_PriceRangeZero() throws Exception {
        testCreateDTO.setPriceRange(0);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_PriceRangeNegative() throws Exception {
        testCreateDTO.setPriceRange(-1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_InvalidEmail() throws Exception {
        testCreateDTO.setEmail("invalid-email");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_MissingEmail() throws Exception {
        testCreateDTO.setEmail(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_InvalidPhone() throws Exception {
        testCreateDTO.setPhone("123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_MissingPhone() throws Exception {
        testCreateDTO.setPhone(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_MissingAddress() throws Exception {
        testCreateDTO.setAddress(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_ServiceError() throws Exception {
        when(restaurantService.createRestaurant(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateRestaurant_EmptyName() throws Exception {
        testCreateDTO.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_WhitespaceName() throws Exception {
        testCreateDTO.setName("   ");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_LongName() throws Exception {
        testCreateDTO.setName("A".repeat(256));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testCreateRestaurant_ResponseFormat() throws Exception {
        when(restaurantService.createRestaurant(any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists());
    }

    // ============================================================
    // PUT /api/restaurants/{id} - Update Restaurant Tests (14 tests)
    // ============================================================

    @Test
    public void testUpdateRestaurant_Success() throws Exception {
        when(restaurantService.updateRestaurant(anyInt(), any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateRestaurant_NotFound() throws Exception {
        when(restaurantService.updateRestaurant(anyInt(), any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdateRestaurant_InvalidId_Negative() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateRestaurant_InvalidId_Zero() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateRestaurant_InvalidPriceRange() throws Exception {
        testDTO.setPriceRange(4);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateRestaurant_PartialUpdate_Name() throws Exception {
        when(restaurantService.updateRestaurant(anyInt(), any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Name\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateRestaurant_PartialUpdate_Price() throws Exception {
        when(restaurantService.updateRestaurant(anyInt(), any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price_range\": 3}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateRestaurant_EmptyName() throws Exception {
        testDTO.setName("");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testUpdateRestaurant_NullFields() throws Exception {
        when(restaurantService.updateRestaurant(anyInt(), any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateRestaurant_ResponseFormat() throws Exception {
        when(restaurantService.updateRestaurant(anyInt(), any())).thenReturn(Optional.of(testCreateDTO));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    @Test
    public void testUpdateRestaurant_InvalidId_NonNumeric() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/restaurants/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // ============================================================
    // DELETE /api/restaurants/{id} - Delete Restaurant Tests (10 tests)
    // ============================================================

    @Test
    public void testDeleteRestaurant_Success() throws Exception {
        ApiRestaurantDTO mockRestaurant = new ApiRestaurantDTO(1, "Test Restaurant", 2, 4);
        when(restaurantService.findRestaurantWithAverageRatingById(1))
                .thenReturn(Optional.of(mockRestaurant));
        doNothing().when(restaurantService).deleteRestaurant(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteRestaurant_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Restaurant not found")).when(restaurantService).deleteRestaurant(999);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteRestaurant_InvalidId_Negative() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testDeleteRestaurant_InvalidId_Zero() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testDeleteRestaurant_InvalidId_NonNumeric() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/abc"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testDeleteRestaurant_CascadeDelete() throws Exception {
        ApiRestaurantDTO mockRestaurant = new ApiRestaurantDTO(1, "Test Restaurant", 2, 4);
        when(restaurantService.findRestaurantWithAverageRatingById(1))
                .thenReturn(Optional.of(mockRestaurant));
        doNothing().when(restaurantService).deleteRestaurant(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        
        verify(restaurantService, times(1)).deleteRestaurant(1);
    }

    @Test
    public void testDeleteRestaurant_LargeId() throws Exception {
        ApiRestaurantDTO mockRestaurant = new ApiRestaurantDTO(999999, "Large ID Restaurant", 3, 5);
        when(restaurantService.findRestaurantWithAverageRatingById(999999))
                .thenReturn(Optional.of(mockRestaurant));
        doNothing().when(restaurantService).deleteRestaurant(999999);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/999999"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteRestaurant_ServiceError() throws Exception {
        ApiRestaurantDTO mockRestaurant = new ApiRestaurantDTO(1, "Test Restaurant", 2, 4);
        when(restaurantService.findRestaurantWithAverageRatingById(1))
                .thenReturn(Optional.of(mockRestaurant));
        doThrow(new RuntimeException("Database error")).when(restaurantService).deleteRestaurant(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void testDeleteRestaurant_MultipleDeletes() throws Exception {
        ApiRestaurantDTO mockRestaurant1 = new ApiRestaurantDTO(1, "Restaurant 1", 2, 4);
        ApiRestaurantDTO mockRestaurant2 = new ApiRestaurantDTO(2, "Restaurant 2", 3, 5);
        when(restaurantService.findRestaurantWithAverageRatingById(1))
                .thenReturn(Optional.of(mockRestaurant1));
        when(restaurantService.findRestaurantWithAverageRatingById(2))
                .thenReturn(Optional.of(mockRestaurant2));
        doNothing().when(restaurantService).deleteRestaurant(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/restaurants/2"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(restaurantService, times(2)).deleteRestaurant(anyInt());
    }


}