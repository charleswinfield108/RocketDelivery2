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
    /**
     * Finds a user by ID using native SQL.
     * 
     * @param id the user ID to find
     * @return Optional containing the user if found, empty otherwise
     */
    @Query(nativeQuery = true, value = """
        SELECT id, email, password, name FROM users WHERE id = ?1
    """)
    Optional<UserEntity> findById(int id);
    
    // Custom query method 
    List<UserEntity> findAllByOrderByIdDesc();
    Optional<UserEntity> findByEmail(String email);
}
