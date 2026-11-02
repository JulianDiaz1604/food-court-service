package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidPriceException extends DomainException {
    public InvalidPriceException() {
        super(DomainConstants.INVALID_PRICE_MESSAGE);
    }
}
