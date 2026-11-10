package com.pragma.foodcourt.infrastructure.out.client;

import com.pragma.foodcourt.domain.exception.MessagingServiceUnavailableException;
import com.pragma.foodcourt.domain.spi.IOrderDeliveryPinPort;
import com.pragma.foodcourt.domain.spi.IOrderReadyNotificationPort;
import com.pragma.foodcourt.infrastructure.out.client.dto.OrderReadyNotificationRequestDto;
import com.pragma.foodcourt.infrastructure.out.client.dto.ValidatePinRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Infrastructure adapter that calls {@code messaging-service}.
 *
 * <ul>
 *   <li>{@code POST /api/v1/notifications/order-ready} — triggers the SMS + PIN notification (HU14)</li>
 *   <li>{@code POST /api/v1/notifications/{orderId}/validate} — validates the delivery PIN (HU15)</li>
 * </ul>
 *
 * The inbound {@code Authorization} header is forwarded via {@link AuthorizationForwardingInterceptor}
 * registered on the {@link RestClient} at construction time.
 */
public class MessagingRestClientAdapter implements IOrderReadyNotificationPort, IOrderDeliveryPinPort {

    private static final String NOTIFICATION_PATH = "/api/v1/notifications/order-ready";
    private static final String VALIDATE_PIN_PATH = "/api/v1/notifications/{orderId}/validate";

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

    @Override
    public boolean isPinValid(UUID orderId, String pin) {
        try {
            restClient.post()
                    .uri(VALIDATE_PIN_PATH, orderId)
                    .body(new ValidatePinRequestDto(pin))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException exception) {
            // messaging-service answers 403 when the PIN does not match; any other 4xx is unexpected.
            if (exception.getStatusCode() == HttpStatus.FORBIDDEN) {
                return false;
            }
            throw new MessagingServiceUnavailableException();
        } catch (RestClientException exception) {
            throw new MessagingServiceUnavailableException();
        }
    }
}
