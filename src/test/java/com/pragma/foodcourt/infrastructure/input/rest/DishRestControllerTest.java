package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishStatusRequestDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;
import com.pragma.foodcourt.application.handler.IDishHandler;
import com.pragma.foodcourt.infrastructure.configuration.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DishRestController} with typed principal (JI-S03).
 * Verifies that the controller correctly reads ownerId from {@link AuthenticatedUser#userId()}
 * and passes it to the handler — no behavior change from the previous String-principal path.
 */
@ExtendWith(MockitoExtension.class)
class DishRestControllerTest {

    @Mock
    private IDishHandler dishHandler;

    private DishRestController controller;

    @BeforeEach
    void setUp() {
        controller = new DishRestController(dishHandler);
    }

    @Test
    void whenCreateDishCalledWithTypedPrincipalThenPassesOwnerUserIdToHandler() {
        // JI-S03: OWNER endpoint reads ownerId from typed principal's userId field
        UUID ownerId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(ownerId, "OWNER", null);
        CreateDishRequestDto request = new CreateDishRequestDto(
                "Pizza", 25000, "Classic pizza", "http://img.url", "Italian", UUID.randomUUID());
        DishResponseDto expected = new DishResponseDto();
        when(dishHandler.createDish(request, ownerId)).thenReturn(expected);

        ResponseEntity<DishResponseDto> response = controller.createDish(principal, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(dishHandler).createDish(request, ownerId);
    }

    @Test
    void whenUpdateDishCalledWithTypedPrincipalThenPassesOwnerUserIdToHandler() {
        UUID dishId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(ownerId, "OWNER", null);
        UpdateDishRequestDto request = new UpdateDishRequestDto();
        DishResponseDto expected = new DishResponseDto();
        when(dishHandler.updateDish(dishId, request, ownerId)).thenReturn(expected);

        ResponseEntity<DishResponseDto> response = controller.updateDish(dishId, principal, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(dishHandler).updateDish(dishId, request, ownerId);
    }

    @Test
    void whenSetDishStatusCalledWithTypedPrincipalThenPassesOwnerUserIdToHandler() {
        UUID dishId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(ownerId, "OWNER", null);
        UpdateDishStatusRequestDto request = new UpdateDishStatusRequestDto();
        DishResponseDto expected = new DishResponseDto();
        when(dishHandler.setDishStatus(dishId, request, ownerId)).thenReturn(expected);

        ResponseEntity<DishResponseDto> response = controller.setDishStatus(dishId, principal, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(dishHandler).setDishStatus(dishId, request, ownerId);
    }
}
