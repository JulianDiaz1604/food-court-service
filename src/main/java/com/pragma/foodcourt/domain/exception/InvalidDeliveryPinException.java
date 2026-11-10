package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when the security PIN digited by the employee does not match the one messaging-service
 * sent to the client (HU15, acceptance criterion 3). Mapped to HTTP 403 through a dedicated
 * {@code ControllerAdvisor} handler: the delivery action is forbidden without the correct PIN.
 */
public class InvalidDeliveryPinException extends DomainException {
    public InvalidDeliveryPinException() {
        super(DomainConstants.INVALID_DELIVERY_PIN_MESSAGE);
    }
}
