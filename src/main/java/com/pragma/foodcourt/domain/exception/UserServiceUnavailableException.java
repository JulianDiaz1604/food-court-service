package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when users-service is unreachable or returns an unexpected response while resolving
 * a client's contact information. Mapped to HTTP 502 Bad Gateway.
 */
public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException() {
        super(DomainConstants.USER_SERVICE_UNAVAILABLE_MESSAGE);
    }
}
