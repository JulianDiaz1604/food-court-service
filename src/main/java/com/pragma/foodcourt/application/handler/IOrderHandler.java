package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;

import java.util.UUID;

public interface IOrderHandler {
    OrderResponseDto placeOrder(CreateOrderRequestDto request, UUID clientId);
}
