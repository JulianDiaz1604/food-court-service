package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IOrderHandler;
import com.pragma.foodcourt.application.mapper.IOrderRequestMapper;
import com.pragma.foodcourt.application.mapper.IOrderResponseMapper;
import com.pragma.foodcourt.domain.api.IOrderServicePort;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public OrderResponseDto placeOrder(CreateOrderRequestDto request, UUID clientId) {
        Order placedOrder = orderServicePort.placeOrder(orderRequestMapper.toOrder(request), clientId);
        return orderResponseMapper.toResponse(placedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<OrderResponseDto> listOrdersByStatus(
            UUID employeeId, String status, int page, int size) {
        PagedResult<Order> result =
                orderServicePort.listOrdersByStatus(employeeId, status, page, size);
        return new PageResponseDto<>(
                orderResponseMapper.toResponseList(result.getContent()),
                result.getPage(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }
}
