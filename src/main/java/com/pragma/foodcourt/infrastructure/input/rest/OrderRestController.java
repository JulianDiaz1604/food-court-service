package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @Operation(summary = "Place an order (client only; the client is taken from the authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed in PENDING status"),
            @ApiResponse(responseCode = "400", description = "Invalid order data"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not a client"),
            @ApiResponse(responseCode = "404", description = "Restaurant or a dish does not exist"),
            @ApiResponse(responseCode = "409", description = "The client already has an order in progress")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @AuthenticationPrincipal String clientId,
            @RequestBody CreateOrderRequestDto request) {
        return new ResponseEntity<>(
                orderHandler.placeOrder(request, UUID.fromString(clientId)), HttpStatus.CREATED);
    }
}
