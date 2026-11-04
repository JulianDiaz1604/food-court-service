package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishRequestDto;
import com.pragma.foodcourt.application.dto.request.UpdateDishStatusRequestDto;
import com.pragma.foodcourt.application.dto.response.DishListItemResponseDto;
import com.pragma.foodcourt.application.dto.response.DishResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management endpoints")
public class DishRestController {

    private final IDishHandler dishHandler;

    @Operation(summary = "Create a dish (the owner is taken from the authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created"),
            @ApiResponse(responseCode = "400", description = "Invalid dish data"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Restaurant does not exist")
    })
    @PostMapping
    public ResponseEntity<DishResponseDto> createDish(
            @AuthenticationPrincipal String ownerId,
            @RequestBody CreateDishRequestDto request) {
        return new ResponseEntity<>(
                dishHandler.createDish(request, UUID.fromString(ownerId)), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a dish (only price and description; owner taken from the token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated"),
            @ApiResponse(responseCode = "400", description = "Invalid dish data"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Dish does not exist")
    })
    @PatchMapping("/{dishId}")
    public ResponseEntity<DishResponseDto> updateDish(
            @PathVariable UUID dishId,
            @AuthenticationPrincipal String ownerId,
            @RequestBody UpdateDishRequestDto request) {
        return new ResponseEntity<>(
                dishHandler.updateDish(dishId, request, UUID.fromString(ownerId)), HttpStatus.OK);
    }

    @Operation(summary = "Enable or disable a dish (owner only; owner taken from the token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish status updated"),
            @ApiResponse(responseCode = "400", description = "The active flag is required"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Dish does not exist")
    })
    @PatchMapping("/{dishId}/status")
    public ResponseEntity<DishResponseDto> setDishStatus(
            @PathVariable UUID dishId,
            @AuthenticationPrincipal String ownerId,
            @RequestBody UpdateDishStatusRequestDto request) {
        return new ResponseEntity<>(
                dishHandler.setDishStatus(dishId, request, UUID.fromString(ownerId)), HttpStatus.OK);
    }

    @Operation(summary = "List a restaurant's active menu, paginated and filterable by category (client only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of active dishes for the restaurant"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not a client"),
            @ApiResponse(responseCode = "404", description = "Restaurant does not exist")
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<DishListItemResponseDto>> listDishesByRestaurant(
            @RequestParam UUID restaurantId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                dishHandler.listDishesByRestaurant(restaurantId, category, page, size));
    }
}
