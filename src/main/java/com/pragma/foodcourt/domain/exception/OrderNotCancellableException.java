package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when a client tries to cancel an order that is not in {@code PENDING} status (HU16,
 * acceptance criterion 1): once an order is in preparation it can no longer be cancelled. Mapped to
 * HTTP 409 through {@link ConflictException}.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the one extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class OrderNotCancellableException extends ConflictException {
    public OrderNotCancellableException() {
        super(DomainConstants.ORDER_NOT_CANCELLABLE_MESSAGE);
    }
}
