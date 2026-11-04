package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidEmailFormatException extends DomainException {
    public InvalidEmailFormatException() {
        super(DomainConstants.INVALID_EMAIL_MESSAGE);
    }
}
