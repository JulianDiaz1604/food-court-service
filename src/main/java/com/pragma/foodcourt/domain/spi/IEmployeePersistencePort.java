package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.EmployeeRestaurant;

public interface IEmployeePersistencePort {

    EmployeeRestaurant saveEmployeeRestaurant(EmployeeRestaurant employeeRestaurant);
}
