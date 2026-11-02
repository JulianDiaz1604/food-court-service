package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.InvalidOwnerException;
import com.pragma.foodcourt.domain.exception.InvalidRestaurantNameException;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.spi.IUserValidationPort;
import com.pragma.foodcourt.domain.validation.RestaurantValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserValidationPort userValidationPort;

    private RestaurantUseCase restaurantUseCase;
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        restaurantUseCase = new RestaurantUseCase(
                restaurantPersistencePort, userValidationPort, new RestaurantValidator());
        validRestaurant = new Restaurant(null, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", 2L);
    }

    @Test
    @DisplayName("Persists the restaurant when data is valid and owner id belongs to an owner")
    void createRestaurant_withValidOwner_persistsRestaurant() {
        when(userValidationPort.isOwner(2L)).thenReturn(true);
        when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

        assertEquals("Tablaito", result.getName());
        verify(userValidationPort).isOwner(2L);
        verify(restaurantPersistencePort).saveRestaurant(validRestaurant);
    }

    @Test
    @DisplayName("Rejects when the owner id does not belong to an owner")
    void createRestaurant_whenOwnerIdIsNotAnOwner_throwsInvalidOwnerException() {
        when(userValidationPort.isOwner(2L)).thenReturn(false);

        assertThrows(InvalidOwnerException.class, () -> restaurantUseCase.createRestaurant(validRestaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Does not call users-service when field validation already fails")
    void createRestaurant_whenValidationFails_doesNotCallUserValidation() {
        validRestaurant.setName("12345");

        assertThrows(InvalidRestaurantNameException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));
        verify(userValidationPort, never()).isOwner(anyLong());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }
}
