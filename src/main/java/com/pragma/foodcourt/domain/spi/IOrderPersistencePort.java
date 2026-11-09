package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.PagedResult;

import java.util.UUID;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);

    Order findOrderById(UUID orderId);

    boolean existsActiveOrderByClient(UUID clientId);

    PagedResult<Order> findOrdersByRestaurantAndStatus(
            UUID restaurantId, OrderStatus status, int page, int size);
}
