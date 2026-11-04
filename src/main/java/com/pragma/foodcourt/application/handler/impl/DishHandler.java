package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.CreateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishStatusRequestDto;
import com.pragma.foodcourt.application.dto.response.DishListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IDishHandler;
import com.pragma.foodcourt.application.mapper.IDishRequestMapper;
import com.pragma.foodcourt.application.mapper.IDishResponseMapper;
import com.pragma.foodcourt.domain.api.IDishServicePort;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public DishResponseDto createDish(CreateDishRequestDto request, UUID ownerId) {
        Dish createdDish = dishServicePort.createDish(dishRequestMapper.toDish(request), ownerId);
        return dishResponseMapper.toResponse(createdDish);
    }

    @Override
    public DishResponseDto updateDish(UUID dishId, UpdateDishRequestDto request, UUID ownerId) {
        Dish updatedDish = dishServicePort.updateDish(
                dishId, request.getPrice(), request.getDescription(), ownerId);
        return dishResponseMapper.toResponse(updatedDish);
    }

    @Override
    public DishResponseDto setDishStatus(UUID dishId, UpdateDishStatusRequestDto request, UUID ownerId) {
        Dish updatedDish = dishServicePort.setDishStatus(dishId, request.getActive(), ownerId);
        return dishResponseMapper.toResponse(updatedDish);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<DishListItemResponseDto> listDishesByRestaurant(
            UUID restaurantId, String category, int page, int size) {
        PagedResult<Dish> result =
                dishServicePort.listDishesByRestaurant(restaurantId, category, page, size);
        return new PageResponseDto<>(
                dishResponseMapper.toListItems(result.getContent()),
                result.getPage(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }
}
