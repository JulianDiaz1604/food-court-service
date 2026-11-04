package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IEmployeeServicePort;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Employee;
import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.spi.IUserCreationPort;
import com.pragma.foodcourt.domain.validation.EmployeeValidator;

import java.util.UUID;

public class EmployeeUseCase implements IEmployeeServicePort {

    private final IUserCreationPort userCreationPort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IEmployeePersistencePort employeePersistencePort;
    private final EmployeeValidator employeeValidator;

    public EmployeeUseCase(IUserCreationPort userCreationPort,
                           IRestaurantPersistencePort restaurantPersistencePort,
                           IEmployeePersistencePort employeePersistencePort,
                           EmployeeValidator employeeValidator) {
        this.userCreationPort = userCreationPort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.employeePersistencePort = employeePersistencePort;
        this.employeeValidator = employeeValidator;
    }

    @Override
    public Employee createEmployee(Employee employee, UUID requesterOwnerId) {
        employeeValidator.validateNewEmployee(employee);
        Restaurant restaurant = findRestaurant(employee.getRestaurantId());
        ensureRequesterOwnsRestaurant(restaurant, requesterOwnerId);

        UUID employeeId = userCreationPort.createEmployee(employee);
        employeePersistencePort.saveEmployeeRestaurant(
                new EmployeeRestaurant(null, employeeId, restaurant.getId()));

        employee.setId(employeeId);
        employee.setPassword(null);
        return employee;
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
}
