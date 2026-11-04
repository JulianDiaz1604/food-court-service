package com.pragma.foodcourt.infrastructure.out.jpa.repository;

import com.pragma.foodcourt.infrastructure.out.jpa.entity.EmployeeRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IEmployeeRestaurantRepository extends JpaRepository<EmployeeRestaurantEntity, UUID> {

    Optional<EmployeeRestaurantEntity> findByEmployeeId(UUID employeeId);
}
