package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.foodcourt.domain.service.EmployeeRestaurantResolver;
import com.pragma.foodcourt.domain.exception.InvalidOrderStatusException;
import com.pragma.foodcourt.domain.exception.InvalidPaginationException;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderQueryUseCaseTest {

    private static final UUID EMPLOYEE_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private EmployeeRestaurantResolver employeeRestaurantResolver;

    private OrderQueryUseCase orderQueryUseCase;

    @BeforeEach
    void setUp() {
        orderQueryUseCase = new OrderQueryUseCase(orderPersistencePort, new OrderValidator(),
                new PaginationValidator(), employeeRestaurantResolver);
    }

    @Test
    @DisplayName("Lists the orders of the employee's restaurant filtered by status and paginated")
    void shouldListOrdersByStatusForEmployeeRestaurant() {
        PagedResult<Order> expected = new PagedResult<>(List.of(), 0, 10, 0L, 0);
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(orderPersistencePort.findOrdersByRestaurantAndStatus(
                RESTAURANT_ID, OrderStatus.PENDING, 0, 10)).thenReturn(expected);

        PagedResult<Order> result = orderQueryUseCase.listOrdersByStatus(EMPLOYEE_ID, "PENDING", 0, 10);

        assertSame(expected, result);
        verify(orderPersistencePort).findOrdersByRestaurantAndStatus(
                RESTAURANT_ID, OrderStatus.PENDING, 0, 10);
    }

    @Test
    @DisplayName("Rejects listing orders with an unknown status value")
    void shouldThrowWhenStatusIsInvalid() {
        assertThrows(InvalidOrderStatusException.class,
                () -> orderQueryUseCase.listOrdersByStatus(EMPLOYEE_ID, "NOT_A_STATUS", 0, 10));
        verify(orderPersistencePort, never())
                .findOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rejects listing orders when the employee is not assigned to any restaurant")
    void shouldThrowWhenEmployeeHasNoRestaurant() {
        when(employeeRestaurantResolver.resolveFor(EMPLOYEE_ID))
                .thenThrow(new EmployeeNotLinkedToRestaurantException());

        assertThrows(EmployeeNotLinkedToRestaurantException.class,
                () -> orderQueryUseCase.listOrdersByStatus(EMPLOYEE_ID, "PENDING", 0, 10));
        verify(orderPersistencePort, never())
                .findOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rejects listing orders with invalid pagination before resolving the restaurant")
    void shouldThrowWhenPaginationIsInvalid() {
        assertThrows(InvalidPaginationException.class,
                () -> orderQueryUseCase.listOrdersByStatus(EMPLOYEE_ID, "PENDING", -1, 10));
        verify(employeeRestaurantResolver, never()).resolveFor(any());
        verify(orderPersistencePort, never())
                .findOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }
}
