package com.pragma.foodcourt.infrastructure.out.jpa.adapter;

import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private static final List<OrderStatus> ACTIVE_STATUSES =
            List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY);

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public Order saveOrder(Order order) {
        OrderEntity savedEntity = orderRepository.save(orderEntityMapper.toEntity(order));
        return orderEntityMapper.toOrder(savedEntity);
    }

    @Override
    public boolean existsActiveOrderByClient(UUID clientId) {
        return orderRepository.existsByClientIdAndStatusIn(clientId, ACTIVE_STATUSES);
    }
}
