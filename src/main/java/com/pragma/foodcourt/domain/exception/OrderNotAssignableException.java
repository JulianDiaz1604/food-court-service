package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when an employee tries to assign an order that is not in {@code PENDING} status, so the
 * {@code PENDING → IN_PREPARATION} transition is not allowed. Mapped to HTTP 409 through
 * {@link ConflictException}.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the one extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class OrderNotAssignableException extends ConflictException {
    public OrderNotAssignableException() {
        super(DomainConstants.ORDER_NOT_ASSIGNABLE_MESSAGE);
    }
}
