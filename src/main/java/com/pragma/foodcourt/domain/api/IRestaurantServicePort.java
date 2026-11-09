package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;

import java.util.UUID;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);

    PagedResult<Restaurant> listRestaurants(int page, int size);

    /**
     * Returns the ownerId of the restaurant with the given id.
     *
     * @param restaurantId the restaurant to look up.
     * @return the UUID of the owner.
     * @throws com.pragma.foodcourt.domain.exception.RestaurantNotFoundException if no restaurant
     *                                                                           with that id exists.
     */
    UUID findOwnerId(UUID restaurantId);
}
