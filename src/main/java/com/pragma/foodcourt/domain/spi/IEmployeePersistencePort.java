package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.EmployeeRestaurant;

import java.util.UUID;

public interface IEmployeePersistencePort {

    EmployeeRestaurant saveEmployeeRestaurant(EmployeeRestaurant employeeRestaurant);

    UUID findRestaurantIdByEmployee(UUID employeeId);

    /**
     * Returns {@code true} when the given employee is already linked to the given restaurant.
     */
    boolean existsLink(UUID employeeId, UUID restaurantId);
}
