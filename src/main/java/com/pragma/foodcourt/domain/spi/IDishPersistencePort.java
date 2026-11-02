package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Dish;

import java.util.UUID;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);

    Dish findDishById(UUID dishId);
}
