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

/**
 * Repository for Order entity providing database access and persistence operations.
 * 
 * Includes native SQL queries for:
 * - Filtering orders by restaurant, customer, or courier
 * - Cascade deleting orders with related ProductOrder records
 * 
 * All queries use parameterized bindings (?1, ?2, etc.) to prevent SQL injection.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // Legacy finder methods (kept for backward compatibility)
    List<Order> findByCustomerId(int id);
    List<Order> findByRestaurantId(int id);
    List<Order> findByCourierId(int id);

    /**
     * Retrieves all orders for a specific restaurant using native SQL.
     * 
     * SELECT query filters orders by restaurant_id, returning all relevant fields
     * needed for the ApiOrderDTO construction. Uses parameterized binding (?1)
     * to prevent SQL injection attacks.
     * 
     * @param restaurantId the restaurant ID to filter by
     * @return list of Order objects for the specified restaurant
     */
    @Query(nativeQuery = true, value = """
        SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
        FROM orders o
        WHERE o.restaurant_id = ?1
    """)
    List<Order> findOrdersByRestaurantId(@Param("restaurantId") int restaurantId);

    /**
     * Retrieves all orders placed by a specific customer using native SQL.
     * 
     * SELECT query filters orders by customer_id, returning all relevant fields
     * for ApiOrderDTO construction. Uses parameterized binding (?1) for safety.
     * 
     * @param customerId the customer ID to filter by
     * @return list of Order objects for the specified customer
     */
    @Query(nativeQuery = true, value = """
        SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
        FROM orders o
        WHERE o.customer_id = ?1
    """)
    List<Order> findOrdersByCustomerId(@Param("customerId") int customerId);

    /**
     * Retrieves all orders assigned to a specific courier using native SQL.
     * 
     * SELECT query filters orders by courier_id, returning all relevant fields
     * for ApiOrderDTO construction. Uses parameterized binding (?1) for security.
     * 
     * @param courierId the courier ID to filter by
     * @return list of Order objects assigned to the specified courier
     */
    @Query(nativeQuery = true, value = """
        SELECT o.id, o.restaurant_id, o.customer_id, o.courier_id, o.status_id, o.restaurant_rating
        FROM orders o
        WHERE o.courier_id = ?1
    """)
    List<Order> findOrdersByCourierId(@Param("courierId") int courierId);

    /**
     * Deletes all ProductOrder entries associated with a specific order.
     * 
     * This DELETE operation cascades the deletion by removing all product items
     * from a specific order. Must be called before deleting the Order itself
     * to maintain referential integrity. Uses parameterized binding (?1).
     * 
     * Transaction handling ensures atomicity of the delete operation.
     * 
     * @param orderId the order ID whose ProductOrder entries should be deleted
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM product_orders WHERE order_id = ?1
    """)
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    /**
     * Deletes an order by ID using native SQL.
     * 
     * DELETE query removes the order record. Should only be called after
     * deleting its associated ProductOrder entries via deleteProductOrdersByOrderId().
     * Uses parameterized binding (?1) to prevent SQL injection.
     * 
     * Note: JpaRepository.deleteById() is typically preferred and is used
     * by the service layer instead of this method.
     * 
     * @param orderId the order ID to delete
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """  
        DELETE FROM orders WHERE id = ?1
    """)
    void deleteOrderById(@Param("orderId") int orderId);
}
