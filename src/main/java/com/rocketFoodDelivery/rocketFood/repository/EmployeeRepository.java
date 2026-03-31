package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Already CRUD operation available : findAll(), findById(), save(), deleteById()

    // Custom query method 

    Optional<Employee> findByUserEntityId( int id);
    
    @Override
    void deleteById(@NonNull Integer employeeId);
}
