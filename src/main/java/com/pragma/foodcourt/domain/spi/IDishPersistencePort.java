package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.PagedResult;

import java.util.UUID;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);

    Dish findDishById(UUID dishId);

    PagedResult<Dish> findActiveDishesByRestaurant(UUID restaurantId, String category, int page, int size);
}
