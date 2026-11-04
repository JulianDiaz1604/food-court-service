package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.Employee;

import java.util.UUID;

public interface IEmployeeServicePort {

    /**
     * Creates an employee for the given restaurant. Only the owner of that restaurant may do it.
     *
     * @param employee         the employee personal data plus the target restaurant id.
     * @param requesterOwnerId the authenticated caller (must own the restaurant).
     * @return the employee with its generated id.
     */
    Employee createEmployee(Employee employee, UUID requesterOwnerId);
}
