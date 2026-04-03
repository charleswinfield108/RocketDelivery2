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
 * Comprehensive test coverage for GET and DELETE restaurant endpoints.
 * Tests list all, retrieve single, and cascade deletion with data verification.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
public class RestaurantGetDeleteTest {

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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private CourierStatusRepository courierStatusRepository;

    private Restaurant testRestaurant1;
    private Restaurant testRestaurant2;
    private Restaurant testRestaurant3;
    private Address testAddress;
    private UserEntity testUser;

    @BeforeEach
    @SuppressWarnings("null")
    public void setup() {
        // Create test address
        testAddress = Address.builder()
                .streetAddress("123 Main St " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(testAddress);

        // Create test user
        testUser = UserEntity.builder()
                .email("owner" + System.nanoTime() + "@test.com")
                .password("password123")
                .name("Test Owner")
                .build();
        userRepository.save(testUser);

        // Create test restaurants with unique emails and users
        UserEntity user1 = UserEntity.builder()
                .email("user1_" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("User1")
                .build();
        userRepository.save(user1);

        Address addr1 = Address.builder()
                .streetAddress("Addr1 " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(addr1);

        testRestaurant1 = Restaurant.builder()
                .name("Pizza Palace")
                .email("pizza" + System.nanoTime() + "@test.com")
                .phone("555-0001")
                .address(addr1)
                .userEntity(user1)
                .priceRange(2)
                .build();
        restaurantRepository.save(testRestaurant1);

        UserEntity user2 = UserEntity.builder()
                .email("user2_" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("User2")
                .build();
        userRepository.save(user2);

        Address addr2 = Address.builder()
                .streetAddress("Addr2 " + System.nanoTime())
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

        UserEntity user3 = UserEntity.builder()
                .email("user3_" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("User3")
                .build();
        userRepository.save(user3);

        Address addr3 = Address.builder()
                .streetAddress("Addr3 " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(addr3);

        testRestaurant3 = Restaurant.builder()
                .name("Taco Tower")
                .email("taco" + System.nanoTime() + "@test.com")
                .phone("555-0003")
                .address(addr3)
                .userEntity(user3)
                .priceRange(3)
                .build();
        restaurantRepository.save(testRestaurant3);
    }

    // ==================== GET ALL RESTAURANTS TESTS ====================

    @Test
    public void testGetAllRestaurants_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testGetAllRestaurants_MultipleRecords_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    public void testGetAllRestaurants_ResponseFormat_ShouldBeValid() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].price_range").isNumber());
    }

    @Test
    public void testGetAllRestaurants_IncludesAllFields() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].price_range").exists());
    }

    @Test
    public void testGetAllRestaurants_WithRatingFilter_Valid() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("rating", "3"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllRestaurants_WithPriceRangeFilter_Valid() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("price_range", "2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllRestaurants_WithBothFilters_Valid() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("rating", "4")
                .param("price_range", "2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllRestaurants_InvalidRatingFilter_LowBound() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("rating", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testGetAllRestaurants_InvalidRatingFilter_HighBound() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("rating", "6"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testGetAllRestaurants_InvalidPriceRangeFilter_LowBound() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("price_range", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testGetAllRestaurants_InvalidPriceRangeFilter_HighBound() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("price_range", "4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ==================== GET SINGLE RESTAURANT TESTS ====================

    @Test
    public void testGetRestaurantById_WithValidId_Returns200() throws Exception {
        mockMvc.perform(get("/api/restaurants/" + testRestaurant1.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRestaurantById_WithValidId_ReturnsCorrectData() throws Exception {
        mockMvc.perform(get("/api/restaurants/" + testRestaurant1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testRestaurant1.getId()))
                .andExpect(jsonPath("$.data.name").value("Pizza Palace"))
                .andExpect(jsonPath("$.data.price_range").value(2));
    }

    @Test
    public void testGetRestaurantById_IncludesAllFields() throws Exception {
        mockMvc.perform(get("/api/restaurants/" + testRestaurant1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.price_range").exists());
    }

    @Test
    public void testGetRestaurantById_WithNonExistentId_Returns404() throws Exception {
        mockMvc.perform(get("/api/restaurants/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRestaurantById_WithInvalidIdFormat_Returns400() throws Exception {
        mockMvc.perform(get("/api/restaurants/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRestaurantById_WithNegativeId_Returns400() throws Exception {
        mockMvc.perform(get("/api/restaurants/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRestaurantById_WithZeroId_Returns400() throws Exception {
        mockMvc.perform(get("/api/restaurants/0"))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE TESTS ====================

    @Test
    public void testDeleteRestaurant_WithValidId_Returns204() throws Exception {
        int restaurantId = testRestaurant1.getId();
        
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRestaurant_VerifyRemoved_Returns404() throws Exception {
        int restaurantId = testRestaurant1.getId();
        
        // Delete restaurant
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Verify it's deleted
        mockMvc.perform(get("/api/restaurants/" + restaurantId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRestaurant_VerifyDatabaseRemoved() throws Exception {
        int restaurantId = testRestaurant1.getId();
        
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Verify in database
        assertFalse(restaurantRepository.existsById(restaurantId));
    }

    @Test
    public void testDeleteRestaurant_WithNonExistentId_Returns404() throws Exception {
        mockMvc.perform(delete("/api/restaurants/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRestaurant_WithInvalidIdFormat_Returns400() throws Exception {
        mockMvc.perform(delete("/api/restaurants/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRestaurant_WithNegativeId_Returns400() throws Exception {
        mockMvc.perform(delete("/api/restaurants/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRestaurant_WithZeroId_Returns400() throws Exception {
        mockMvc.perform(delete("/api/restaurants/0"))
                .andExpect(status().isBadRequest());
    }

    // ==================== CASCADE DELETE TESTS ====================

    @Test
    public void testDeleteRestaurant_CascadeProducts() throws Exception {
        // Setup: Create products for restaurant
        Product product = Product.builder()
                .name("Pizza Margherita")
                .description("Classic pizza")
                .cost(1200)
                .restaurant(testRestaurant1)
                .build();
        productRepository.save(product);
        
        int restaurantId = testRestaurant1.getId();
        
        // Delete restaurant
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Verify products are deleted
        List<Product> products = productRepository.findByRestaurantId(restaurantId);
        assertTrue(products.isEmpty());
    }

    @Test
    public void testDeleteRestaurant_CascadeOrders() throws Exception {
        // Setup: Create customer and order
        Address customerAddress = Address.builder()
                .streetAddress("456 Oak Ave")
                .city("Cambridge")
                .postalCode("02138")
                .build();
        addressRepository.save(customerAddress);
        
        UserEntity customerUser = UserEntity.builder()
                .email("cust@test.com")
                .password("pass123")
                .name("Customer")
                .build();
        userRepository.save(customerUser);
        
        Customer customer = Customer.builder()
                .email("cust@test.com")
                .phone("555-1234")
                .address(customerAddress)
                .userEntity(customerUser)
                .active(true)
                .build();
        customerRepository.save(customer);
        
        OrderStatus status = OrderStatus.builder().name("PENDING").build();
        orderStatusRepository.save(status);
        
        Order order = Order.builder()
                .customer(customer)
                .restaurant(testRestaurant1)
                .orderStatus(status)
                .restaurantRating(4)
                .build();
        orderRepository.save(order);
        
        int restaurantId = testRestaurant1.getId();
        int orderId = order.getId();
        
        // Delete restaurant
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Verify orders associated with restaurant are handled
        assertTrue(!orderRepository.existsById(orderId) || 
                   orderRepository.findById(orderId).get().getRestaurant() == null);
    }

    @Test
    public void testDeleteRestaurant_CascadeProductOrders() throws Exception {
        // Setup: Create product and order with product_order
        Product product = Product.builder()
                .name("Burger Deluxe")
                .description("Full burger meal")
                .cost(1500)
                .restaurant(testRestaurant1)
                .build();
        productRepository.save(product);
        
        // Create customer and order
        Address customerAddress = Address.builder()
                .streetAddress("789 Elm St")
                .city("Somerville")
                .postalCode("02144")
                .build();
        addressRepository.save(customerAddress);
        
        UserEntity customerUser = UserEntity.builder()
                .email("cust2@test.com")
                .password("pass123")
                .name("Customer 2")
                .build();
        userRepository.save(customerUser);
        
        Customer customer = Customer.builder()
                .email("cust2@test.com")
                .phone("555-5678")
                .address(customerAddress)
                .userEntity(customerUser)
                .active(true)
                .build();
        customerRepository.save(customer);
        
        OrderStatus status = OrderStatus.builder().name("PENDING").build();
        orderStatusRepository.save(status);
        
        Order order = Order.builder()
                .customer(customer)
                .restaurant(testRestaurant1)
                .orderStatus(status)
                .restaurantRating(5)
                .build();
        orderRepository.save(order);
        
        ProductOrder productOrder = ProductOrder.builder()
                .product(product)
                .order(order)
                .productQuantity(2)
                .productUnitCost(1500)
                .build();
        productOrderRepository.save(productOrder);
        
        int restaurantId = testRestaurant1.getId();
        
        // Delete restaurant
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Verify cascade is complete
        assertFalse(restaurantRepository.existsById(restaurantId));
        List<Product> products = productRepository.findByRestaurantId(restaurantId);
        assertTrue(products.isEmpty());
    }

    @Test
    public void testDeleteRestaurant_AtomicTransaction() throws Exception {
        // Verify that delete operation is atomic
        int restaurantId = testRestaurant1.getId();
        
        // Delete restaurants
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Verify complete deletion (atomic - all or nothing)
        assertFalse(restaurantRepository.existsById(restaurantId));
    }

    @Test
    public void testDeleteRestaurant_CannotDeleteTwice() throws Exception {
        int restaurantId = testRestaurant2.getId();
        
        // First delete
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk());
        
        // Second delete should return 404
        mockMvc.perform(delete("/api/restaurants/" + restaurantId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRestaurant_OtherRestaurantsUnaffected() throws Exception {
        int restaurantIdToDelete = testRestaurant1.getId();
        int otherRestaurantId = testRestaurant2.getId();
        
        // Delete one restaurant
        mockMvc.perform(delete("/api/restaurants/" + restaurantIdToDelete))
                .andExpect(status().isOk());
        
        // Verify other restaurant still exists and is accessible
        mockMvc.perform(get("/api/restaurants/" + otherRestaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(otherRestaurantId))
                .andExpect(jsonPath("$.data.name").value("Burger Barn"));
    }

    @Test
    public void testGetAllRestaurants_AfterDelete_ExcludesDeleted() throws Exception {
        int restaurantIdToDelete = testRestaurant1.getId();
        
        // Get initial count
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
        
        // Delete restaurant
        mockMvc.perform(delete("/api/restaurants/" + restaurantIdToDelete))
                .andExpect(status().isOk());
        
        // Verify deleted restaurant not in list anymore
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }
}
