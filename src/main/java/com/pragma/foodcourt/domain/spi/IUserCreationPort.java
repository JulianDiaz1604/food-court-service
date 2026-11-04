package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.Employee;

import java.util.UUID;

/**
 * Driven port to create employee users in the users-service. The domain stays unaware of the
 * transport (HTTP, messaging, ...).
 */
public interface IUserCreationPort {

    /**
     * Creates the employee as a user with the EMPLOYEE role in the users-service.
     *
     * @return the id of the created user.
     */
    UUID createEmployee(Employee employee);
}
