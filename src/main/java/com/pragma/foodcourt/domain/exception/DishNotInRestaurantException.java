package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class DishNotInRestaurantException extends DomainException {
    public DishNotInRestaurantException() {
        super(DomainConstants.DISH_NOT_IN_RESTAURANT_MESSAGE);
    }
}
