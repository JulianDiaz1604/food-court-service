package com.pragma.foodcourt.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequestDto {
    private UUID restaurantId;
    private List<OrderItemRequestDto> items;
}
