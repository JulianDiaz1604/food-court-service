package com.pragma.foodcourt.domain.spi;

import java.util.UUID;

/**
 * Driven port for notifying the client that their order is ready for pick-up.
 * The implementation calls messaging-service and must be placed in {@code infrastructure/out/client}.
 */
public interface IOrderReadyNotificationPort {

    /**
     * Sends an "order ready" notification to the given phone number.
     *
     * @param orderId    the UUID of the order that is ready
     * @param phone      the client's phone number
     * @param clientName the client's name (optional — may be null)
     * @throws com.pragma.foodcourt.domain.exception.MessagingServiceUnavailableException if
     *         messaging-service is unreachable or responds with an unexpected error
     */
    void notifyOrderReady(UUID orderId, String phone, String clientName);
}
