package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IDishServicePort;
import com.pragma.foodcourt.domain.exception.DishNotFoundException;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.validation.DishValidator;

import java.util.UUID;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final DishValidator dishValidator;

    public DishUseCase(IDishPersistencePort dishPersistencePort,
                       IRestaurantPersistencePort restaurantPersistencePort,
                       DishValidator dishValidator) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishValidator = dishValidator;
    }

    @Override
    public Dish createDish(Dish dish, UUID ownerId) {
        dishValidator.validateNewDish(dish);
        Restaurant restaurant = findRestaurant(dish.getRestaurantId());
        ensureRequesterOwnsRestaurant(restaurant, ownerId);
        dish.setActive(true);
        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish updateDish(UUID dishId, Integer price, String description, UUID ownerId) {
        dishValidator.validateDishUpdate(price, description);
        Dish dish = findDish(dishId);
        Restaurant restaurant = findRestaurant(dish.getRestaurantId());
        ensureRequesterOwnsRestaurant(restaurant, ownerId);
        dish.setPrice(price);
        dish.setDescription(description);
        return dishPersistencePort.saveDish(dish);
    }

    private Dish findDish(UUID dishId) {
        Dish dish = dishPersistencePort.findDishById(dishId);
        if (dish == null) {
            throw new DishNotFoundException();
        }
        return dish;
    }

    private Restaurant findRestaurant(UUID restaurantId) {
        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException();
        }
        return restaurant;
    }

    private void ensureRequesterOwnsRestaurant(Restaurant restaurant, UUID ownerId) {
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new NotRestaurantOwnerException();
        }
    }
}
