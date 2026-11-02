package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantRequestMapper {
    Restaurant toRestaurant(CreateRestaurantRequestDto request);
}
