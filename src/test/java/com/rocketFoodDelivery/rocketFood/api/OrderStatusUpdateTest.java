package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test coverage for POST /api/order/{id}/status endpoint.
 * Tests order status update with various scenarios (success, validation errors, edge cases).
 * 
 * Test Categories:
 * - Successful Updates: Valid order ID and status
 * - Status Values: Different status strings (pending, in progress, delivered, cancelled)
 * - Validation: Missing/empty status, invalid formats
 * - Order Validation: Non-existent ID, invalid formats, negative/zero IDs
 * - Response Format: ApiResponseDTO structure, field validation
 * - Database Verification: Status updated in database
 * - Edge Cases: Idempotency, multiple updates
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
public class OrderStatusUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder1;
    private Order testOrder2;
    private OrderStatus pendingStatus;
    private OrderStatus deliveredStatus;
    private OrderStatus inProgressStatus;
    private OrderStatus cancelledStatus;

    @BeforeEach
    @SuppressWarnings("null")
    public void setup() {
        // Create or get order statuses
        pendingStatus = orderStatusRepository.findAll().stream()
                .filter(os -> "PENDING".equalsIgnoreCase(os.getName()))
                .findFirst()
                .orElseGet(() -> {
                    OrderStatus status = OrderStatus.builder().name("PENDING").build();
                    return orderStatusRepository.save(status);
                });

        deliveredStatus = orderStatusRepository.findAll().stream()
                .filter(os -> "DELIVERED".equalsIgnoreCase(os.getName()))
                .findFirst()
                .orElseGet(() -> {
                    OrderStatus status = OrderStatus.builder().name("DELIVERED").build();
                    return orderStatusRepository.save(status);
                });

        inProgressStatus = orderStatusRepository.findAll().stream()
                .filter(os -> "IN PROGRESS".equalsIgnoreCase(os.getName()))
                .findFirst()
                .orElseGet(() -> {
                    OrderStatus status = OrderStatus.builder().name("IN PROGRESS").build();
                    return orderStatusRepository.save(status);
                });

        cancelledStatus = orderStatusRepository.findAll().stream()
                .filter(os -> "CANCELLED".equalsIgnoreCase(os.getName()))
                .findFirst()
                .orElseGet(() -> {
                    OrderStatus status = OrderStatus.builder().name("CANCELLED").build();
                    return orderStatusRepository.save(status);
                });

        // Create test restaurant
        UserEntity restaurantUser = UserEntity.builder()
                .email("restaurant" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("Restaurant User")
                .build();
        userRepository.save(restaurantUser);

        Address restaurantAddr = Address.builder()
                .streetAddress("Restaurant St " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(restaurantAddr);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .email("rest" + System.nanoTime() + "@test.com")
                .phone("555-1234")
                .address(restaurantAddr)
                .userEntity(restaurantUser)
                .priceRange(2)
                .build();
        restaurantRepository.save(restaurant);

        // Create test customer
        UserEntity customerUser = UserEntity.builder()
                .email("customer_user" + System.nanoTime() + "@test.com")
                .password("pass")
                .name("Test Customer")
                .build();
        userRepository.save(customerUser);

        Address customerAddr = Address.builder()
                .streetAddress("Customer St " + System.nanoTime())
                .city("Boston")
                .postalCode("02101")
                .build();
        addressRepository.save(customerAddr);

        Customer customer = Customer.builder()
                .userEntity(customerUser)
                .email("customer" + System.nanoTime() + "@test.com")
                .phone("555-5678")
                .address(customerAddr)
                .active(true)
                .build();
        customerRepository.save(customer);

        // Create test orders
        testOrder1 = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .orderStatus(pendingStatus)
                .restaurantRating(4)
                .build();
        orderRepository.save(testOrder1);

        testOrder2 = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .orderStatus(pendingStatus)
                .restaurantRating(3)
                .build();
        orderRepository.save(testOrder2);
    }

    // ==================== SUCCESSFUL STATUS UPDATE TESTS ====================

    /**
     * Test: POST with valid order ID and status
     * Expected: 200 OK with updated status
     */
    @Test
    public void testUpdateOrderStatusSuccess() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("delivered"));
    }

    /**
     * Test: Update to PENDING status
     * Expected: 200 OK with status field
     */
    @Test
    public void testUpdateOrderStatusPending() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "pending");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("pending"));
    }

    /**
     * Test: Update to IN PROGRESS status
     * Expected: 200 OK with status field
     */
    @Test
    public void testUpdateOrderStatusInProgress() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "in progress");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("in progress"));
    }

    /**
     * Test: Update to CANCELLED status
     * Expected: 200 OK with status field
     */
    @Test
    public void testUpdateOrderStatusCancelled() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "cancelled");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("cancelled"));
    }

    // ==================== REQUEST BODY VALIDATION TESTS ====================

    /**
     * Test: POST with missing status field in request body
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusMissingStatus() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        // No status field

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST with empty status string
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusEmptyStatus() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST with null status value
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusNullStatus() throws Exception {
        String requestBody = "{\"status\": null}";

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST with invalid JSON body
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusInvalidJson() throws Exception {
        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    // ==================== ORDER ID VALIDATION TESTS ====================

    /**
     * Test: POST with non-existent order ID
     * Expected: 404 Not Found
     */
    @Test
    public void testUpdateOrderStatusNotFound() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/99999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: POST with invalid ID format (non-integer)
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusInvalidId() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/invalid/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST with negative order ID
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusNegativeId() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/-1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST with zero order ID
     * Expected: 400 Bad Request
     */
    @Test
    public void testUpdateOrderStatusZeroId() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/0/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    // ==================== RESPONSE FORMAT TESTS ====================

    /**
     * Test: Verify response has status field
     * Expected: Response includes status field with updated value
     */
    @Test
    public void testUpdateOrderStatusResponseFormat() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.status").isString());
    }

    /**
     * Test: Verify status value matches request
     * Expected: Response status equals request status
     */
    @Test
    public void testUpdateOrderStatusValueMatchesRequest() throws Exception {
        String statusValue = "in progress";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", statusValue);

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(statusValue));
    }

    // ==================== DATABASE VERIFICATION TESTS ====================

    /**
     * Test: Verify status updated in database
     * Expected: Order in database has updated status
     */
    @Test
    public void testUpdateOrderStatusDatabaseVerification() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // Verify in database
                    Order dbOrder = orderRepository.findById(testOrder1.getId()).orElse(null);
                    assertNotNull(dbOrder, "Order should exist in database");
                    assertNotNull(dbOrder.getOrderStatus(), "Order status should not be null");
                    assertTrue("delivered".equalsIgnoreCase(dbOrder.getOrderStatus().getName()),
                            "Order status should be DELIVERED in database");
                });
    }

    /**
     * Test: Multiple status updates in sequence
     * Expected: Each update succeeds and updates the database
     */
    @Test
    public void testUpdateOrderStatusMultipleUpdates() throws Exception {
        // First update to IN PROGRESS
        Map<String, String> requestBody1 = new HashMap<>();
        requestBody1.put("status", "in progress");
        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("in progress"));

        // Second update to DELIVERED
        Map<String, String> requestBody2 = new HashMap<>();
        requestBody2.put("status", "delivered");
        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("delivered"));

        // Verify final state in database
        Order dbOrder = orderRepository.findById(testOrder1.getId()).orElse(null);
        assertNotNull(dbOrder, "Order should exist");
        assertEquals("delivered", dbOrder.getOrderStatus().getName().toLowerCase(),
                "Final status should be DELIVERED");
    }

    /**
     * Test: Idempotency - updating to same status succeeds
     * Expected: 200 OK, status unchanged but operation succeeds
     */
    @Test
    public void testUpdateOrderStatusIdempotency() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "pending");

        // First update (from PENDING to PENDING - should succeed)
        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("pending"));

        // Second update to same status (should still succeed)
        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("pending"));
    }

    /**
     * Test: Different orders maintain separate status
     * Expected: Updating order 1 doesn't affect order 2
     */
    @Test
    public void testUpdateOrderStatusIsolation() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        // Update order 1
        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        // Verify order 1 updated
        Order order1 = orderRepository.findById(testOrder1.getId()).orElse(null);
        assertEquals("delivered", order1.getOrderStatus().getName().toLowerCase(),
                "Order 1 status should be DELIVERED");

        // Verify order 2 unchanged
        Order order2 = orderRepository.findById(testOrder2.getId()).orElse(null);
        assertEquals("pending", order2.getOrderStatus().getName().toLowerCase(),
                "Order 2 status should still be PENDING");
    }

    // ==================== EDGE CASE TESTS ====================

    /**
     * Test: Update with custom status string
     * Expected: 200 OK, status accepted as-is
     */
    @Test
    public void testUpdateOrderStatusCustomValue() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "out for delivery");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("out for delivery"));
    }

    /**
     * Test: Update with status containing spaces
     * Expected: 200 OK, status preserved with spaces
     */
    @Test
    public void testUpdateOrderStatusWithSpaces() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "ready for pickup");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ready for pickup"));
    }

    /**
     * Test: Update with status having uppercase letters
     * Expected: 200 OK, status case preserved
     */
    @Test
    public void testUpdateOrderStatusMixedCase() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "DELIVERED");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    /**
     * Test: Update with very long order ID
     * Expected: 404 Not Found (ID doesn't exist)
     */
    @Test
    public void testUpdateOrderStatusLargeId() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/" + Integer.MAX_VALUE + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: Verify response is JSON object (not array or string)
     * Expected: Response is object with status field
     */
    @Test
    public void testUpdateOrderStatusResponseIsObject() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "delivered");

        mockMvc.perform(post("/api/order/" + testOrder1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(isA(java.util.List.class)))) // Not a list
                .andExpect(jsonPath("$.data.status").exists()); // Has status field
    }
}
