package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Restaurant;

import java.util.UUID;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);

    Restaurant findRestaurantById(UUID id);

    boolean existsByNit(String nit);
}
