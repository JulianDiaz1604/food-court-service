package com.pragma.foodcourt.infrastructure.out.jpa.repository;

import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface IOrderRepository extends JpaRepository<OrderEntity, UUID> {

    boolean existsByClientIdAndStatusIn(UUID clientId, Collection<OrderStatus> statuses);

    Page<OrderEntity> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);
}
