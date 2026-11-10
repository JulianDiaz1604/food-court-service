package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IOrderStatusServicePort;
import com.pragma.foodcourt.domain.exception.InvalidDeliveryPinException;
import com.pragma.foodcourt.domain.exception.OrderNotFoundException;
import com.pragma.foodcourt.domain.service.EmployeeRestaurantResolver;
import com.pragma.foodcourt.domain.exception.OrderNotFromEmployeeRestaurantException;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.UserContact;
import com.pragma.foodcourt.domain.spi.IOrderDeliveryPinPort;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderReadyNotificationPort;
import com.pragma.foodcourt.domain.spi.IUserClientPort;
import com.pragma.foodcourt.domain.validation.OrderValidator;

import java.util.UUID;

public class OrderStatusUseCase implements IOrderStatusServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final OrderValidator orderValidator;
    private final IUserClientPort userClientPort;
    private final IOrderReadyNotificationPort orderReadyNotificationPort;
    private final IOrderDeliveryPinPort orderDeliveryPinPort;
    private final EmployeeRestaurantResolver employeeRestaurantResolver;

    public OrderStatusUseCase(IOrderPersistencePort orderPersistencePort,
                              OrderValidator orderValidator,
                              IUserClientPort userClientPort,
                              IOrderReadyNotificationPort orderReadyNotificationPort,
                              IOrderDeliveryPinPort orderDeliveryPinPort,
                              EmployeeRestaurantResolver employeeRestaurantResolver) {
        this.orderPersistencePort = orderPersistencePort;
        this.orderValidator = orderValidator;
        this.userClientPort = userClientPort;
        this.orderReadyNotificationPort = orderReadyNotificationPort;
        this.orderDeliveryPinPort = orderDeliveryPinPort;
        this.employeeRestaurantResolver = employeeRestaurantResolver;
    }

    @Override
    public Order assignOrder(UUID orderId, UUID employeeId) {
        Order order = loadOrderForEmployee(orderId, employeeId);
        orderValidator.validateAssignable(order);
        order.setAssignedEmployeeId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);
        return orderPersistencePort.saveOrder(order);
    }

    /**
     * Marks an {@code IN_PREPARATION} order as {@code READY} and notifies the client by SMS.
     *
     * <p>Saga ordering (notification before persist): if the SMS delivery fails the order stays in
     * {@code IN_PREPARATION} and the caller can retry; if the persist fails after a successful
     * notification the order also stays in {@code IN_PREPARATION}. In that rare edge case the
     * messaging-service's duplicate-notification guard handles a subsequent retry.</p>
     */
    @Override
    public Order markOrderReady(UUID orderId, UUID employeeId) {
        Order order = loadOrderForEmployee(orderId, employeeId);
        orderValidator.validateReadyTransition(order);
        UserContact contact = userClientPort.getUserContactById(order.getClientId());
        orderReadyNotificationPort.notifyOrderReady(order.getId(), contact.getPhone(), contact.getName());
        order.setStatus(OrderStatus.READY);
        return orderPersistencePort.saveOrder(order);
    }

    /**
     * Marks a {@code READY} order as {@code DELIVERED} after the employee digits the correct security
     * PIN (HU15). The PIN is validated against messaging-service (criterion 3); only orders in
     * {@code READY} may transition (criterion 1). {@code DELIVERED} is terminal: no other transition
     * uses it as a source state (criterion 2).
     */
    @Override
    public Order markOrderDelivered(UUID orderId, UUID employeeId, String pin) {
        Order order = loadOrderForEmployee(orderId, employeeId);
        orderValidator.validateDeliverTransition(order);
        orderValidator.validateDeliveryPin(pin);
        if (!orderDeliveryPinPort.isPinValid(orderId, pin)) {
            throw new InvalidDeliveryPinException();
        }
        order.setStatus(OrderStatus.DELIVERED);
        return orderPersistencePort.saveOrder(order);
    }

    private Order loadOrderForEmployee(UUID orderId, UUID employeeId) {
        Order order = orderPersistencePort.findOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        UUID employeeRestaurantId = employeeRestaurantResolver.resolveFor(employeeId);
        if (!employeeRestaurantId.equals(order.getRestaurantId())) {
            throw new OrderNotFromEmployeeRestaurantException();
        }
        return order;
    }
}
