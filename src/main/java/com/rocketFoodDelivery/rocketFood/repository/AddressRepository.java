package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    // CRUD operation available using JPA : findAll(), findById(), save(), deleteById()

    Optional<Address> findById(int id);

    List<Address> findAllByOrderByIdDesc();
    
    /**
     * Saves a new address with native SQL INSERT.
     * 
     * @param streetAddress the street address
     * @param city the city
     * @param postalCode the postal code
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO addresses (street_address, city, postal_code)
        VALUES (?1, ?2, ?3)
    """)
    void saveAddress(String streetAddress, String city, String postalCode);
    
    @Query(nativeQuery = true, value = """
        SELECT LAST_INSERT_ID() AS id
    """)
    int getLastInsertedId();

}
