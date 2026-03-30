package com.rocketFoodDelivery.rocketFood.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrdersApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourierStatusRepository courierStatusRepository;

    @Autowired
    private OrderService orderService;

    private Restaurant testRestaurant;
    private Customer testCustomer;
    private Courier testCourier;
    private OrderStatus testOrderStatus;
    private Order testOrder1;
    private Order testOrder2;

    @BeforeEach
    public void setup() {
        // Create test data with required relationships
        
        // Create order status
        testOrderStatus = OrderStatus.builder().name("PENDING").build();
        orderStatusRepository.save(testOrderStatus);

        // Create address for restaurant
        Address restaurantAddress = Address.builder()
                .streetAddress("123 Restaurant St")
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(restaurantAddress);

        // Create user for restaurant
        UserEntity restaurantUser = UserEntity.builder()
                .email("restaurant@test.com")
                .password("pass123")
                .name("Restaurant User")
                .build();
        userRepository.save(restaurantUser);

        // Create restaurant
        testRestaurant = Restaurant.builder()
                .email("rest@test.com")
                .phone("555-rest")
                .name("Test Restaurant")
                .address(restaurantAddress)
                .userEntity(restaurantUser)
                .priceRange(2)
                .build();
        restaurantRepository.save(testRestaurant);

        // Create address for customer
        Address customerAddress = Address.builder()
                .streetAddress("456 Customer Ave")
                .city("Cambridge")
                .postalCode("02138")
                .build();
        addressRepository.save(customerAddress);

        // Create user for customer
        UserEntity customerUser = UserEntity.builder()
                .email("customer@test.com")
                .password("pass123")
                .name("Customer User")
                .build();
        userRepository.save(customerUser);

        // Create customer
        testCustomer = Customer.builder()
                .email("customer@test.com")
                .phone("555-0001")
                .address(customerAddress)
                .userEntity(customerUser)
                .active(true)
                .build();
        customerRepository.save(testCustomer);

        // Create address for courier
        Address courierAddress = Address.builder()
                .streetAddress("789 Courier Ln")
                .city("Somerville")
                .postalCode("02144")
                .build();
        addressRepository.save(courierAddress);

        // Create user for courier
        UserEntity courierUser = UserEntity.builder()
                .email("courier@test.com")
                .password("pass123")
                .name("Courier User")
                .build();
        userRepository.save(courierUser);

        // Create courier status
        CourierStatus courierStatus = CourierStatus.builder().name("AVAILABLE").build();
        courierStatusRepository.save(courierStatus);

        // Create courier
        testCourier = Courier.builder()
                .address(courierAddress)
                .email("courier@test.com")
                .phone("555-0002")
                .address(courierAddress)
                .userEntity(courierUser)
                .courierStatus(courierStatus)
                .build();
        courierRepository.save(testCourier);

        // Create test orders
        testOrder1 = Order.builder()
                .restaurant(testRestaurant)
                .customer(testCustomer)
                .order_status(testOrderStatus)
                .courier(testCourier)
                .restaurant_rating(4)
                .build();
        orderRepository.save(testOrder1);

        testOrder2 = Order.builder()
                .restaurant(testRestaurant)
                .customer(testCustomer)
                .order_status(testOrderStatus)
                .courier(testCourier)
                .restaurant_rating(5)
                .build();
        orderRepository.save(testOrder2);
    }

    // ==================== GET BY RESTAURANT TYPE ====================

    @Test
    public void testGetOrdersByRestaurantType_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    public void testGetOrdersByRestaurantType_VerifyDataStructure() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber());
    }

    @Test
    public void testGetOrdersByRestaurantType_VerifyAllOrdersRetrieved() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    // ==================== GET BY CUSTOMER TYPE ====================

    @Test
    public void testGetOrdersByCustomerType_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "customer")
                .param("id", String.valueOf(testCustomer.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    public void testGetOrdersByCustomerType_VerifyDataCorrect() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "customer")
                .param("id", String.valueOf(testCustomer.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    // ==================== GET BY COURIER TYPE ====================

    @Test
    public void testGetOrdersByCourierType_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "courier")
                .param("id", String.valueOf(testCourier.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    public void testGetOrdersByCourierType_VerifyDataCorrect() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "courier")
                .param("id", String.valueOf(testCourier.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    // ==================== EMPTY RESULTS ====================

    @Test
    public void testGetOrdersWithNoResults_ShouldReturn200EmptyList() throws Exception {
        // Create a courier with no orders
        Address emptyCourierAddress = Address.builder()
                .streetAddress("Empty St")
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(emptyCourierAddress);

        UserEntity emptyCourierUser = UserEntity.builder()
                .email("empty@courier.com")
                .password("pass")
                .name("Empty")
                .build();
        userRepository.save(emptyCourierUser);

        CourierStatus emptyCourierStatus = CourierStatus.builder().name("AVAILABLE").build();
        courierStatusRepository.save(emptyCourierStatus);

        Courier emptyCourier = Courier.builder()
                .email("empty@courier.com")
                .phone("555-9999")
                .address(emptyCourierAddress)
                .userEntity(emptyCourierUser)
                .courierStatus(emptyCourierStatus)
                .build();
        courierRepository.save(emptyCourier);

        mockMvc.perform(get("/api/orders")
                .param("type", "courier")
                .param("id", String.valueOf(emptyCourier.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    // ==================== INVALID TYPE PARAMETER ====================

    @Test
    public void testGetOrdersWithInvalidType_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "invalid_type")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOrdersWithEmptyType_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isBadRequest());
    }

    // ==================== MISSING ID PARAMETER ====================

    @Test
    public void testGetOrdersWithoutIdParameter_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOrdersWithInvalidIdFormat_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", "not_a_number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // ==================== NON-EXISTENT ENTITY ====================

    @Test
    public void testGetOrdersWithNonExistentRestaurantId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOrdersWithNonExistentCustomerId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "customer")
                .param("id", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOrdersWithNonExistentCourierId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "courier")
                .param("id", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // ==================== CASE INSENSITIVITY ====================

    @Test
    public void testGetOrdersWithCapitalizedType_ShouldWork() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "Restaurant")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOrdersWithUppercaseType_ShouldWork() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "CUSTOMER")
                .param("id", String.valueOf(testCustomer.getId())))
                .andExpect(status().isOk());
    }

    // ==================== DELETE ORDER ====================

    @Test
    public void testDeleteOrder_ShouldReturn200() throws Exception {
        int orderId = testOrder1.getId();
        
        mockMvc.perform(delete("/api/order/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    public void testDeleteOrder_VerifyOrderDeleted() throws Exception {
        int orderId = testOrder1.getId();
        
        mockMvc.perform(delete("/api/order/" + orderId))
                .andExpect(status().isOk());
        
        // Verify order is deleted from database
        assert !orderRepository.existsById(orderId);
    }

    @Test
    public void testDeleteOrder_WithNonExistentId_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/order/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteOrder_WithInvalidIdFormat_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/api/order/invalid_id"))
                .andExpect(status().isBadRequest());
    }

    // ==================== RESPONSE FORMAT VALIDATION ====================

    @Test
    public void testResponseHasCorrectStructure_GET() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", String.valueOf(testRestaurant.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testErrorResponseHasCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "invalid")
                .param("id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // ==================== NEGATIVE ID PARAMETER ====================

    @Test
    public void testGetOrdersWithNegativeId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOrdersWithZeroId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("type", "restaurant")
                .param("id", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
