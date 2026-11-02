package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class RequiredFieldException extends DomainException {
    public RequiredFieldException() {
        super(DomainConstants.FIELD_REQUIRED_MESSAGE);
    }
}
