package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishStatusRequestDto;
import com.pragma.foodcourt.application.dto.response.DishListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;

import java.util.UUID;

public interface IDishHandler {
    DishResponseDto createDish(CreateDishRequestDto request, UUID ownerId);

    DishResponseDto updateDish(UUID dishId, UpdateDishRequestDto request, UUID ownerId);

    DishResponseDto setDishStatus(UUID dishId, UpdateDishStatusRequestDto request, UUID ownerId);

    PageResponseDto<DishListItemResponseDto> listDishesByRestaurant(
            UUID restaurantId, String category, int page, int size);
}
