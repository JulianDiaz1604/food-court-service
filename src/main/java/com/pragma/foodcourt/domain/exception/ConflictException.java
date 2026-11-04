package com.pragma.foodcourt.domain.exception;

/**
 * Base class for conflicts with the current state of a resource (uniqueness, etc.). Mapped to HTTP 409.
 */
public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(message);
    }
}
