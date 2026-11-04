package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class EmptyOrderException extends DomainException {
    public EmptyOrderException() {
        super(DomainConstants.ORDER_EMPTY_MESSAGE);
    }
}
