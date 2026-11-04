package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.PagedResult;

import java.util.UUID;

public interface IDishServicePort {
    Dish createDish(Dish dish, UUID ownerId);

    Dish updateDish(UUID dishId, Integer price, String description, UUID ownerId);

    Dish setDishStatus(UUID dishId, Boolean active, UUID ownerId);

    PagedResult<Dish> listDishesByRestaurant(UUID restaurantId, String category, int page, int size);
}
