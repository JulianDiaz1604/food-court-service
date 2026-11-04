package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderResponseMapper {

    OrderResponseDto toResponse(Order order);

    List<OrderResponseDto> toResponseList(List<Order> orders);
}
