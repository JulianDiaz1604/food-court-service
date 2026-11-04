package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.EmployeeRestaurant;

import java.util.UUID;

public interface IEmployeePersistencePort {

    EmployeeRestaurant saveEmployeeRestaurant(EmployeeRestaurant employeeRestaurant);

    UUID findRestaurantIdByEmployee(UUID employeeId);
}
