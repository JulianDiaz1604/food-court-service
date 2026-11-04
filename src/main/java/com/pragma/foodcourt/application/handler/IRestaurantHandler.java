package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request);

    PageResponseDto<RestaurantListItemResponseDto> listRestaurants(int page, int size);
}
