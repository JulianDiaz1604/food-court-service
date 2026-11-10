package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Order;

import java.util.UUID;

public interface IOrderStatusServicePort {
    Order assignOrder(UUID orderId, UUID employeeId);

    Order markOrderReady(UUID orderId, UUID employeeId);

    Order markOrderDelivered(UUID orderId, UUID employeeId, String pin);
}
