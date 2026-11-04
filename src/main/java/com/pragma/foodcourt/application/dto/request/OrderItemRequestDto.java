package com.pragma.foodcourt.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItemRequestDto {
    private UUID dishId;
    private Integer quantity;
}
