package com.pragma.foodcourt.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for marking an order as DELIVERED (HU15). Carries the security PIN that the client
 * received by SMS and digits to the employee at pick-up.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliverOrderRequestDto {
    private String pin;
}
