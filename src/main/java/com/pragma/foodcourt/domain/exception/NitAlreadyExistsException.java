package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when a restaurant is created with a NIT that is already registered.
 */
public class NitAlreadyExistsException extends ConflictException {
    public NitAlreadyExistsException() {
        super(DomainConstants.NIT_ALREADY_EXISTS_MESSAGE);
    }
}
