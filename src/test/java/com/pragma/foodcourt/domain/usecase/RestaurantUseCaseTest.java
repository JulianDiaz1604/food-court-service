package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.InvalidPaginationException;
import com.pragma.foodcourt.domain.exception.InvalidRestaurantNameException;
import com.pragma.foodcourt.domain.exception.NitAlreadyExistsException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import com.pragma.foodcourt.domain.validation.RestaurantValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    private static final UUID OWNER_ID = UUID.randomUUID();

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private RestaurantUseCase restaurantUseCase;
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        restaurantUseCase = new RestaurantUseCase(
                restaurantPersistencePort,
                new RestaurantValidator(), new PaginationValidator());
        validRestaurant = new Restaurant(null, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", OWNER_ID);
    }

    @Test
    @DisplayName("Persists the restaurant when all fields are valid and ownerId is present (JI-S05: no outbound call)")
    void shouldPersistRestaurantWhenDataIsValid() {
        when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

        assertEquals("Tablaito", result.getName());
        verify(restaurantPersistencePort).saveRestaurant(validRestaurant);
    }

    @Test
    @DisplayName("Rejects when the NIT already belongs to another restaurant")
    void shouldThrowWhenNitAlreadyExists() {
        when(restaurantPersistencePort.existsByNit("900123456")).thenReturn(true);

        assertThrows(NitAlreadyExistsException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Rejects when field validation fails; persistence is never reached")
    void shouldNotReachPersistenceWhenFieldValidationFails() {
        validRestaurant.setName("12345");

        assertThrows(InvalidRestaurantNameException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Rejects restaurant creation when ownerId is null (RequiredFieldException from validator)")
    void shouldThrowWhenOwnerIdIsNull() {
        validRestaurant.setOwnerId(null);

        // RestaurantValidator.validateRequiredFields throws RequiredFieldException for null ownerId
        assertThrows(com.pragma.foodcourt.domain.exception.RequiredFieldException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Returns the alphabetically ordered page of restaurants for valid pagination")
    void shouldReturnRestaurantPageWhenPaginationIsValid() {
        PagedResult<Restaurant> page = new PagedResult<>(List.of(validRestaurant), 0, 10, 1L, 1);
        when(restaurantPersistencePort.findAllOrderedByName(0, 10)).thenReturn(page);

        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("Tablaito", result.getContent().get(0).getName());
        verify(restaurantPersistencePort).findAllOrderedByName(0, 10);
    }

    @Test
    @DisplayName("Rejects listing with invalid pagination and never touches persistence")
    void shouldThrowWhenPaginationIsInvalid() {
        assertThrows(InvalidPaginationException.class, () -> restaurantUseCase.listRestaurants(-1, 10));
        verify(restaurantPersistencePort, never()).findAllOrderedByName(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Returns the ownerId when the restaurant exists (ROV-S01)")
    void shouldReturnOwnerIdWhenRestaurantExists() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(validRestaurant);

        UUID result = restaurantUseCase.findOwnerId(restaurantId);

        assertEquals(OWNER_ID, result);
        verify(restaurantPersistencePort).findRestaurantById(restaurantId);
    }

    @Test
    @DisplayName("Throws RestaurantNotFoundException when the restaurant does not exist (ROV-S02, ROV-S05)")
    void shouldThrowWhenRestaurantNotFoundOnFindOwnerId() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class,
                () -> restaurantUseCase.findOwnerId(restaurantId));
    }
}
