package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidQuantityException extends DomainException {
    public InvalidQuantityException() {
        super(DomainConstants.INVALID_QUANTITY_MESSAGE);
    }
}
