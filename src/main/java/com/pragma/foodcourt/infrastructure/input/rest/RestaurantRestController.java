package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateRestaurantRequestDto;
import com.pragma.foodcourt.application.dto.response.OwnerIdResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.RestaurantResponseDto;
import com.pragma.foodcourt.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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

    @Operation(summary = "List restaurants ordered alphabetically, paginated (client only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of restaurants (name and logo URL)"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not a client")
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<RestaurantListItemResponseDto>> listRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(restaurantHandler.listRestaurants(page, size));
    }

    @Operation(summary = "Get the owner id of a restaurant (any authenticated user)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owner id returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "404", description = "Restaurant does not exist")
    })
    @GetMapping("/{restaurantId}/owner")
    public ResponseEntity<OwnerIdResponseDto> getRestaurantOwner(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(restaurantHandler.getOwnerId(restaurantId));
    }
}
