package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IOrderHandler;
import com.pragma.foodcourt.infrastructure.configuration.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OrderRestController} with typed principal (JI-S04).
 * Verifies that the controller correctly reads clientId / employeeId from
 * {@link AuthenticatedUser#userId()} and passes it to the handler — no behavior change
 * from the previous String-principal path.
 */
@ExtendWith(MockitoExtension.class)
class OrderRestControllerTest {

    @Mock
    private IOrderHandler orderHandler;

    private OrderRestController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderRestController(orderHandler);
    }

    @Test
    void whenPlaceOrderCalledWithTypedClientPrincipalThenPassesClientUserIdToHandler() {
        // JI-S03 / JI-S04: CLIENT endpoint reads clientId from typed principal's userId field
        UUID clientId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(clientId, "CLIENT", null);
        CreateOrderRequestDto request = new CreateOrderRequestDto();
        OrderResponseDto expected = new OrderResponseDto();
        when(orderHandler.placeOrder(request, clientId)).thenReturn(expected);

        ResponseEntity<OrderResponseDto> response = controller.placeOrder(principal, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(orderHandler).placeOrder(request, clientId);
    }

    @Test
    void whenListOrdersByStatusCalledWithTypedEmployeePrincipalThenPassesEmployeeUserIdToHandler() {
        // JI-S04: EMPLOYEE endpoint reads employeeId from typed principal's userId field
        UUID employeeId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(employeeId, "EMPLOYEE",
                UUID.randomUUID());
        PageResponseDto<OrderResponseDto> expected =
                new PageResponseDto<>(Collections.emptyList(), 0, 10, 0L, 0);
        when(orderHandler.listOrdersByStatus(employeeId, "PENDING", 0, 10)).thenReturn(expected);

        ResponseEntity<PageResponseDto<OrderResponseDto>> response =
                controller.listOrdersByStatus(principal, "PENDING", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderHandler).listOrdersByStatus(employeeId, "PENDING", 0, 10);
    }
}
