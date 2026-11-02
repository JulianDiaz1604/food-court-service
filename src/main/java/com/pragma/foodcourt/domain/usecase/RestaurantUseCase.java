package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IRestaurantServicePort;
import com.pragma.foodcourt.domain.exception.InvalidOwnerException;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.spi.IUserValidationPort;
import com.pragma.foodcourt.domain.validation.RestaurantValidator;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPort userValidationPort;
    private final RestaurantValidator restaurantValidator;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                             IUserValidationPort userValidationPort,
                             RestaurantValidator restaurantValidator) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userValidationPort = userValidationPort;
        this.restaurantValidator = restaurantValidator;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurantValidator.validateNewRestaurant(restaurant);
        ensureOwnerIsValid(restaurant.getOwnerId());
        return restaurantPersistencePort.saveRestaurant(restaurant);
    }

    private void ensureOwnerIsValid(Long ownerId) {
        if (!userValidationPort.isOwner(ownerId)) {
            throw new InvalidOwnerException();
        }
    }
}
