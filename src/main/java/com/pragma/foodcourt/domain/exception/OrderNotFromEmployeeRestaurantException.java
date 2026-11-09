package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class OrderNotFromEmployeeRestaurantException extends DomainException {
    public OrderNotFromEmployeeRestaurantException() {
        super(DomainConstants.ORDER_NOT_FROM_EMPLOYEE_RESTAURANT_MESSAGE);
    }
}
