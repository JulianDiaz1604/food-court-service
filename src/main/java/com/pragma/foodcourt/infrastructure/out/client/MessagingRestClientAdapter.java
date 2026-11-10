package com.pragma.foodcourt.infrastructure.out.client;

import com.pragma.foodcourt.domain.exception.MessagingServiceUnavailableException;
import com.pragma.foodcourt.domain.spi.IOrderReadyNotificationPort;
import com.pragma.foodcourt.infrastructure.out.client.dto.OrderReadyNotificationRequestDto;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Infrastructure adapter that calls {@code messaging-service} to send the "order ready" SMS.
 *
 * <ul>
 *   <li>{@code POST /api/v1/notifications/order-ready} — triggers the SMS + PIN notification</li>
 * </ul>
 *
 * The inbound {@code Authorization} header is forwarded via {@link AuthorizationForwardingInterceptor}
 * registered on the {@link RestClient} at construction time.
 */
public class MessagingRestClientAdapter implements IOrderReadyNotificationPort {

    private static final String NOTIFICATION_PATH = "/api/v1/notifications/order-ready";

    private final RestClient restClient;

    public MessagingRestClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void notifyOrderReady(UUID orderId, String phone, String clientName) {
        try {
            restClient.post()
                    .uri(NOTIFICATION_PATH)
                    .body(new OrderReadyNotificationRequestDto(orderId, phone, clientName))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new MessagingServiceUnavailableException();
        }
    }
}
