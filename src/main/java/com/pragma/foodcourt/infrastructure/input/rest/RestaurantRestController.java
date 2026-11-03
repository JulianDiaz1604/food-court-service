package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;
import com.pragma.foodcourt.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management endpoints")
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;

    @Operation(summary = "Create a restaurant (administrator only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created"),
            @ApiResponse(responseCode = "400", description = "Invalid restaurant data or owner id"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not an administrator")
    })
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(
            @RequestBody CreateRestaurantRequestDto request) {
        return new ResponseEntity<>(restaurantHandler.createRestaurant(request), HttpStatus.CREATED);
    }
}
