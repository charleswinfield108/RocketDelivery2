package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class AddressService {
    
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository){
        this.addressRepository = addressRepository;
    }

    public Optional<Address> findById(int id){
        return addressRepository.findById(id);
    }

    public Optional<Integer> findLastAddressId() {
        List<Address> addresses = addressRepository.findAllByOrderByIdDesc();
        if (!addresses.isEmpty()) {
            return Optional.of(addresses.get(0).getId());
        } else {
            return Optional.empty();
        }
    }
    
    public Address saveAddress(Address address){
        return addressRepository.save(address);
    }

    @Transactional
    public int saveAddress(String streetAddress, String city, String postalCode) {
        try {
            addressRepository.saveAddress(streetAddress, city, postalCode);
            return addressRepository.getLastInsertedId();
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Create a new address with the provided details
     * 
     * ✅ Acceptance Criteria:
     * - All fields persisted to database
     * - Service layer pattern used
     * - Parameterized SQL queries (no concatenation)
     * 
     * Uses Spring Data JPA save method which leverages Hibernate ORM to generate
     * parameterized SQL statements, providing protection against SQL injection.
     * 
     * @param streetAddress The street address (required, non-empty, validated by controller)
     * @param city The city (required, non-empty, validated by controller)
     * @param postalCode The postal code (required, non-empty, validated by controller)
     * @return The created Address object with auto-generated ID from database
     * @throws org.springframework.dao.DataAccessException if database operation fails
     */
    @Transactional
    public Address createAddress(String streetAddress, String city, String postalCode) {
        // Build Address entity
        Address address = Address.builder()
                .streetAddress(streetAddress)
                .city(city)
                .postalCode(postalCode)
                .build();

        // Save to database using repository (parameterized queries)
        return addressRepository.save(address);
    }


    public void delete(int id) {
        addressRepository.deleteById(id);
    }
}