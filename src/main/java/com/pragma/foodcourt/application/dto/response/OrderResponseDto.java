package com.pragma.foodcourt.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private UUID id;
    private UUID clientId;
    private UUID restaurantId;
    private String status;
    private List<OrderItemResponseDto> items;
}
