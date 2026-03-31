package com.rocketFoodDelivery.rocketFood.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductsApiController.
 * 
 * Tests cover:
 * - GET /api/products?restaurant={id} - retrieve products by restaurant
 * - DELETE /api/products?restaurant={id} - delete products by restaurant
 * 
 * Following TDD methodology with both positive and negative test cases.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("all")
public class ProductsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    private Restaurant testRestaurant;
    private Restaurant anotherRestaurant;
    private Product product1;
    private Product product2;
    private Product product3;
    private Address address;
    private UserEntity userEntity;

    @BeforeEach
    public void setup() {
        // Create address
        address = Address.builder()
                .streetAddress("123 Test St")
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(address);

        // Create user
        userEntity = UserEntity.builder()
                .email("restaurant@test.com")
                .password("pass123")
                .name("Restaurant User")
                .build();
        userRepository.save(userEntity);

        // Create test restaurant
        testRestaurant = Restaurant.builder()
                .email("rest1@test.com")
                .phone("555-1111")
                .name("Test Restaurant")
                .address(address)
                .userEntity(userEntity)
                .priceRange(2)
                .build();
        restaurantRepository.save(testRestaurant);

        // Create another restaurant (for negative tests)
        Address address2 = Address.builder()
                .streetAddress("456 Other St")
                .city("Cambridge")
                .postalCode("02138")
                .build();
        addressRepository.save(address2);

        UserEntity userEntity2 = UserEntity.builder()
                .email("restaurant2@test.com")
                .password("pass123")
                .name("Another Restaurant User")
                .build();
        userRepository.save(userEntity2);

        anotherRestaurant = Restaurant.builder()
                .email("rest2@test.com")
                .phone("555-2222")
                .name("Another Restaurant")
                .address(address2)
                .userEntity(userEntity2)
                .priceRange(3)
                .build();
        restaurantRepository.save(anotherRestaurant);

        // Create products for test restaurant
        product1 = Product.builder()
                .name("Margherita Pizza")
                .description("Classic pizza with tomato, mozzarella, and basil")
                .cost(1299) // $12.99 in cents
                .restaurant(testRestaurant)
                .build();
        productRepository.save(product1);

        product2 = Product.builder()
                .name("Caesar Salad")
                .description("Fresh romaine lettuce with Caesar dressing")
                .cost(899) // $8.99 in cents
                .restaurant(testRestaurant)
                .build();
        productRepository.save(product2);

        product3 = Product.builder()
                .name("Tiramisu")
                .description("Italian dessert with mascarpone and espresso")
                .cost(650) // $6.50 in cents
                .restaurant(testRestaurant)
                .build();
        productRepository.save(product3);
    }

    // ==================== GET PRODUCT TESTS ====================

    @Test
    public void testGetProductsByRestaurant_Success_WithProducts() throws Exception {
        // Arrange: Restaurant has 3 products
        int restaurantId = testRestaurant.getId();

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].cost").exists());
    }

    @Test
    public void testGetProductsByRestaurant_Success_EmptyList() throws Exception {
        // Arrange: Another restaurant has no products
        int restaurantId = anotherRestaurant.getId();

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_BadRequest_MissingRestaurantParam() throws Exception {
        // Arrange: No restaurant parameter provided

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_BadRequest_InvalidRestaurantFormat() throws Exception {
        // Arrange: Invalid restaurant parameter (non-numeric)

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_BadRequest_NegativeRestaurantId() throws Exception {
        // Arrange: Negative restaurant ID

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "-5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_BadRequest_ZeroRestaurantId() throws Exception {
        // Arrange: Zero restaurant ID

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_NotFound_RestaurantDoesNotExist() throws Exception {
        // Arrange: Use a restaurant ID that doesn't exist (9999)

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_Success_ResponseFormat() throws Exception {
        // Arrange: Verify exact response format
        int restaurantId = testRestaurant.getId();

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    public void testGetProductsByRestaurant_Success_AllProductFields() throws Exception {
        // Arrange: Verify all product fields are returned
        int restaurantId = testRestaurant.getId();

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].cost").exists());
    }

    @Test
    public void testGetProductsByRestaurant_Success_VerifyProductsReturned() throws Exception {
        // Arrange: Verify specific products are returned
        int restaurantId = testRestaurant.getId();

        // Act & Assert: Verify we can find the expected product names
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].name", 
                        hasItems(containsString("Pizza"), containsString("Salad"))));
    }

    // ==================== DELETE PRODUCT TESTS ====================

    @Test
    public void testDeleteProductsByRestaurant_Success_WithProducts() throws Exception {
        // Arrange: Restaurant has 3 products
        int restaurantId = testRestaurant.getId();
        
        // Verify products exist before delete
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));

        // Act: Delete all products for the restaurant
        mockMvc.perform(delete("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));

        // Assert: Verify products are deleted (re-query returns empty list)
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void testDeleteProductsByRestaurant_Success_EmptyRestaurant() throws Exception {
        // Arrange: Restaurant has no products
        int restaurantId = anotherRestaurant.getId();

        // Act: Delete (should return 200 even if no products)
        mockMvc.perform(delete("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_BadRequest_MissingRestaurantParam() throws Exception {
        // Arrange: No restaurant parameter provided

        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_BadRequest_InvalidRestaurantFormat() throws Exception {
        // Arrange: Invalid restaurant parameter (non-numeric)

        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .param("restaurant", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_BadRequest_NegativeRestaurantId() throws Exception {
        // Arrange: Negative restaurant ID

        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .param("restaurant", "-5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_BadRequest_ZeroRestaurantId() throws Exception {
        // Arrange: Zero restaurant ID

        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .param("restaurant", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_NotFound_RestaurantDoesNotExist() throws Exception {
        // Arrange: Use a restaurant ID that doesn't exist (9999)

        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .param("restaurant", "9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_Success_ResponseFormat() throws Exception {
        // Arrange: Verify exact response format
        int restaurantId = testRestaurant.getId();

        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    public void testDeleteProductsByRestaurant_Success_DeletionConfirmation() throws Exception {
        // Arrange: Get initial product count
        int restaurantId = testRestaurant.getId();
        int initialCount = productRepository.findProductsByRestaurantId(restaurantId).size();

        // Act: Delete products
        mockMvc.perform(delete("/api/products")
                .param("restaurant", String.valueOf(restaurantId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert: Verify count is now 0
        int finalCount = productRepository.findProductsByRestaurantId(restaurantId).size();
        assert finalCount == 0 : "Expected 0 products after deletion, got " + finalCount;
    }

    @Test
    public void testDeleteProductsByRestaurant_Success_MultipleRestaurants() throws Exception {
        // Arrange: Create products for both restaurants
        int restaurantId1 = testRestaurant.getId();
        int restaurantId2 = anotherRestaurant.getId();

        // Add a product to another restaurant
        Product otherProduct = Product.builder()
                .name("Pasta")
                .description("Italian pasta")
                .cost(1199)
                .restaurant(anotherRestaurant)
                .build();
        productRepository.save(otherProduct);

        // Act: Delete products from testRestaurant only
        mockMvc.perform(delete("/api/products")
                .param("restaurant", String.valueOf(restaurantId1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert: Verify testRestaurant has no products
        assert productRepository.findProductsByRestaurantId(restaurantId1).size() == 0;

        // Assert: Verify anotherRestaurant still has 1 product
        assert productRepository.findProductsByRestaurantId(restaurantId2).size() == 1;
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    public void testGetProducts_LargeRestaurantId() throws Exception {
        // Arrange: Use a very large ID that doesn't exist
        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "2147483647")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteProducts_LargeRestaurantId() throws Exception {
        // Arrange: Use a very large ID that doesn't exist
        // Act & Assert
        mockMvc.perform(delete("/api/products")
                .param("restaurant", "2147483647")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProducts_WhitespaceRestaurantParam() throws Exception {
        // Arrange: Whitespace as parameter
        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "   ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetProducts_DecimalRestaurantId() throws Exception {
        // Arrange: Decimal ID instead of integer
        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("restaurant", "5.5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
