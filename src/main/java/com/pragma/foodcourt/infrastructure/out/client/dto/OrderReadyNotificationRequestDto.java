package com.pragma.foodcourt.infrastructure.out.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Request body sent to messaging-service {@code POST /api/v1/notifications/order-ready}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadyNotificationRequestDto {
    private UUID orderId;
    private String phone;
    private String clientName;
}
