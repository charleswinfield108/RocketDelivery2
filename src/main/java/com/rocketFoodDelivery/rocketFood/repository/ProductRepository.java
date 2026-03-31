package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()

    // Custom query method 
    List<Product> findByRestaurantId(int restaurantId);

    @Query(nativeQuery = true, value = """
        // todo: Write SQL query here
    """)
    List<Product> findProductsByRestaurantId(@Param("restaurantId") int restaurantId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        // todo: Write SQL query here
    """)
    void deleteProductsByRestaurantId(@Param("restaurantId") int restaurantId);
}
