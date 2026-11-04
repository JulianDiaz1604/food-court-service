package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when a restaurant is created with a NIT that is already registered.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the one extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class NitAlreadyExistsException extends ConflictException {
    public NitAlreadyExistsException() {
        super(DomainConstants.NIT_ALREADY_EXISTS_MESSAGE);
    }
}
