package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidNitException extends DomainException {
    public InvalidNitException() {
        super(DomainConstants.INVALID_NIT_MESSAGE);
    }
}
