package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * 
 * Provides both standard CRUD operations (inherited from JpaRepository):
 * - findAll(), findById(), save(), deleteById()
 * 
 * And custom native SQL operations:
 * - findProductsByRestaurantId() - Retrieve products filtered by restaurant
 * - deleteProductsByRestaurantId() - Cascade delete products by restaurant
 * 
 * All custom queries use parameterized bindings (?1) to prevent SQL injection.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()

    /**
     * Retrieves all products for a given restaurant.
     * Note: Using JPA method name convention for reference.
     * 
     * @param restaurantId the restaurant ID to filter products by
     * @return list of products for the restaurant (empty if none found)
     */
    List<Product> findByRestaurantId(int restaurantId);

    /**
     * Retrieves all products for a given restaurant using native SQL.
     * 
     * Native SQL Query:
     * SELECT p.id, p.restaurant_id, p.name, p.description, p.cost
     * FROM products p
     * WHERE p.restaurant_id = ?1
     * ORDER BY p.id ASC
     * 
     * Uses parameterized binding (?1) to prevent SQL injection.
     * Results are ordered by product ID ascending for consistency.
     * 
     * @param restaurantId the restaurant ID to filter products by (must be > 0)
     * @return list of Product entities for the restaurant, ordered by ID
     *         Empty list if restaurant has no products
     */
    @Query(nativeQuery = true, value = """
        SELECT p.id, p.restaurant_id, p.name, p.description, p.cost
        FROM products p
        WHERE p.restaurant_id = ?1
        ORDER BY p.id ASC
    """)
    List<Product> findProductsByRestaurantId(@Param("restaurantId") int restaurantId);

    /**
     * Deletes all products associated with a restaurant.
     * This is a cascade delete operation used when a restaurant is deleted.
     * 
     * Native SQL Query:
     * DELETE FROM products WHERE restaurant_id = ?1
     * 
     * Uses parameterized binding (?1) to prevent SQL injection.
     * Operation is atomic and transactional - all products deleted or none.
     * 
     * IMPORTANT: This method does NOT delete related ProductOrder entries.
     * Those must be handled separately by ProductService cascade logic.
     * 
     * @param restaurantId the restaurant ID whose products should be deleted (must be > 0)
     * @return count of deleted product rows
     * @throws org.springframework.dao.DataAccessException if database error occurs
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM products WHERE restaurant_id = ?1
    """)
    int deleteProductsByRestaurantId(@Param("restaurantId") int restaurantId);
}
