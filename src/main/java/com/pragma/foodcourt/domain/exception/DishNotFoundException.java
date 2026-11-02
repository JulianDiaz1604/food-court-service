package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class DishNotFoundException extends DomainException {
    public DishNotFoundException() {
        super(DomainConstants.DISH_NOT_FOUND_MESSAGE);
    }
}
