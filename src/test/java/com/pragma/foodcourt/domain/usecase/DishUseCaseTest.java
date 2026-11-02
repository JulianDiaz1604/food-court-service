package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.InvalidPriceException;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.validation.DishValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private DishUseCase dishUseCase;
    private Dish validDish;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        dishUseCase = new DishUseCase(dishPersistencePort, restaurantPersistencePort, new DishValidator());
        validDish = new Dish(null, "Bandeja Paisa", 25000,
                "Traditional dish", "http://img/bandeja.png", "Main course", RESTAURANT_ID, false);
        restaurant = new Restaurant(RESTAURANT_ID, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", OWNER_ID);
    }

    @Test
    @DisplayName("Persists the dish and forces active to true when owner creates it in their restaurant")
    void shouldPersistDishWithActiveTrueWhenOwnerIsValid() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(dishPersistencePort.saveDish(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dishUseCase.createDish(validDish, OWNER_ID);

        ArgumentCaptor<Dish> captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).saveDish(captor.capture());
        assertTrue(captor.getValue().isActive());
    }

    @Test
    @DisplayName("Rejects when the restaurant does not exist")
    void shouldThrowWhenRestaurantNotFound() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class, () -> dishUseCase.createDish(validDish, OWNER_ID));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Rejects when the requester is not the owner of the restaurant")
    void shouldThrowWhenRequesterIsNotRestaurantOwner() {
        UUID anotherOwnerId = UUID.randomUUID();
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        assertThrows(NotRestaurantOwnerException.class,
                () -> dishUseCase.createDish(validDish, anotherOwnerId));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Does not touch persistence when field validation fails")
    void shouldNotReachPersistenceWhenValidationFails() {
        validDish.setPrice(0);

        assertThrows(InvalidPriceException.class, () -> dishUseCase.createDish(validDish, OWNER_ID));
        verify(restaurantPersistencePort, never()).findRestaurantById(any());
        verify(dishPersistencePort, never()).saveDish(any());
    }
}
