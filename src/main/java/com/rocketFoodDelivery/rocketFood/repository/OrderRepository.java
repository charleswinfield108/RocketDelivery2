package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()

    // Custom query method 
    List<Order> findByCustomerId(int id);
    List<Order> findByRestaurantId(int id);
    List<Order> findByCourierId(int id);

    @Query(nativeQuery = true, value = """
        SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
        FROM orders o
        WHERE o.restaurant_id = ?1
    """)
    List<Order> findOrdersByRestaurantId(@Param("restaurantId") int restaurantId);

    @Query(nativeQuery = true, value = """
        SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
        FROM orders o
        WHERE o.customer_id = ?1
    """)
    List<Order> findOrdersByCustomerId(@Param("customerId") int customerId);

    @Query(nativeQuery = true, value = """
        SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
        FROM orders o
        WHERE o.courier_id = ?1
    """)
    List<Order> findOrdersByCourierId(@Param("courierId") int courierId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM product_orders WHERE order_id = ?1
    """)
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """  
        DELETE FROM orders WHERE id = ?1
    """)
    void deleteOrderById(@Param("orderId") int orderId);
}
