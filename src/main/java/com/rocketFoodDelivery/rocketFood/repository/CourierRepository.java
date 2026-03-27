package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository <Courier, Integer> {
    // CRUD operation available using JPA : findAll(), findById(), save(), deleteById()

    // Custom query method 
    Optional<Courier> findByUserEntityId(int id);
}
