package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()

    // Custom query method 
    Optional<Restaurant> findByUserEntityId(int id);


    /**
     * Finds a restaurant by its ID along with the calculated average rating rounded up to the ceiling.
     *
     * @param restaurantId The ID of the restaurant to retrieve.
     * @return A list of Object arrays representing the selected columns from the query result.
     *         Each Object array corresponds to the restaurant's information.
     *         An empty list is returned if no restaurant is found with the specified ID.
     */
    @Query(nativeQuery = true, value = """
        SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
        FROM restaurants r
        LEFT JOIN orders o ON r.id = o.restaurant_id
        WHERE r.id = :restaurantId
        GROUP BY r.id
    """)
    List<Object[]> findRestaurantWithAverageRatingById(@Param("restaurantId") int restaurantId);
    
    /**
     * Finds restaurants based on the provided rating and price range.
     *
     * Executes a native SQL query that retrieves restaurants with their information, including a calculated
     * average rating rounded up to the ceiling.
     *
     * @param rating     The minimum rounded-up average rating of the restaurants. (Optional)
     * @param priceRange The price range of the restaurants. (Optional)
     * @return A list of Object arrays representing the selected columns from the query result.
     *         Each Object array corresponds to a restaurant's information.
     *         An empty list is returned if no restaurant is found with the specified ID.
     */
    @Query(nativeQuery = true, value = """
        SELECT * FROM (
        SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
        FROM restaurants r
        LEFT JOIN orders o ON r.id = o.restaurant_id
        WHERE (:priceRange IS NULL OR r.price_range = :priceRange)
        GROUP BY r.id
        ) AS result
        WHERE (:rating IS NULL OR result.rating = :rating)
    """)
    List<Object[]> findRestaurantsByRatingAndPriceRange(@Param("rating") Integer rating, @Param("priceRange") Integer priceRange);


    /**
     * Saves a new restaurant with the provided details using native SQL INSERT.
     * 
     * @param userId the user ID who owns the restaurant
     * @param addressId the address ID for the restaurant
     * @param name the restaurant name
     * @param priceRange the price range (1-3)
     * @param phone the phone number
     * @param email the email address
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
        VALUES (?1, ?2, ?3, ?4, ?5, ?6)
    """)
    void saveRestaurant(long userId, long addressId, String name, int priceRange, String phone, String email);


    /**
     * Updates an existing restaurant with new details using native SQL UPDATE.
     * 
     * @param restaurantId the restaurant ID to update
     * @param name the new restaurant name
     * @param priceRange the new price range (1-3)
     * @param phone the new phone number
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        UPDATE restaurants 
        SET name = ?2, price_range = ?3, phone = ?4
        WHERE id = ?1
    """)
    void updateRestaurant(int restaurantId, String name, int priceRange, String phone);


    /**
     * Finds a restaurant by ID using native SQL.
     * 
     * @param restaurantId the restaurant ID to find
     * @return Optional containing the restaurant if found, empty otherwise
     */
    @Query(nativeQuery = true, value = """
        SELECT r.id, r.user_id, r.address_id, r.name, r.price_range, r.phone, r.email
        FROM restaurants r
        WHERE r.id = ?1
    """)
    Optional<Restaurant> findRestaurantById(@Param("restaurantId") int restaurantId);


    @Query(nativeQuery = true, value = """
        SELECT LAST_INSERT_ID() AS id
    """)
    int getLastInsertedId();

    
    /**
     * Deletes a restaurant by ID using native SQL.
     * NOTE: This method should only be called after cascading deletes of related records.
     * 
     * @param restaurantId the restaurant ID to delete
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM restaurants WHERE id = ?1
    """)
    void deleteRestaurantById(@Param("restaurantId") int restaurantId);
}
