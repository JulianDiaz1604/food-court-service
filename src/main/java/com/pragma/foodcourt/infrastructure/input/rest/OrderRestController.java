package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IOrderHandler;
import com.pragma.foodcourt.infrastructure.configuration.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody CreateOrderRequestDto request) {
        return new ResponseEntity<>(
                orderHandler.placeOrder(request, authenticatedUser.userId()), HttpStatus.CREATED);
    }

    @Operation(summary = "List the orders of the employee's restaurant filtered by status, paginated "
            + "(employee only; the employee is taken from the authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of orders for the employee's restaurant"),
            @ApiResponse(responseCode = "400", description = "Invalid status or pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not an employee"),
            @ApiResponse(responseCode = "404", description = "The employee is not assigned to any restaurant")
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<OrderResponseDto>> listOrdersByStatus(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                orderHandler.listOrdersByStatus(authenticatedUser.userId(), status, page, size));
    }
}
