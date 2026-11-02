package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidOwnerException extends DomainException {
    public InvalidOwnerException() {
        super(DomainConstants.INVALID_OWNER_MESSAGE);
    }
}
