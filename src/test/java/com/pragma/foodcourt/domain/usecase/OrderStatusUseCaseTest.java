package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.ClientNotFoundException;
import com.pragma.foodcourt.domain.service.EmployeeRestaurantResolver;
import com.pragma.foodcourt.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.foodcourt.domain.exception.MessagingServiceUnavailableException;
import com.pragma.foodcourt.domain.exception.OrderNotAssignableException;
import com.pragma.foodcourt.domain.exception.OrderNotFoundException;
import com.pragma.foodcourt.domain.exception.OrderNotFromEmployeeRestaurantException;
import com.pragma.foodcourt.domain.exception.OrderNotInPreparationException;
import com.pragma.foodcourt.domain.exception.UserServiceUnavailableException;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderItem;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.UserContact;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderReadyNotificationPort;
import com.pragma.foodcourt.domain.spi.IUserClientPort;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusUseCaseTest {

    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID DISH_ID = UUID.randomUUID();
    private static final UUID EMPLOYEE_ID = UUID.randomUUID();
    private static final UUID ORDER_ID = UUID.randomUUID();

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IUserClientPort userClientPort;

    @Mock
    private IOrderReadyNotificationPort orderReadyNotificationPort;

    @Mock
    private EmployeeRestaurantResolver employeeRestaurantResolver;

    private OrderStatusUseCase orderStatusUseCase;

    @BeforeEach
    void setUp() {
        orderStatusUseCase = new OrderStatusUseCase(orderPersistencePort, new OrderValidator(),
                userClientPort, orderReadyNotificationPort, employeeRestaurantResolver);
    }

    private Order orderInStatus(UUID restaurantId, OrderStatus status) {
        return new Order(ORDER_ID, CLIENT_ID, restaurantId, status, null,
                new ArrayList<>(List.of(new OrderItem(DISH_ID, 1))));
    }

    // ---- assignOrder ----

    @Test
    @DisplayName("Assigns the employee to a pending order and moves it to IN_PREPARATION")
    void shouldAssignEmployeeAndMoveOrderToInPreparation() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(RESTAURANT_ID, OrderStatus.PENDING));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(orderPersistencePort.saveOrder(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderStatusUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).saveOrder(captor.capture());
        Order saved = captor.getValue();
        assertEquals(OrderStatus.IN_PREPARATION, saved.getStatus());
        assertEquals(EMPLOYEE_ID, saved.getAssignedEmployeeId());
        assertSame(saved, result);
    }

    @Test
    @DisplayName("Rejects assigning an order that does not exist")
    void shouldThrowWhenOrderToAssignNotFound() {
        when(orderPersistencePort.findOrderById(ORDER_ID)).thenReturn(null);

        assertThrows(OrderNotFoundException.class,
                () -> orderStatusUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects assigning an order when the employee is not assigned to any restaurant")
    void shouldThrowWhenAssigningEmployeeHasNoRestaurant() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(RESTAURANT_ID, OrderStatus.PENDING));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID))
                .thenThrow(new EmployeeNotLinkedToRestaurantException());

        assertThrows(EmployeeNotLinkedToRestaurantException.class,
                () -> orderStatusUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects assigning an order that belongs to another restaurant")
    void shouldThrowWhenOrderBelongsToAnotherRestaurant() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(UUID.randomUUID(), OrderStatus.PENDING));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);

        assertThrows(OrderNotFromEmployeeRestaurantException.class,
                () -> orderStatusUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects assigning an order that is not in PENDING status")
    void shouldThrowWhenOrderIsNotPending() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(RESTAURANT_ID, OrderStatus.IN_PREPARATION));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);

        assertThrows(OrderNotAssignableException.class,
                () -> orderStatusUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    // ---- markOrderReady ----

    @Test
    @DisplayName("Notifies the client and persists READY status when order is IN_PREPARATION")
    void shouldMarkOrderReadyAndNotifyClientWhenOrderIsInPreparation() {
        Order order = orderInStatus(RESTAURANT_ID, OrderStatus.IN_PREPARATION);
        UserContact contact = new UserContact("+573005698325", "Juan Perez");
        when(orderPersistencePort.findOrderById(ORDER_ID)).thenReturn(order);
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(userClientPort.getUserContactById(CLIENT_ID)).thenReturn(contact);
        when(orderPersistencePort.saveOrder(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderReadyNotificationPort).notifyOrderReady(ORDER_ID, "+573005698325", "Juan Perez");
        verify(orderPersistencePort).saveOrder(captor.capture());
        assertEquals(OrderStatus.READY, captor.getValue().getStatus());
        assertEquals(OrderStatus.READY, result.getStatus());
    }

    @Test
    @DisplayName("Rejects marking an order as ready when the order does not exist")
    void shouldThrowOrderNotFoundWhenMarkingNonExistentOrderAsReady() {
        when(orderPersistencePort.findOrderById(ORDER_ID)).thenReturn(null);

        assertThrows(OrderNotFoundException.class,
                () -> orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects marking an order as ready when the order belongs to another restaurant")
    void shouldThrowWhenReadyOrderBelongsToAnotherRestaurant() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(UUID.randomUUID(), OrderStatus.IN_PREPARATION));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);

        assertThrows(OrderNotFromEmployeeRestaurantException.class,
                () -> orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects marking an order as ready when it is not in IN_PREPARATION status")
    void shouldThrowWhenOrderIsNotInPreparation() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(RESTAURANT_ID, OrderStatus.PENDING));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);

        assertThrows(OrderNotInPreparationException.class,
                () -> orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Propagates UserServiceUnavailableException without persisting READY when users-service is down")
    void shouldThrowUserServiceUnavailableAndNotPersistReadyWhenUsersServiceFails() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(RESTAURANT_ID, OrderStatus.IN_PREPARATION));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(userClientPort.getUserContactById(CLIENT_ID))
                .thenThrow(new UserServiceUnavailableException());

        assertThrows(UserServiceUnavailableException.class,
                () -> orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Propagates MessagingServiceUnavailableException without persisting READY when messaging-service fails")
    void shouldThrowMessagingServiceUnavailableAndNotPersistReadyWhenMessagingFails() {
        Order order = orderInStatus(RESTAURANT_ID, OrderStatus.IN_PREPARATION);
        UserContact contact = new UserContact("+573005698325", "Juan Perez");
        when(orderPersistencePort.findOrderById(ORDER_ID)).thenReturn(order);
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(userClientPort.getUserContactById(CLIENT_ID)).thenReturn(contact);
        doThrow(new MessagingServiceUnavailableException())
                .when(orderReadyNotificationPort)
                .notifyOrderReady(any(), any(), any());

        assertThrows(MessagingServiceUnavailableException.class,
                () -> orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Propagates ClientNotFoundException when users-service reports 404 for the client")
    void shouldThrowClientNotFoundWhenUsersServiceReturns404() {
        when(orderPersistencePort.findOrderById(ORDER_ID))
                .thenReturn(orderInStatus(RESTAURANT_ID, OrderStatus.IN_PREPARATION));
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(userClientPort.getUserContactById(CLIENT_ID))
                .thenThrow(new ClientNotFoundException());

        assertThrows(ClientNotFoundException.class,
                () -> orderStatusUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }
}
