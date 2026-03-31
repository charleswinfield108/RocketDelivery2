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

    /**
     * Deletes all ProductOrder entries for a specific order.
     * Uses native SQL with parameterized binding to prevent SQL injection.
     * 
     * @param orderId the order ID whose product orders should be deleted
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM product_orders WHERE order_id = ?1
    """)
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    /**
     * Deletes all ProductOrder entries associated with a specific restaurant.
     * This cascades the deletion by removing all product items from all orders
     * in the restaurant. Uses parameterized binding (?1) to prevent SQL injection.
     * 
     * @param restaurantId the restaurant ID whose associated product orders should be deleted
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM product_orders WHERE order_id IN (
            SELECT id FROM orders WHERE restaurant_id = ?1
        )
    """)
    void deleteProductOrdersByRestaurant(@Param("restaurantId") int restaurantId);

    List<ProductOrder> findByOrderId(int id);
    List<ProductOrder> findByProductId(int id);

    @Override
    void deleteById(@NonNull Integer productOrderId);
}
