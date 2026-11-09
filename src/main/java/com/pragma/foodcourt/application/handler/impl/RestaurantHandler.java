package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.application.dto.response.OwnerIdResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;
import com.pragma.foodcourt.application.handler.IRestaurantHandler;
import com.pragma.foodcourt.application.mapper.IRestaurantRequestMapper;
import com.pragma.foodcourt.application.mapper.IRestaurantResponseMapper;
import com.pragma.foodcourt.domain.api.IRestaurantServicePort;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request) {
        Restaurant createdRestaurant = restaurantServicePort.createRestaurant(
                restaurantRequestMapper.toRestaurant(request));
        return restaurantResponseMapper.toResponse(createdRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<RestaurantListItemResponseDto> listRestaurants(int page, int size) {
        PagedResult<Restaurant> result = restaurantServicePort.listRestaurants(page, size);
        return new PageResponseDto<>(
                restaurantResponseMapper.toListItems(result.getContent()),
                result.getPage(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerIdResponseDto getOwnerId(UUID restaurantId) {
        UUID ownerId = restaurantServicePort.findOwnerId(restaurantId);
        return new OwnerIdResponseDto(restaurantId, ownerId);
    }
}
