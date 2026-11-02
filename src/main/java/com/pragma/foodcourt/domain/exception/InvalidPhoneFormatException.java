package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidPhoneFormatException extends DomainException {
    public InvalidPhoneFormatException() {
        super(DomainConstants.INVALID_PHONE_MESSAGE);
    }
}
