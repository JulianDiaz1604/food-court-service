package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when users-service returns 404 for the client referenced by an order.
 * Mapped to HTTP 404.
 */
public class ClientNotFoundException extends DomainException {
    public ClientNotFoundException() {
        super(DomainConstants.CLIENT_NOT_FOUND_MESSAGE);
    }
}
