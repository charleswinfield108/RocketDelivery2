package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()

    // Custom query method 
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        // todo: Write SQL query here
    """)
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    List<ProductOrder> findByOrderId(int id);
    List<ProductOrder> findByProductId(int id);

    @Override
    void deleteById(@NonNull Integer productOrderId);
}
