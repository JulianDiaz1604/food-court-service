package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IEmployeeServicePort;
import com.pragma.foodcourt.domain.exception.EmployeeLinkAlreadyExistsException;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;

import java.util.UUID;

public class EmployeeUseCase implements IEmployeeServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IEmployeePersistencePort employeePersistencePort;

    public EmployeeUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                           IEmployeePersistencePort employeePersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.employeePersistencePort = employeePersistencePort;
    }

    @Override
    public EmployeeRestaurant registerEmployee(UUID employeeId, UUID restaurantId, UUID requesterOwnerId) {
        Restaurant restaurant = findRestaurant(restaurantId);
        ensureRequesterOwnsRestaurant(restaurant, requesterOwnerId);
        ensureLinkDoesNotExist(employeeId, restaurantId);
        return employeePersistencePort.saveEmployeeRestaurant(
                new EmployeeRestaurant(null, employeeId, restaurantId));
    }

    private Restaurant findRestaurant(UUID restaurantId) {
        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException();
        }
        return restaurant;
    }

    private void ensureRequesterOwnsRestaurant(Restaurant restaurant, UUID requesterOwnerId) {
        if (!restaurant.getOwnerId().equals(requesterOwnerId)) {
            throw new NotRestaurantOwnerException();
        }
    }

    private void ensureLinkDoesNotExist(UUID employeeId, UUID restaurantId) {
        if (employeePersistencePort.existsLink(employeeId, restaurantId)) {
            throw new EmployeeLinkAlreadyExistsException();
        }
    }
}
