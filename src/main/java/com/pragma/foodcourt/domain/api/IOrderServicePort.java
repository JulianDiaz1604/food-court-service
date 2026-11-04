package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Order;

import java.util.UUID;

public interface IOrderServicePort {
    Order placeOrder(Order order, UUID clientId);
}
