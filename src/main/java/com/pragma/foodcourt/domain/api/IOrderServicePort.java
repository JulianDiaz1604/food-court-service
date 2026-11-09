package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.PagedResult;

import java.util.UUID;

public interface IOrderServicePort {
    Order placeOrder(Order order, UUID clientId);

    PagedResult<Order> listOrdersByStatus(UUID employeeId, String status, int page, int size);

    Order assignOrder(UUID orderId, UUID employeeId);
}
