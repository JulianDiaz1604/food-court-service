package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IOrderHandler;
import com.pragma.foodcourt.application.mapper.IOrderRequestMapper;
import com.pragma.foodcourt.application.mapper.IOrderResponseMapper;
import com.pragma.foodcourt.domain.api.IOrderQueryServicePort;
import com.pragma.foodcourt.domain.api.IOrderStatusServicePort;
import com.pragma.foodcourt.domain.api.IPlaceOrderServicePort;
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

    private final IPlaceOrderServicePort placeOrderServicePort;
    private final IOrderQueryServicePort orderQueryServicePort;
    private final IOrderStatusServicePort orderStatusServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public OrderResponseDto placeOrder(CreateOrderRequestDto request, UUID clientId) {
        Order placedOrder = placeOrderServicePort.placeOrder(orderRequestMapper.toOrder(request), clientId);
        return orderResponseMapper.toResponse(placedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<OrderResponseDto> listOrdersByStatus(
            UUID employeeId, String status, int page, int size) {
        PagedResult<Order> result =
                orderQueryServicePort.listOrdersByStatus(employeeId, status, page, size);
        return new PageResponseDto<>(
                orderResponseMapper.toResponseList(result.getContent()),
                result.getPage(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    public OrderResponseDto assignOrder(UUID orderId, UUID employeeId) {
        Order assignedOrder = orderStatusServicePort.assignOrder(orderId, employeeId);
        return orderResponseMapper.toResponse(assignedOrder);
    }

    @Override
    public OrderResponseDto markOrderReady(UUID orderId, UUID employeeId) {
        Order readyOrder = orderStatusServicePort.markOrderReady(orderId, employeeId);
        return orderResponseMapper.toResponse(readyOrder);
    }

    @Override
    public OrderResponseDto markOrderDelivered(UUID orderId, UUID employeeId, String pin) {
        Order deliveredOrder = orderStatusServicePort.markOrderDelivered(orderId, employeeId, pin);
        return orderResponseMapper.toResponse(deliveredOrder);
    }
}
