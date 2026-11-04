package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class EmployeeNotLinkedToRestaurantException extends DomainException {
    public EmployeeNotLinkedToRestaurantException() {
        super(DomainConstants.EMPLOYEE_NOT_LINKED_MESSAGE);
    }
}
