package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when a client tries to place a new order while still having one in progress
 * (PENDING, IN_PREPARATION or READY). Mapped to HTTP 409 through {@link ConflictException}.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the one extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class ClientHasActiveOrderException extends ConflictException {
    public ClientHasActiveOrderException() {
        super(DomainConstants.CLIENT_HAS_ACTIVE_ORDER_MESSAGE);
    }
}
