package com.pragma.foodcourt.infrastructure.out.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body sent to messaging-service {@code POST /api/v1/notifications/{orderId}/validate}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidatePinRequestDto {
    private String pin;
}
