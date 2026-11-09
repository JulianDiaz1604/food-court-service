package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when the employee-restaurant link being registered already exists.
 * Mapped to HTTP 409 through {@link ConflictException}.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class EmployeeLinkAlreadyExistsException extends ConflictException {
    public EmployeeLinkAlreadyExistsException() {
        super(DomainConstants.EMPLOYEE_LINK_ALREADY_EXISTS_MESSAGE);
    }
}
