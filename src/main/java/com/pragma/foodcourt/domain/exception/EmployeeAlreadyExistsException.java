package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when the users-service rejects the employee creation because the email or document number
 * is already registered. Mapped to HTTP 409 through {@link ConflictException}.
 *
 * <p>{@code java:S110} (inheritance depth) is suppressed on purpose: the one extra level comes from
 * the deliberate {@code ConflictException} marker that {@code ControllerAdvisor} maps to HTTP 409.
 * Flattening the hierarchy would lose that mapping; the depth here is a meaningful domain design.</p>
 */
@SuppressWarnings("java:S110")
public class EmployeeAlreadyExistsException extends ConflictException {
    public EmployeeAlreadyExistsException() {
        super(DomainConstants.EMPLOYEE_ALREADY_EXISTS_MESSAGE);
    }
}
