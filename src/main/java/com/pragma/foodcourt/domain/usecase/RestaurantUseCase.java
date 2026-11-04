package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IRestaurantServicePort;
import com.pragma.foodcourt.domain.exception.InvalidOwnerException;
import com.pragma.foodcourt.domain.exception.NitAlreadyExistsException;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.spi.IUserValidationPort;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import com.pragma.foodcourt.domain.validation.RestaurantValidator;

import java.util.UUID;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPort userValidationPort;
    private final RestaurantValidator restaurantValidator;
    private final PaginationValidator paginationValidator;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                             IUserValidationPort userValidationPort,
                             RestaurantValidator restaurantValidator,
                             PaginationValidator paginationValidator) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userValidationPort = userValidationPort;
        this.restaurantValidator = restaurantValidator;
        this.paginationValidator = paginationValidator;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurantValidator.validateNewRestaurant(restaurant);
        ensureNitIsAvailable(restaurant.getNit());
        ensureOwnerIsValid(restaurant.getOwnerId());
        return restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public PagedResult<Restaurant> listRestaurants(int page, int size) {
        paginationValidator.validate(page, size);
        return restaurantPersistencePort.findAllOrderedByName(page, size);
    }

    private void ensureNitIsAvailable(String nit) {
        if (restaurantPersistencePort.existsByNit(nit)) {
            throw new NitAlreadyExistsException();
        }
    }

    private void ensureOwnerIsValid(UUID ownerId) {
        if (!userValidationPort.isOwner(ownerId)) {
            throw new InvalidOwnerException();
        }
    }
}
