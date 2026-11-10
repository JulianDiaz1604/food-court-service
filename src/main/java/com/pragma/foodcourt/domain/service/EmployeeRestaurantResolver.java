package com.pragma.foodcourt.domain.service;

import com.pragma.foodcourt.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;

import java.util.UUID;

public class EmployeeRestaurantResolver {

    private final IEmployeePersistencePort employeePersistencePort;

    public EmployeeRestaurantResolver(IEmployeePersistencePort employeePersistencePort) {
        this.employeePersistencePort = employeePersistencePort;
    }

    public UUID resolveFor(UUID employeeId) {
        UUID restaurantId = employeePersistencePort.findRestaurantIdByEmployee(employeeId);
        if (restaurantId == null) {
            throw new EmployeeNotLinkedToRestaurantException();
        }
        return restaurantId;
    }
}
