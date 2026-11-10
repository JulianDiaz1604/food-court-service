package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when an employee tries to mark an order as READY but the order is not in
 * {@code IN_PREPARATION} status, so the {@code IN_PREPARATION → READY} transition is not allowed.
 * Mapped to HTTP 409 through {@link ConflictException}.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the one extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class OrderNotInPreparationException extends ConflictException {
    public OrderNotInPreparationException() {
        super(DomainConstants.ORDER_NOT_IN_PREPARATION_MESSAGE);
    }
}
