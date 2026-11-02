package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class NotRestaurantOwnerException extends DomainException {
    public NotRestaurantOwnerException() {
        super(DomainConstants.NOT_RESTAURANT_OWNER_MESSAGE);
    }
}
