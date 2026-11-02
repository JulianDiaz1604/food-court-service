package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.response.DishResponseDto;
import com.pragma.foodcourt.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishResponseMapper {
    DishResponseDto toResponse(Dish dish);
}
