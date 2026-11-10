package com.pragma.foodcourt.infrastructure.out.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Request body sent to traceability-service {@code POST /api/v1/traces} on each order transition.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordTraceRequestDto {
    private UUID orderId;
    private UUID clientId;
    private UUID restaurantId;
    private String previousStatus;
    private String newStatus;
    private UUID employeeId;
}
