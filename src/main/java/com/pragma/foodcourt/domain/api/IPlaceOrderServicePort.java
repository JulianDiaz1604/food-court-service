package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Order;

import java.util.UUID;

public interface IPlaceOrderServicePort {
    Order placeOrder(Order order, UUID clientId);
}
