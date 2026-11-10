package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Raised when messaging-service is unreachable or returns an unexpected response while sending
 * the "order ready" notification. Mapped to HTTP 502 Bad Gateway.
 *
 * <p>When this exception is thrown the order has NOT been persisted as READY yet (the notification
 * step precedes the status update), so the caller may safely retry the mark-as-ready operation.</p>
 */
public class MessagingServiceUnavailableException extends RuntimeException {
    public MessagingServiceUnavailableException() {
        super(DomainConstants.MESSAGING_SERVICE_UNAVAILABLE_MESSAGE);
    }
}
