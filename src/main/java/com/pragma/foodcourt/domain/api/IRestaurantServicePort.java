package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);

    PagedResult<Restaurant> listRestaurants(int page, int size);
}
