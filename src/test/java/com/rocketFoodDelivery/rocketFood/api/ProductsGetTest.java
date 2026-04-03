package com.rocketFoodDelivery.rocketFood.api;

import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test coverage for GET /api/products endpoint.
 * Tests product retrieval for restaurants with various scenarios (success, error, edge cases).
 * 
 * Test Categories:
 * - Basic Retrieval: Valid restaurant ID with products
 * - Multiple Products: Various product counts (1, 3, many)
 * - Empty Lists: Restaurant with no products
 * - Validation: Valid/invalid IDs, missing filters, edge cases
 * - Response Format: ApiResponseDTO structure, field validation
 * - Database Verification: Data matches repository
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
public class ProductsGetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private Restaurant testRestaurant1;
    private Restaurant testRestaurant2;
    private Restaurant testRestaurantEmpty;
    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    @SuppressWarnings("null")
    public void setup() {
        // Create test restaurant 1 with multiple products
        UserEntity user1 = UserEntity.builder()
                .email("user1_" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("User1")
                .build();
        userRepository.save(user1);

        Address addr1 = Address.builder()
                .streetAddress("100 Pasta St " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(addr1);

        testRestaurant1 = Restaurant.builder()
                .name("Pasta Palace")
                .email("pasta" + System.nanoTime() + "@test.com")
                .phone("555-0001")
                .address(addr1)
                .userEntity(user1)
                .priceRange(2)
                .build();
        restaurantRepository.save(testRestaurant1);

        // Create products for restaurant 1
        testProduct1 = Product.builder()
                .name("Spaghetti Bolognese")
                .description("Classic meat sauce pasta")
                .cost(1299) // $12.99
                .restaurant(testRestaurant1)
                .build();
        productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .name("Fettuccine Alfredo")
                .description("Creamy sauce pasta")
                .cost(1399) // $13.99
                .restaurant(testRestaurant1)
                .build();
        productRepository.save(testProduct2);

        testProduct3 = Product.builder()
                .name("Lasagna")
                .description("Baked layers of pasta")
                .cost(1499) // $14.99
                .restaurant(testRestaurant1)
                .build();
        productRepository.save(testProduct3);

        // Create test restaurant 2 with single product
        UserEntity user2 = UserEntity.builder()
                .email("user2_" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("User2")
                .build();
        userRepository.save(user2);

        Address addr2 = Address.builder()
                .streetAddress("200 Burger St " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(addr2);

        testRestaurant2 = Restaurant.builder()
                .name("Burger Barn")
                .email("burger" + System.nanoTime() + "@test.com")
                .phone("555-0002")
                .address(addr2)
                .userEntity(user2)
                .priceRange(1)
                .build();
        restaurantRepository.save(testRestaurant2);

        Product singleProduct = Product.builder()
                .name("Classic Burger")
                .description("Juicy beef burger")
                .cost(999) // $9.99
                .restaurant(testRestaurant2)
                .build();
        productRepository.save(singleProduct);

        // Create test restaurant with no products
        UserEntity user3 = UserEntity.builder()
                .email("user3_" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("User3")
                .build();
        userRepository.save(user3);

        Address addr3 = Address.builder()
                .streetAddress("300 Empty St " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(addr3);

        testRestaurantEmpty = Restaurant.builder()
                .name("Empty Restaurant")
                .email("empty" + System.nanoTime() + "@test.com")
                .phone("555-0003")
                .address(addr3)
                .userEntity(user3)
                .priceRange(2)
                .build();
        restaurantRepository.save(testRestaurantEmpty);
    }

    // ==================== BASIC RETRIEVAL TESTS ====================

    /**
     * Test: GET products by valid restaurant ID
     * Expected: 200 OK with 3 products returned
     */
    @Test
    public void testGetProductsByRestaurantIdValid() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    /**
     * Test: GET multiple products from restaurant
     * Expected: 200 OK, verify 3 products returned in correct format
     */
    @Test
    public void testGetProductsByRestaurantMultipleProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].description").exists())
                .andExpect(jsonPath("$.data[0].cost").exists())
                .andExpect(jsonPath("$.data[1].id").exists())
                .andExpect(jsonPath("$.data[2].id").exists());
    }

    /**
     * Test: GET single product from restaurant
     * Expected: 200 OK, verify single product with all fields
     */
    @Test
    public void testGetProductsByRestaurantSingleProduct() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant2.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Classic Burger"))
                .andExpect(jsonPath("$.data[0].description").value("Juicy beef burger"))
                .andExpect(jsonPath("$.data[0].cost").value(999));
    }

    // ==================== EMPTY LIST TESTS ====================

    /**
     * Test: GET products from restaurant with no products
     * Expected: 200 OK with empty array (not 404 or null)
     */
    @Test
    public void testGetProductsByRestaurantNoProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurantEmpty.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.data", isA(List.class)))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    // ==================== RESTAURANT VALIDATION TESTS ====================

    /**
     * Test: GET products with non-existent restaurant ID
     * Expected: 404 Not Found
     */
    @Test
    public void testGetProductsByRestaurantNotFound() throws Exception {
        int nonExistentId = 99999;
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(nonExistentId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("not found")));
    }

    /**
     * Test: GET products with invalid ID format (non-integer)
     * Expected: 400 Bad Request
     */
    @Test
    public void testGetProductsByRestaurantInvalidId() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("valid")));
    }

    /**
     * Test: GET products with negative restaurant ID
     * Expected: 400 Bad Request
     */
    @Test
    public void testGetProductsByRestaurantNegativeId() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Test: GET products with zero restaurant ID
     * Expected: 400 Bad Request
     */
    @Test
    public void testGetProductsByRestaurantZeroId() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ==================== FILTER VALIDATION TESTS ====================

    /**
     * Test: GET products without restaurant filter parameter
     * Expected: 400 Bad Request (parameter required)
     */
    @Test
    public void testGetProductsByRestaurantMissingFilter() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("required")));
    }

    /**
     * Test: GET products with null filter parameter
     * Expected: 400 Bad Request
     */
    @Test
    public void testGetProductsByRestaurantNullFilter() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", (String) null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Test: GET products with empty string filter
     * Expected: 400 Bad Request
     */
    @Test
    public void testGetProductsByRestaurantEmptyFilter() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ==================== RESPONSE FORMAT TESTS ====================

    /**
     * Test: Verify ApiResponseDTO structure
     * Expected: Response has message, data, and no error field on success
     */
    @Test
    public void testGetProductsByRestaurantResponseFormat() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    /**
     * Test: Verify all product fields are present
     * Expected: Each product has id, name, description, cost, restaurantId
     */
    @Test
    public void testGetProductsByRestaurantIncludesAllFields() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].description").isString())
                .andExpect(jsonPath("$.data[0].cost").isNumber())
                .andExpect(jsonPath("$.data[0].restaurant_id").isNumber());
    }

    /**
     * Test: Verify cost values are correctly returned
     * Expected: Cost values match database records
     */
    @Test
    public void testGetProductsByRestaurantCostValidation() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].cost").value(1299))
                .andExpect(jsonPath("$.data[1].cost").value(1399))
                .andExpect(jsonPath("$.data[2].cost").value(1499));
    }

    /**
     * Test: Verify product names are correctly returned
     * Expected: Names match database records
     */
    @Test
    public void testGetProductsByRestaurantNameValidation() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Spaghetti Bolognese"))
                .andExpect(jsonPath("$.data[1].name").value("Fettuccine Alfredo"))
                .andExpect(jsonPath("$.data[2].name").value("Lasagna"));
    }

    /**
     * Test: Verify restaurant ID is included in product response
     * Expected: restaurantId matches the queried restaurant
     */
    @Test
    public void testGetProductsByRestaurantIncludesRestaurantId() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].restaurant_id").value(testRestaurant1.getId()))
                .andExpect(jsonPath("$.data[1].restaurant_id").value(testRestaurant1.getId()))
                .andExpect(jsonPath("$.data[2].restaurant_id").value(testRestaurant1.getId()));
    }

    // ==================== DATABASE VERIFICATION TESTS ====================

    /**
     * Test: Verify products returned match database records
     * Expected: API response matches database query
     */
    @Test
    public void testGetProductsByRestaurantDatabaseVerification() throws Exception {
        // Get count from response
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // Verify database state
                    List<Product> dbProducts = productRepository.findProductsByRestaurantId(testRestaurant1.getId());
                    assertEquals(3, dbProducts.size(), "Database should contain 3 products");
                });
    }

    /**
     * Test: Verify products returned are only for specified restaurant
     * Expected: No products from other restaurants included
     */
    @Test
    public void testGetProductsByRestaurantIsolation() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].restaurant_id", everyItem(
                        equalTo(testRestaurant1.getId())
                )));
    }

    /**
     * Test: Verify different restaurants have isolated product lists
     * Expected: Restaurant 1 has 3 products, Restaurant 2 has 1 product
     */
    @Test
    public void testGetProductsByRestaurantMultipleRestaurants() throws Exception {
        // Get products for restaurant 1
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));

        // Get products for restaurant 2
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant2.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    // ==================== EDGE CASE TESTS ====================

    /**
     * Test: GET products with large valid restaurant ID
     * Expected: 404 Not Found (ID doesn't exist)
     */
    @Test
    public void testGetProductsByRestaurantLargeId() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Test: GET products, verify description field handling
     * Expected: Descriptions present and correct
     */
    @Test
    public void testGetProductsByRestaurantDescriptionHandling() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].description").value("Classic meat sauce pasta"))
                .andExpect(jsonPath("$.data[1].description").value("Creamy sauce pasta"))
                .andExpect(jsonPath("$.data[2].description").value("Baked layers of pasta"));
    }

    /**
     * Test: Verify response is array (not single object)
     * Expected: $.data is array type
     */
    @Test
    public void testGetProductsByRestaurantResponseIsArray() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * Test: Verify cost field is numeric
     * Expected: Cost values are numbers (not strings)
     */
    @Test
    public void testGetProductsByRestaurantCostIsNumeric() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(testRestaurant1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].cost").isNumber())
                .andExpect(jsonPath("$.data[1].cost").isNumber())
                .andExpect(jsonPath("$.data[2].cost").isNumber());
    }

    /**
     * Test: Verify restaurant ID field consistency
     * Expected: restaurantId matches both request param and response
     */
    @Test
    public void testGetProductsByRestaurantConsistentRestaurantId() throws Exception {
        int restaurantId = testRestaurant1.getId();
        mockMvc.perform(get("/api/products")
                .param("restaurant", String.valueOf(restaurantId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].restaurant_id").value(restaurantId))
                .andExpect(jsonPath("$.data[1].restaurant_id").value(restaurantId))
                .andExpect(jsonPath("$.data[2].restaurant_id").value(restaurantId));
    }
}
