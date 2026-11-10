package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.EmptyOrderException;
import com.pragma.foodcourt.domain.exception.InvalidOrderStatusException;
import com.pragma.foodcourt.domain.exception.InvalidQuantityException;
import com.pragma.foodcourt.domain.exception.OrderNotAssignableException;
import com.pragma.foodcourt.domain.exception.OrderNotInPreparationException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderItem;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.util.DomainConstants;

import java.util.List;
import java.util.Locale;

public class OrderValidator {

    public OrderStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidOrderStatusException();
        }
        try {
            return OrderStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException invalidStatus) {
            throw new InvalidOrderStatusException();
        }
    }

    public void validateAssignable(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderNotAssignableException();
        }
    }

    public void validateReadyTransition(Order order) {
        if (order.getStatus() != OrderStatus.IN_PREPARATION) {
            throw new OrderNotInPreparationException();
        }
    }

    public void validateNewOrder(Order order) {
        if (order.getRestaurantId() == null) {
            throw new RequiredFieldException();
        }
        List<OrderItem> items = order.getItems();
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderException();
        }
        items.forEach(this::validateItem);
    }

    private void validateItem(OrderItem item) {
        if (item == null || item.getDishId() == null || item.getQuantity() == null) {
            throw new RequiredFieldException();
        }
        if (item.getQuantity() <= DomainConstants.ORDER_QUANTITY_MINIMUM) {
            throw new InvalidQuantityException();
        }
    }
}
