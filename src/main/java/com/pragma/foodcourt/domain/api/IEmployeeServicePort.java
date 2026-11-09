package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.EmployeeRestaurant;

import java.util.UUID;

public interface IEmployeeServicePort {

    /**
     * Links an existing employee (a user already in the users-service with the EMPLOYEE role)
     * to a restaurant. Only the owner of that restaurant may perform this action.
     *
     * @param employeeId       the id of the employee user to link.
     * @param restaurantId     the target restaurant id.
     * @param requesterOwnerId the authenticated caller (must own the restaurant).
     * @return the persisted employee-restaurant link.
     */
    EmployeeRestaurant registerEmployee(UUID employeeId, UUID restaurantId, UUID requesterOwnerId);
}
