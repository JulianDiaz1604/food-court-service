package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Restaurant;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);
}
