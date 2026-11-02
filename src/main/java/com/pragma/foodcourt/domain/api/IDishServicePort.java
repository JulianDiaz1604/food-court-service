package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Dish;

import java.util.UUID;

public interface IDishServicePort {
    Dish createDish(Dish dish, UUID ownerId);
}
