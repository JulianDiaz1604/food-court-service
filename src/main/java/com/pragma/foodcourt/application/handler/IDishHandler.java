package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishRequestDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;

import java.util.UUID;

public interface IDishHandler {
    DishResponseDto createDish(CreateDishRequestDto request);

    DishResponseDto updateDish(UUID dishId, UpdateDishRequestDto request);
}
