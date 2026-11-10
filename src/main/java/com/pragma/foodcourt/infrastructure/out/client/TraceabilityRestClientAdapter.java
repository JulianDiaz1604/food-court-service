package com.pragma.foodcourt.infrastructure.out.client;

import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.spi.IOrderTraceabilityPort;
import com.pragma.foodcourt.infrastructure.out.client.dto.RecordTraceRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Infrastructure adapter that records order status changes in {@code traceability-service} (HU17).
 *
 * <ul>
 *   <li>{@code POST /api/v1/traces} — stores one status-change record</li>
 * </ul>
 *
 * Recording is <strong>best-effort</strong>: a traceability failure is logged and swallowed so it
 * never breaks the order lifecycle. The inbound {@code Authorization} header is forwarded via
 * {@link AuthorizationForwardingInterceptor} registered on the {@link RestClient}.
 */
public class TraceabilityRestClientAdapter implements IOrderTraceabilityPort {

    private static final Logger log = LoggerFactory.getLogger(TraceabilityRestClientAdapter.class);
    private static final String TRACES_PATH = "/api/v1/traces";

    private final RestClient restClient;

    public TraceabilityRestClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void recordStatusChange(UUID orderId, UUID clientId, OrderStatus previousStatus,
                                   OrderStatus newStatus, UUID employeeId) {
        RecordTraceRequestDto body = new RecordTraceRequestDto(
                orderId, clientId, name(previousStatus), name(newStatus), employeeId);
        try {
            restClient.post()
                    .uri(TRACES_PATH)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            // Best-effort: never block the order lifecycle because traceability is unavailable.
            log.warn("Could not record traceability for order {} ({} -> {}): {}",
                    orderId, name(previousStatus), name(newStatus), exception.getMessage());
        }
    }

    private String name(OrderStatus status) {
        return status != null ? status.name() : null;
    }
}
