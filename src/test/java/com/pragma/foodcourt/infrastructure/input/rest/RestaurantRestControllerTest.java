package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.response.OwnerIdResponseDto;
import com.pragma.foodcourt.application.handler.IRestaurantHandler;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the ownership GET endpoint in {@link RestaurantRestController}.
 * Spring Security's 401 for unauthenticated requests is enforced by the filter chain and
 * is not directly testable at this unit level (ROV-S04/S06 are covered by integration tests).
 */
@ExtendWith(MockitoExtension.class)
class RestaurantRestControllerTest {

    @Mock
    private IRestaurantHandler restaurantHandler;

    private RestaurantRestController controller;

    @BeforeEach
    void setUp() {
        controller = new RestaurantRestController(restaurantHandler);
    }

    @Test
    @DisplayName("Returns 200 with restaurantId and ownerId when restaurant exists (ROV-S01)")
    void whenGetRestaurantOwnerCalledWithExistingRestaurantThenReturnsOwnerIdResponse() {
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        OwnerIdResponseDto expected = new OwnerIdResponseDto(restaurantId, ownerId);
        when(restaurantHandler.getOwnerId(restaurantId)).thenReturn(expected);

        ResponseEntity<OwnerIdResponseDto> response = controller.getRestaurantOwner(restaurantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        assertEquals(restaurantId, response.getBody().getRestaurantId());
        assertEquals(ownerId, response.getBody().getOwnerId());
        verify(restaurantHandler).getOwnerId(restaurantId);
    }

    @Test
    @DisplayName("Propagates RestaurantNotFoundException when restaurant does not exist (ROV-S02)")
    void whenGetRestaurantOwnerCalledWithMissingRestaurantThenPropagatesNotFoundException() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantHandler.getOwnerId(restaurantId)).thenThrow(new RestaurantNotFoundException());

        assertThrows(RestaurantNotFoundException.class,
                () -> controller.getRestaurantOwner(restaurantId));
        verify(restaurantHandler).getOwnerId(restaurantId);
    }
}
