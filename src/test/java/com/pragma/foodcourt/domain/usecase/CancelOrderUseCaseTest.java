package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.OrderNotCancellableException;
import com.pragma.foodcourt.domain.exception.OrderNotFoundException;
import com.pragma.foodcourt.domain.exception.OrderNotFromClientException;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderItem;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderTraceabilityPort;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID DISH_ID = UUID.randomUUID();
    private static final UUID ORDER_ID = UUID.randomUUID();

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IOrderTraceabilityPort orderTraceabilityPort;

    private CancelOrderUseCase cancelOrderUseCase;

    @BeforeEach
    void setUp() {
        cancelOrderUseCase = new CancelOrderUseCase(orderPersistencePort, new OrderValidator(),
                orderTraceabilityPort);
    }

    private Order orderOwnedBy(UUID clientId, OrderStatus status) {
        return new Order(ORDER_ID, clientId, RESTAURANT_ID, status, null,
                new ArrayList<>(List.of(new OrderItem(DISH_ID, 1))));
    }

    @Test
    @DisplayName("Cancels a pending order owned by the requesting client")
    void shouldCancelPendingOrderOwnedByClient() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderOwnedBy(CLIENT_ID, OrderStatus.PENDING));
        when(orderPersistencePort.saveOrder(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = cancelOrderUseCase.cancelOrder(ORDER_ID, CLIENT_ID);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).saveOrder(captor.capture());
        assertEquals(OrderStatus.CANCELLED, captor.getValue().getStatus());
        assertSame(captor.getValue(), result);
        verify(orderTraceabilityPort).recordStatusChange(
                eq(ORDER_ID), eq(CLIENT_ID), eq(OrderStatus.PENDING), eq(OrderStatus.CANCELLED), any());
    }

    @Test
    @DisplayName("Rejects cancelling an order that does not exist")
    void shouldThrowWhenOrderToCancelNotFound() {
        when(orderPersistencePort.findOrderById(ORDER_ID)).thenReturn(null);

        assertThrows(OrderNotFoundException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects cancelling an order that belongs to another client")
    void shouldThrowWhenOrderBelongsToAnotherClient() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderOwnedBy(UUID.randomUUID(), OrderStatus.PENDING));

        assertThrows(OrderNotFromClientException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects cancelling an order that is not in PENDING status")
    void shouldThrowWhenOrderIsNotPending() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderOwnedBy(CLIENT_ID, OrderStatus.IN_PREPARATION));

        assertThrows(OrderNotCancellableException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }
}
