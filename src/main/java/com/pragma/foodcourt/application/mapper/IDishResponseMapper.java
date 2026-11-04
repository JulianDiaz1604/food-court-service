package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.response.DishListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;
import com.pragma.foodcourt.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishResponseMapper {
    DishResponseDto toResponse(Dish dish);

    DishListItemResponseDto toListItem(Dish dish);

    List<DishListItemResponseDto> toListItems(List<Dish> dishes);
}
