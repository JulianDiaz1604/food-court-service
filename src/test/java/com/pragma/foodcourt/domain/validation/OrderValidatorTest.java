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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderValidatorTest {

    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID DISH_ID = UUID.randomUUID();

    private OrderValidator orderValidator;

    @BeforeEach
    void setUp() {
        orderValidator = new OrderValidator();
    }

    private Order orderWith(List<OrderItem> items) {
        return new Order(null, null, RESTAURANT_ID, null, null, items);
    }

    @Test
    @DisplayName("Accepts an order with a restaurant and at least one valid item")
    void shouldAcceptValidOrder() {
        Order order = orderWith(List.of(new OrderItem(DISH_ID, 2)));

        assertDoesNotThrow(() -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Rejects an order without restaurant id")
    void shouldThrowWhenRestaurantIdIsMissing() {
        Order order = new Order(null, null, null, null, null, List.of(new OrderItem(DISH_ID, 1)));

        assertThrows(RequiredFieldException.class, () -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Rejects an order with a null item list")
    void shouldThrowWhenItemsAreNull() {
        Order order = orderWith(null);

        assertThrows(EmptyOrderException.class, () -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Rejects an order with an empty item list")
    void shouldThrowWhenItemsAreEmpty() {
        Order order = orderWith(new ArrayList<>());

        assertThrows(EmptyOrderException.class, () -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Rejects an item without a dish id")
    void shouldThrowWhenItemHasNoDishId() {
        Order order = orderWith(List.of(new OrderItem(null, 1)));

        assertThrows(RequiredFieldException.class, () -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Rejects an item without a quantity")
    void shouldThrowWhenItemHasNoQuantity() {
        Order order = orderWith(List.of(new OrderItem(DISH_ID, null)));

        assertThrows(RequiredFieldException.class, () -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Rejects an item whose quantity is zero or negative")
    void shouldThrowWhenQuantityIsNotPositive() {
        Order order = orderWith(List.of(new OrderItem(DISH_ID, 0)));

        assertThrows(InvalidQuantityException.class, () -> orderValidator.validateNewOrder(order));
    }

    @Test
    @DisplayName("Parses a valid status name ignoring case and surrounding spaces")
    void shouldParseValidStatusIgnoringCaseAndSpaces() {
        assertEquals(OrderStatus.IN_PREPARATION, orderValidator.parseStatus("  in_preparation  "));
    }

    @Test
    @DisplayName("Rejects a null or blank status")
    void shouldThrowWhenStatusIsBlank() {
        assertThrows(InvalidOrderStatusException.class, () -> orderValidator.parseStatus("   "));
        assertThrows(InvalidOrderStatusException.class, () -> orderValidator.parseStatus(null));
    }

    @Test
    @DisplayName("Rejects a status that is not part of the lifecycle")
    void shouldThrowWhenStatusIsUnknown() {
        assertThrows(InvalidOrderStatusException.class, () -> orderValidator.parseStatus("SHIPPED"));
    }

    @Test
    @DisplayName("Accepts assigning an order that is in PENDING status")
    void shouldAcceptAssignableWhenOrderIsPending() {
        Order order = new Order(null, null, RESTAURANT_ID, OrderStatus.PENDING, null,
                List.of(new OrderItem(DISH_ID, 1)));

        assertDoesNotThrow(() -> orderValidator.validateAssignable(order));
    }

    @Test
    @DisplayName("Rejects assigning an order that is not in PENDING status")
    void shouldThrowWhenAssigningNonPendingOrder() {
        Order order = new Order(null, null, RESTAURANT_ID, OrderStatus.IN_PREPARATION, null,
                List.of(new OrderItem(DISH_ID, 1)));

        assertThrows(OrderNotAssignableException.class,
                () -> orderValidator.validateAssignable(order));
    }

    @Test
    @DisplayName("Accepts the ready transition when the order is in IN_PREPARATION status")
    void shouldAcceptReadyTransitionWhenOrderIsInPreparation() {
        Order order = new Order(null, null, RESTAURANT_ID, OrderStatus.IN_PREPARATION, null,
                List.of(new OrderItem(DISH_ID, 1)));

        assertDoesNotThrow(() -> orderValidator.validateReadyTransition(order));
    }

    @Test
    @DisplayName("Rejects the ready transition when the order is not in IN_PREPARATION status")
    void shouldThrowWhenReadyTransitionOnNonPreparationOrder() {
        Order order = new Order(null, null, RESTAURANT_ID, OrderStatus.PENDING, null,
                List.of(new OrderItem(DISH_ID, 1)));

        assertThrows(OrderNotInPreparationException.class,
                () -> orderValidator.validateReadyTransition(order));
    }
}
