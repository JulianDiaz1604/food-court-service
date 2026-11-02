package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateDishRequestDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;

public interface IDishHandler {
    DishResponseDto createDish(CreateDishRequestDto request);
}
