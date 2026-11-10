package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.ICancelOrderServicePort;
import com.pragma.foodcourt.domain.exception.OrderNotFoundException;
import com.pragma.foodcourt.domain.exception.OrderNotFromClientException;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.validation.OrderValidator;

import java.util.UUID;

public class CancelOrderUseCase implements ICancelOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final OrderValidator orderValidator;

    public CancelOrderUseCase(IOrderPersistencePort orderPersistencePort,
                              OrderValidator orderValidator) {
        this.orderPersistencePort = orderPersistencePort;
        this.orderValidator = orderValidator;
    }

    /**
     * Cancels a {@code PENDING} order on behalf of the client who owns it (HU16). Only the owning
     * client may cancel (criterion 1 scope), and only while the order is still {@code PENDING};
     * any other status raises {@link com.pragma.foodcourt.domain.exception.OrderNotCancellableException}
     * (criterion 2).
     */
    @Override
    public Order cancelOrder(UUID orderId, UUID clientId) {
        Order order = orderPersistencePort.findOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        if (!clientId.equals(order.getClientId())) {
            throw new OrderNotFromClientException();
        }
        orderValidator.validateCancellable(order);
        order.setStatus(OrderStatus.CANCELLED);
        return orderPersistencePort.saveOrder(order);
    }
}
