package com.pragma.foodcourt.domain.spi;

import java.util.UUID;

/**
 * Driven port for validating the security PIN that was sent to the client when the order became
 * READY (HU14). The implementation calls messaging-service and must be placed in
 * {@code infrastructure/out/client}.
 */
public interface IOrderDeliveryPinPort {

    /**
     * Checks whether the given PIN matches the one messaging-service sent to the client for the order.
     *
     * @param orderId the UUID of the order being delivered
     * @param pin     the PIN digited by the employee
     * @return {@code true} if the PIN matches, {@code false} if it does not
     * @throws com.pragma.foodcourt.domain.exception.MessagingServiceUnavailableException if
     *         messaging-service is unreachable or responds with an unexpected error
     */
    boolean isPinValid(UUID orderId, String pin);
}
