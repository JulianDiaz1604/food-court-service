package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Order;

import java.util.UUID;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);

    boolean existsActiveOrderByClient(UUID clientId);
}
