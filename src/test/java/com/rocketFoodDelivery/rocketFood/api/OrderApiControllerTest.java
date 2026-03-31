package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductItemDTO;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test coverage for POST /api/orders endpoint.
 * Tests creation of orders with comprehensive success and failure scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
public class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private Restaurant testRestaurant;
    private Customer testCustomer;
    private Product testProduct1;
    private Product testProduct2;
    private OrderStatus pendingStatus;
    private UserEntity restaurantUser;
    private UserEntity customerUser;

    @BeforeEach
    @SuppressWarnings("null")
    public void setup() {
        // Create order status
        pendingStatus = OrderStatus.builder().name("PENDING").build();
        orderStatusRepository.save(pendingStatus);

        // Create restaurant
        Address restaurantAddress = Address.builder()
                .streetAddress("123 Restaurant St")
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(restaurantAddress);

        restaurantUser = UserEntity.builder()
                .email("restaurant@test.com")
                .password("pass123")
                .name("Restaurant User")
                .build();
        userRepository.save(restaurantUser);

        testRestaurant = Restaurant.builder()
                .email("rest@test.com")
                .phone("555-rest")
                .name("Test Restaurant")
                .address(restaurantAddress)
                .userEntity(restaurantUser)
                .priceRange(2)
                .build();
        restaurantRepository.save(testRestaurant);

        // Create customer
        Address customerAddress = Address.builder()
                .streetAddress("456 Customer Ave")
                .city("Cambridge")
                .postalCode("02138")
                .build();
        addressRepository.save(customerAddress);

        customerUser = UserEntity.builder()
                .email("customer@test.com")
                .password("pass123")
                .name("Customer User")
                .build();
        userRepository.save(customerUser);

        testCustomer = Customer.builder()
                .email("customer@test.com")
                .phone("555-0001")
                .address(customerAddress)
                .userEntity(customerUser)
                .active(true)
                .build();
        customerRepository.save(testCustomer);

        // Create products
        testProduct1 = Product.builder()
                .name("Burger")
                .description("Delicious burger")
                .cost(1000)
                .restaurant(testRestaurant)
                .build();
        productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .name("Pizza")
                .description("Cheesy pizza")
                .cost(1500)
                .restaurant(testRestaurant)
                .build();
        productRepository.save(testProduct2);
    }

    // ==================== POST SUCCESS TESTS ====================

    @Test
    public void testCreateOrderWithValidData_ShouldReturn201() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(
                        createProductItem(testProduct1.getId(), 2)
                ),
                2000 // 1000 * 2
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    public void testCreateOrderWithMultipleProducts_ShouldReturn201() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(
                        createProductItem(testProduct1.getId(), 2),
                        createProductItem(testProduct2.getId(), 1)
                ),
                3500 // (1000 * 2) + (1500 * 1)
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.products", hasSize(2)));
    }

    @Test
    public void testCreateOrderResponseFormat_ShouldIncludeAllFields() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.customer_id").value(testCustomer.getId()))
                .andExpect(jsonPath("$.data.restaurant_id").value(testRestaurant.getId()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.total_cost").value(1000))
                .andExpect(jsonPath("$.data.products").isArray());
    }

    @Test
    public void testCreateOrderGeneratesAutoId_ShouldNotBeNull() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    public void testCreateOrderInitialStatus_ShouldBePENDING() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    public void testCreateOrderIncludesAllProducts_InResponse() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(
                        createProductItem(testProduct1.getId(), 2),
                        createProductItem(testProduct2.getId(), 3)
                ),
                6500 // (1000 * 2) + (1500 * 3)
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.products", hasSize(2)))
                .andExpect(jsonPath("$.data.products[0].product_name").isNotEmpty())
                .andExpect(jsonPath("$.data.products[0].quantity").isNumber());
    }

    @Test
    public void testCreateOrderPersistence_ShouldSaveToDatabase() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify order exists in database
        assert orderRepository.count() > 0;
    }

    @Test
    public void testCreateOrderProductOrderCreation_ShouldCreateJunctionRecords() throws Exception {
        long initialProductOrderCount = productOrderRepository.count();

        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(
                        createProductItem(testProduct1.getId(), 1),
                        createProductItem(testProduct2.getId(), 1)
                ),
                2500
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify ProductOrder records created (2 products)
        assert productOrderRepository.count() == initialProductOrderCount + 2;
    }

    @Test
    public void testCreateOrderProductQuantities_ShouldMatchRequest() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 5)),
                5000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.products[0].quantity").value(5));
    }

    @Test
    public void testCreateOrderWithDifferentQuantities_ShouldPreserveEachQuantity() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(
                        createProductItem(testProduct1.getId(), 2),
                        createProductItem(testProduct2.getId(), 3)
                ),
                6500 // (1000 * 2) + (1500 * 3)
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.products[0].quantity").value(2))
                .andExpect(jsonPath("$.data.products[1].quantity").value(3));
    }

    @Test
    public void testCreateOrderWithLargeQuantity_ShouldWork() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 999)),
                999000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.products[0].quantity").value(999));
    }

    @Test
    public void testCreateOrderTotalPrice_ShouldMatchProducts() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 2)),
                2000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.total_cost").value(2000));
    }

    @Test
    public void testCreateOrderMultipleProductsTotalPrice_ShouldBeCorrect() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(
                        createProductItem(testProduct1.getId(), 2),
                        createProductItem(testProduct2.getId(), 1)
                ),
                3500
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.total_cost").value(3500));
    }

    // ==================== POST FAILURE TESTS ====================

    @Test
    public void testCreateOrderMissingCustomerId_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );
        request.setCustomer_id(0);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderMissingRestaurantId_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );
        request.setRestaurant_id(0);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderMissingProductsArray_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                null,
                0
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderEmptyProductsArray_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                new ArrayList<>(),
                0
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderMissingTotalPrice_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );
        request.setTotal_cost(0);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderNonExistentCustomer_ShouldReturn404() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                99999,
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderNonExistentRestaurant_ShouldReturn404() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                99999,
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderNonExistentProduct_ShouldReturn404() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(99999, 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderProductFromDifferentRestaurant_ShouldReturn400() throws Exception {
        // Create another restaurant with a product
        Address anotherRestaurantAddress = Address.builder()
                .streetAddress("999 Other St")
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(anotherRestaurantAddress);

        UserEntity anotherRestaurantUser = UserEntity.builder()
                .email("other@test.com")
                .password("pass123")
                .name("Other User")
                .build();
        userRepository.save(anotherRestaurantUser);

        Restaurant otherRestaurant = Restaurant.builder()
                .email("other@rest.com")
                .phone("555-other")
                .name("Other Restaurant")
                .address(anotherRestaurantAddress)
                .userEntity(anotherRestaurantUser)
                .priceRange(2)
                .build();
        restaurantRepository.save(otherRestaurant);

        Product otherProduct = Product.builder()
                .name("Other Product")
                .description("From other restaurant")
                .cost(500)
                .restaurant(otherRestaurant)
                .build();
        productRepository.save(otherProduct);

        // Try to order product from other restaurant
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(otherProduct.getId(), 1)),
                500
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderPriceMismatch_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 2)),
                5000 // Wrong: should be 2000 (1000 * 2)
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderZeroQuantity_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 0)),
                0
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderNegativeQuantity_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), -5)),
                -5000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderNegativeCustomerId_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                -1,
                testRestaurant.getId(),
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testCreateOrderNegativeRestaurantId_ShouldReturn400() throws Exception {
        ApiCreateOrderRequestDTO request = createValidOrderRequest(
                testCustomer.getId(),
                -1,
                List.of(createProductItem(testProduct1.getId(), 1)),
                1000
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // ==================== HELPER METHODS ====================

    /**
     * Creates a valid order request DTO with the provided parameters.
     */
    private ApiCreateOrderRequestDTO createValidOrderRequest(
            int customerId,
            int restaurantId,
            List<ApiProductItemDTO> products,
            long totalCost) {
        ApiCreateOrderRequestDTO request = new ApiCreateOrderRequestDTO();
        request.setCustomer_id(customerId);
        request.setRestaurant_id(restaurantId);
        request.setProducts(products);
        request.setTotal_cost(totalCost);
        return request;
    }

    /**
     * Creates a product item DTO with product ID and quantity.
     */
    private ApiProductItemDTO createProductItem(int productId, int quantity) {
        ApiProductItemDTO item = new ApiProductItemDTO();
        item.setProduct_id(productId);
        item.setProduct_quantity(quantity);
        return item;
    }
}
