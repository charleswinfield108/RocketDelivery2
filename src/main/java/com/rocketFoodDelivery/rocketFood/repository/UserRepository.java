package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository <UserEntity, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()
    @Query(nativeQuery = true, value = """
        // todo: Write SQL query here
    """)
    Optional<UserEntity> findById(int id);
    
    // Custom query method 
    List<UserEntity> findAllByOrderByIdDesc();
    Optional<UserEntity> findByEmail(String email);
}
