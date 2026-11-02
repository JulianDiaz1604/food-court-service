package com.pragma.foodcourt.infrastructure.out.jpa.repository;

import com.pragma.foodcourt.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IDishRepository extends JpaRepository<DishEntity, UUID> {
}
