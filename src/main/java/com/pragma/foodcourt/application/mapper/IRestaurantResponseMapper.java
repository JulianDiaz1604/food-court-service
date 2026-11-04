package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.response.RestaurantListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;
import com.pragma.foodcourt.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantResponseMapper {
    RestaurantResponseDto toResponse(Restaurant restaurant);

    RestaurantListItemResponseDto toListItem(Restaurant restaurant);

    List<RestaurantListItemResponseDto> toListItems(List<Restaurant> restaurants);
}
