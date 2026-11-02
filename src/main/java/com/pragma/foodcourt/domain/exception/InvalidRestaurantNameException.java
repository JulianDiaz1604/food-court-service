package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidRestaurantNameException extends DomainException {
    public InvalidRestaurantNameException() {
        super(DomainConstants.INVALID_RESTAURANT_NAME_MESSAGE);
    }
}
