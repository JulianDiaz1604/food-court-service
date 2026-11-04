package com.pragma.foodcourt.infrastructure.out.jpa.repository;

import com.pragma.foodcourt.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IDishRepository extends JpaRepository<DishEntity, UUID> {

    Page<DishEntity> findByRestaurantIdAndActiveTrue(UUID restaurantId, Pageable pageable);

    Page<DishEntity> findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
            UUID restaurantId, String category, Pageable pageable);
}
