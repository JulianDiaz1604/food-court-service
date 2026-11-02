package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException() {
        super(DomainConstants.RESTAURANT_NOT_FOUND_MESSAGE);
    }
}
