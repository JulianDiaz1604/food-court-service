package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;

import java.util.UUID;

public interface IOrderHandler {
    OrderResponseDto placeOrder(CreateOrderRequestDto request, UUID clientId);

    PageResponseDto<OrderResponseDto> listOrdersByStatus(
            UUID employeeId, String status, int page, int size);

    OrderResponseDto assignOrder(UUID orderId, UUID employeeId);
}
