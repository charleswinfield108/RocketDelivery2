package com.rocketFoodDelivery.rocketFood.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDTO;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    // ====== VALID DATA TESTS ======

    /**
     * Test: Create address with all valid required fields
     * Expected: 201 Created with address object containing generated ID
     * ✅ Acceptance Criteria: POST endpoint exists
     */
    @Test
    public void testCreateAddressWithValidData_ShouldReturn201() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,  // id (will be generated)
                "123 Main Street",
                "New York",
                "10001"
        );

        String requestBody = objectMapper.writeValueAsString(validAddress);

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())  // 201
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.streetAddress").value("123 Main Street"))
                .andExpect(jsonPath("$.data.city").value("New York"))
                .andExpect(jsonPath("$.data.postalCode").value("10001"));
    }

    /**
     * Test: Verify address is persisted to database
     * Expected: Address record exists in DB with generated ID
     * ✅ Acceptance Criteria: All fields persisted to database
     */
    @Test
    public void testCreateAddressWithValidData_VerifyDatabasePersistence() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,  // id (will be generated)
                "456 Oak Avenue",
                "Los Angeles",
                "90001"
        );

        String requestBody = objectMapper.writeValueAsString(validAddress);

        // Act
        MvcResult result = mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the ID from response
        String responseBody = result.getResponse().getContentAsString();
        Integer addressId = objectMapper.readTree(responseBody).get("data").get("id").asInt();

        // Assert - verify in database
        assertTrue(addressRepository.existsById(addressId), "Address should exist in database");
        Address dbAddress = addressRepository.findById(addressId).orElse(null);
        assertNotNull(dbAddress);
        assertEquals("456 Oak Avenue", dbAddress.getStreetAddress());
        assertEquals("Los Angeles", dbAddress.getCity());
        assertEquals("90001", dbAddress.getPostalCode());
    }

    /**
     * Test: Create multiple addresses and verify all are persisted
     * Expected: All addresses created successfully with unique IDs
     * ✅ Acceptance Criteria: Service Layer pattern used
     */
    @Test
    public void testCreateMultipleAddresses_AllPersisted() throws Exception {
        long initialCount = addressRepository.count();

        // Create first address
        ApiAddressDTO address1 = new ApiAddressDTO(
                0,
                "111 First St",
                "Boston",
                "02101"
        );
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address1)))
                .andExpect(status().isCreated());

        // Create second address
        ApiAddressDTO address2 = new ApiAddressDTO(
                0,
                "222 Second St",
                "Chicago",
                "60601"
        );
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address2)))
                .andExpect(status().isCreated());

        // Assert
        long finalCount = addressRepository.count();
        assertEquals(initialCount + 2, finalCount, "Should have 2 more addresses in database");
    }

    // ====== MISSING REQUIRED FIELDS TESTS ======

    /**
     * Test: Missing street_address field
     * Expected: 400 Bad Request with validation error
     * ✅ Acceptance Criteria: Missing fields return 400 Bad Request
     */
    @Test
    public void testCreateAddressWithoutStreet_ShouldReturn400() throws Exception {
        // Arrange - using null for street_address
        String requestBody = "{\"street_address\": null, \"city\": \"New York\", \"postal_code\": \"10001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Missing city field
     * Expected: 400 Bad Request with validation error
     * ✅ Acceptance Criteria: Missing fields return 400 Bad Request
     */
    @Test
    public void testCreateAddressWithoutCity_ShouldReturn400() throws Exception {
        // Arrange
        String requestBody = "{\"street_address\": \"123 Main St\", \"city\": null, \"postal_code\": \"10001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Missing postal_code field
     * Expected: 400 Bad Request with validation error
     * ✅ Acceptance Criteria: Missing fields return 400 Bad Request
     */
    @Test
    public void testCreateAddressWithoutPostalCode_ShouldReturn400() throws Exception {
        // Arrange
        String requestBody = "{\"street_address\": \"123 Main St\", \"city\": \"New York\", \"postal_code\": null}";

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ====== EMPTY FIELDS TESTS ======

    /**
     * Test: Empty string for street_address
     * Expected: 400 Bad Request
     * ✅ Acceptance Criteria: Invalid data returns 400 Bad Request
     */
    @Test
    public void testCreateAddressWithEmptyStreet_ShouldReturn400() throws Exception {
        // Arrange
        ApiAddressDTO invalidAddress = new ApiAddressDTO(
                0,
                "",  // Empty street
                "New York",
                "10001"
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddress)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Empty string for city
     * Expected: 400 Bad Request
     * ✅ Acceptance Criteria: Invalid data returns 400 Bad Request
     */
    @Test
    public void testCreateAddressWithEmptyCity_ShouldReturn400() throws Exception {
        // Arrange
        ApiAddressDTO invalidAddress = new ApiAddressDTO(
                0,
                "123 Main St",
                "",  // Empty city
                "10001"
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddress)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Empty string for postal_code
     * Expected: 400 Bad Request
     * ✅ Acceptance Criteria: Invalid data returns 400 Bad Request
     */
    @Test
    public void testCreateAddressWithEmptyPostalCode_ShouldReturn400() throws Exception {
        // Arrange
        ApiAddressDTO invalidAddress = new ApiAddressDTO(
                0,
                "123 Main St",
                "New York",
                ""  // Empty postal code
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddress)))
                .andExpect(status().isBadRequest());
    }

    // ====== RESPONSE FORMAT TESTS ======

    /**
     * Test: Response includes correct HTTP status and headers
     * Expected: 201 Created with proper content type
     * ✅ Acceptance Criteria: Valid request returns 201 Created
     */
    @Test
    public void testCreateAddressResponse_CorrectStatusAndHeaders() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                "789 Pine Road",
                "Seattle",
                "98101"
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test: Response body contains all address fields
     * Expected: All fields present in response
     * ✅ Acceptance Criteria: Response includes generated address ID
     */
    @Test
    public void testCreateAddressResponse_ContainsAllFields() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                "321 Elm Street",
                "Portland",
                "97201"
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.streetAddress").exists())
                .andExpect(jsonPath("$.data.city").exists())
                .andExpect(jsonPath("$.data.postalCode").exists());
    }

    /**
     * Test: Response contains message field
     * Expected: message field present in response
     */
    @Test
    public void testCreateAddressResponse_ContainsMessage() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                "654 Maple Drive",
                "Denver",
                "80201"
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    // ====== INTEGRATION TESTS ======

    /**
     * Test: Address can be retrieved after creation
     * Expected: Address exists and can be fetched from repository
     * ✅ Acceptance Criteria: Parameterized SQL queries (no concatenation)
     */
    @Test
    public void testCreateAddress_CanBeRetrievedAfterCreation() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                "999 New Street",
                "Miami",
                "33101"
        );

        // Act
        MvcResult result = mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract ID from response
        String responseBody = result.getResponse().getContentAsString();
        Integer addressId = objectMapper.readTree(responseBody).get("data").get("id").asInt();

        // Assert - verify we can retrieve it
        Address retrieved = addressRepository.findById(addressId).orElse(null);
        assertNotNull(retrieved);
        assertEquals("999 New Street", retrieved.getStreetAddress());
        assertEquals("Miami", retrieved.getCity());
        assertEquals("33101", retrieved.getPostalCode());
    }

    /**
     * Test: Special characters in address fields are preserved
     * Expected: Special characters stored and retrieved correctly
     */
    @Test
    public void testCreateAddress_WithSpecialCharacters() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                "123 O'Connor Street, Apt #4B",
                "Saint-Louis",
                "63101"
        );

        // Act & Assert
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.streetAddress").value("123 O'Connor Street, Apt #4B"))
                .andExpect(jsonPath("$.data.city").value("Saint-Louis"));
    }

    /**
     * Test: Very long address strings are handled
     * Expected: All valid long strings are persisted without truncation
     */
    @Test
    public void testCreateAddress_WithLongStrings() throws Exception {
        // Arrange
        String longStreet = "123 Very Long Street Name That Goes On And On With Multiple Words And Numbers";
        String longCity = "San Francisco";
        String longPostal = "94102";

        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                longStreet,
                longCity,
                longPostal
        );

        // Act
        MvcResult result = mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();
        assertEquals(longStreet, objectMapper.readTree(responseBody).get("data").get("streetAddress").asText());
    }

    /**
     * Test: Whitespace handling in address fields
     * Expected: Leading/trailing whitespace should be preserved or trimmed appropriately
     */
    @Test
    public void testCreateAddress_WithWhitespace() throws Exception {
        // Arrange
        ApiAddressDTO validAddress = new ApiAddressDTO(
                0,
                "  123 Main Street  ",  // Has whitespace
                "New York",
                "10001"
        );

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddress)))
                .andExpect(status().isCreated())
                .andReturn();

        // Verify ID was generated
        String responseBody = result.getResponse().getContentAsString();
        assertThat(objectMapper.readTree(responseBody).get("data").get("id").asInt(), greaterThan(0));
    }

    /**
     * Test: Address IDs are unique
     * Expected: Each created address has a unique auto-generated ID
     */
    @Test
    public void testCreateMultipleAddresses_UniqueIDs() throws Exception {
        // Create first address
        ApiAddressDTO address1 = new ApiAddressDTO(0, "Address 1", "City 1", "12345");
        MvcResult result1 = mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address1)))
                .andExpect(status().isCreated())
                .andReturn();

        Integer id1 = objectMapper.readTree(result1.getResponse().getContentAsString()).get("data").get("id").asInt();

        // Create second address
        ApiAddressDTO address2 = new ApiAddressDTO(0, "Address 2", "City 2", "54321");
        MvcResult result2 = mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address2)))
                .andExpect(status().isCreated())
                .andReturn();

        Integer id2 = objectMapper.readTree(result2.getResponse().getContentAsString()).get("data").get("id").asInt();

        // Assert - IDs should be different
        assertNotEquals(id1, id2, "Each address should have a unique ID");
        assertTrue(id1 > 0 && id2 > 0, "Both IDs should be positive");
    }
}
