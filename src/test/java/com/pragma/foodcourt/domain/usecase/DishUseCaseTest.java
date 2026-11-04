package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.DishNotFoundException;
import com.pragma.foodcourt.domain.exception.InvalidPaginationException;
import com.pragma.foodcourt.domain.exception.InvalidPriceException;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.validation.DishValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
        dishUseCase = new DishUseCase(dishPersistencePort, restaurantPersistencePort,
                new DishValidator(), new PaginationValidator());
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

    @Test
    @DisplayName("Updates only price and description when owner updates a dish of their restaurant")
    void shouldUpdateOnlyPriceAndDescriptionWhenOwnerIsValid() {
        UUID dishId = UUID.randomUUID();
        Dish existingDish = new Dish(dishId, "Bandeja Paisa", 25000,
                "Old description", "http://img/bandeja.png", "Main course", RESTAURANT_ID, true);
        when(dishPersistencePort.findDishById(dishId)).thenReturn(existingDish);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(dishPersistencePort.saveDish(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dishUseCase.updateDish(dishId, 30000, "New description", OWNER_ID);

        ArgumentCaptor<Dish> captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).saveDish(captor.capture());
        Dish saved = captor.getValue();
        assertEquals(30000, saved.getPrice());
        assertEquals("New description", saved.getDescription());
        assertEquals("Bandeja Paisa", saved.getName());
        assertEquals("Main course", saved.getCategory());
        assertEquals("http://img/bandeja.png", saved.getImageUrl());
        assertTrue(saved.isActive());
    }

    @Test
    @DisplayName("Rejects an update when the dish does not exist")
    void shouldThrowWhenDishNotFoundOnUpdate() {
        UUID dishId = UUID.randomUUID();
        when(dishPersistencePort.findDishById(dishId)).thenReturn(null);

        assertThrows(DishNotFoundException.class,
                () -> dishUseCase.updateDish(dishId, 30000, "New description", OWNER_ID));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Rejects an update when the requester is not the owner of the restaurant")
    void shouldThrowWhenRequesterIsNotOwnerOnUpdate() {
        UUID dishId = UUID.randomUUID();
        UUID anotherOwnerId = UUID.randomUUID();
        Dish existingDish = new Dish(dishId, "Bandeja Paisa", 25000,
                "Old description", "http://img/bandeja.png", "Main course", RESTAURANT_ID, true);
        when(dishPersistencePort.findDishById(dishId)).thenReturn(existingDish);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        assertThrows(NotRestaurantOwnerException.class,
                () -> dishUseCase.updateDish(dishId, 30000, "New description", anotherOwnerId));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Does not touch persistence when update validation fails")
    void shouldNotReachPersistenceWhenUpdateValidationFails() {
        UUID dishId = UUID.randomUUID();

        assertThrows(RequiredFieldException.class,
                () -> dishUseCase.updateDish(dishId, 30000, "  ", OWNER_ID));
        verify(dishPersistencePort, never()).findDishById(any());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Disables a dish when the owner toggles its status off in their restaurant")
    void shouldDisableDishWhenOwnerIsValid() {
        UUID dishId = UUID.randomUUID();
        Dish existingDish = new Dish(dishId, "Bandeja Paisa", 25000,
                "Traditional dish", "http://img/bandeja.png", "Main course", RESTAURANT_ID, true);
        when(dishPersistencePort.findDishById(dishId)).thenReturn(existingDish);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(dishPersistencePort.saveDish(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dishUseCase.setDishStatus(dishId, false, OWNER_ID);

        ArgumentCaptor<Dish> captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).saveDish(captor.capture());
        assertFalse(captor.getValue().isActive());
    }

    @Test
    @DisplayName("Enables a dish when the owner toggles its status on in their restaurant")
    void shouldEnableDishWhenOwnerIsValid() {
        UUID dishId = UUID.randomUUID();
        Dish existingDish = new Dish(dishId, "Bandeja Paisa", 25000,
                "Traditional dish", "http://img/bandeja.png", "Main course", RESTAURANT_ID, false);
        when(dishPersistencePort.findDishById(dishId)).thenReturn(existingDish);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(dishPersistencePort.saveDish(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dishUseCase.setDishStatus(dishId, true, OWNER_ID);

        ArgumentCaptor<Dish> captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).saveDish(captor.capture());
        assertTrue(captor.getValue().isActive());
    }

    @Test
    @DisplayName("Rejects a status change when the dish does not exist")
    void shouldThrowWhenDishNotFoundOnStatusChange() {
        UUID dishId = UUID.randomUUID();
        when(dishPersistencePort.findDishById(dishId)).thenReturn(null);

        assertThrows(DishNotFoundException.class,
                () -> dishUseCase.setDishStatus(dishId, false, OWNER_ID));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Rejects a status change when the requester is not the owner of the restaurant")
    void shouldThrowWhenRequesterIsNotOwnerOnStatusChange() {
        UUID dishId = UUID.randomUUID();
        UUID anotherOwnerId = UUID.randomUUID();
        Dish existingDish = new Dish(dishId, "Bandeja Paisa", 25000,
                "Traditional dish", "http://img/bandeja.png", "Main course", RESTAURANT_ID, true);
        when(dishPersistencePort.findDishById(dishId)).thenReturn(existingDish);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        assertThrows(NotRestaurantOwnerException.class,
                () -> dishUseCase.setDishStatus(dishId, false, anotherOwnerId));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Does not touch persistence when the status flag is missing")
    void shouldNotReachPersistenceWhenStatusValidationFails() {
        UUID dishId = UUID.randomUUID();

        assertThrows(RequiredFieldException.class,
                () -> dishUseCase.setDishStatus(dishId, null, OWNER_ID));
        verify(dishPersistencePort, never()).findDishById(any());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Returns the restaurant's active dishes with no category filter when category is blank")
    void shouldListActiveDishesWithoutCategoryFilterWhenCategoryIsBlank() {
        Dish dish = new Dish(UUID.randomUUID(), "Bandeja Paisa", 25000,
                "Traditional dish", "http://img/bandeja.png", "Main course", RESTAURANT_ID, true);
        PagedResult<Dish> page = new PagedResult<>(List.of(dish), 0, 10, 1L, 1);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(dishPersistencePort.findActiveDishesByRestaurant(RESTAURANT_ID, null, 0, 10)).thenReturn(page);

        PagedResult<Dish> result = dishUseCase.listDishesByRestaurant(RESTAURANT_ID, "   ", 0, 10);

        assertEquals(1, result.getContent().size());
        verify(dishPersistencePort).findActiveDishesByRestaurant(RESTAURANT_ID, null, 0, 10);
    }

    @Test
    @DisplayName("Forwards the trimmed category to persistence when a category filter is provided")
    void shouldListActiveDishesFilteredByCategory() {
        PagedResult<Dish> page = new PagedResult<>(List.of(), 1, 5, 0L, 0);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(dishPersistencePort.findActiveDishesByRestaurant(RESTAURANT_ID, "Main course", 1, 5))
                .thenReturn(page);

        dishUseCase.listDishesByRestaurant(RESTAURANT_ID, "  Main course  ", 1, 5);

        verify(dishPersistencePort).findActiveDishesByRestaurant(RESTAURANT_ID, "Main course", 1, 5);
    }

    @Test
    @DisplayName("Rejects listing when the restaurant does not exist and never touches dish persistence")
    void shouldThrowWhenRestaurantNotFoundOnListing() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class,
                () -> dishUseCase.listDishesByRestaurant(RESTAURANT_ID, null, 0, 10));
        verify(dishPersistencePort, never())
                .findActiveDishesByRestaurant(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rejects listing with invalid pagination and never touches persistence")
    void shouldThrowWhenPaginationIsInvalidOnListing() {
        assertThrows(InvalidPaginationException.class,
                () -> dishUseCase.listDishesByRestaurant(RESTAURANT_ID, null, 0, 0));
        verify(restaurantPersistencePort, never()).findRestaurantById(any());
        verify(dishPersistencePort, never())
                .findActiveDishesByRestaurant(any(), isNull(), eq(0), eq(0));
    }
}
