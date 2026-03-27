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
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        // todo: Write SQL query here
    """)
    void saveAddress(String streetAddress, String city, String postalCode);
    
    @Query(nativeQuery = true, value = """
        SELECT LAST_INSERT_ID() AS id
    """)
    int getLastInsertedId();

}
