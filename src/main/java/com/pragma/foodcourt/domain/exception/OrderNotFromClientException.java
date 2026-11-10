package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when a client tries to cancel an order that does not belong to them (HU16). A client may
 * only cancel their own orders. Mapped to HTTP 403 through a dedicated {@code ControllerAdvisor}
 * handler.
 */
public class OrderNotFromClientException extends DomainException {
    public OrderNotFromClientException() {
        super(DomainConstants.ORDER_NOT_FROM_CLIENT_MESSAGE);
    }
}
