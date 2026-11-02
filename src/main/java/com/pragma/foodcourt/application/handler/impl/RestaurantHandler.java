package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;
import com.pragma.foodcourt.application.handler.IRestaurantHandler;
import com.pragma.foodcourt.application.mapper.IRestaurantRequestMapper;
import com.pragma.foodcourt.application.mapper.IRestaurantResponseMapper;
import com.pragma.foodcourt.domain.api.IRestaurantServicePort;
import com.pragma.foodcourt.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
