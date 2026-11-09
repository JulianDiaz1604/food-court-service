package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException() {
        super(DomainConstants.ORDER_NOT_FOUND_MESSAGE);
    }
}
