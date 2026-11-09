package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.application.dto.response.OwnerIdResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;

import java.util.UUID;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request);

    PageResponseDto<RestaurantListItemResponseDto> listRestaurants(int page, int size);

    /**
     * Returns the ownerId of the given restaurant.
     *
     * @param restaurantId the target restaurant.
     * @return {@link OwnerIdResponseDto} carrying restaurantId and ownerId.
     */
    OwnerIdResponseDto getOwnerId(UUID restaurantId);
}
