package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateOrderRequestDto;
import com.pragma.foodcourt.application.dto.request.DeliverOrderRequestDto;
import com.pragma.foodcourt.application.dto.response.OrderResponseDto;
import com.pragma.foodcourt.application.dto.response.PageResponseDto;
import com.pragma.foodcourt.application.handler.IOrderHandler;
import com.pragma.foodcourt.infrastructure.configuration.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
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

    @Operation(summary = "Assign the authenticated employee to a PENDING order and move it to "
            + "IN_PREPARATION (employee only; the employee is taken from the authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order assigned and moved to IN_PREPARATION"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not an employee or the order "
                    + "belongs to another restaurant"),
            @ApiResponse(responseCode = "404", description = "The order does not exist or the employee is "
                    + "not assigned to any restaurant"),
            @ApiResponse(responseCode = "409", description = "The order is not in PENDING status")
    })
    @PatchMapping("/{orderId}/assign")
    public ResponseEntity<OrderResponseDto> assignOrder(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderHandler.assignOrder(orderId, authenticatedUser.userId()));
    }

    @Operation(summary = "Mark an IN_PREPARATION order as READY and notify the client by SMS "
            + "(employee only; the employee is taken from the authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order marked as READY and client notified"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not an employee or the order "
                    + "belongs to another restaurant"),
            @ApiResponse(responseCode = "404", description = "The order does not exist, the employee is "
                    + "not assigned to any restaurant, or the client account was not found"),
            @ApiResponse(responseCode = "409", description = "The order is not in IN_PREPARATION status"),
            @ApiResponse(responseCode = "502", description = "users-service or messaging-service is "
                    + "unavailable; the order remains in IN_PREPARATION and the caller may retry")
    })
    @PatchMapping("/{orderId}/ready")
    public ResponseEntity<OrderResponseDto> markOrderReady(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderHandler.markOrderReady(orderId, authenticatedUser.userId()));
    }

    @Operation(summary = "Mark a READY order as DELIVERED after validating the client's security PIN "
            + "(employee only; the employee is taken from the authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order marked as DELIVERED"),
            @ApiResponse(responseCode = "400", description = "The security PIN is missing"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not an employee, the order "
                    + "belongs to another restaurant, or the security PIN does not match"),
            @ApiResponse(responseCode = "404", description = "The order does not exist or the employee is "
                    + "not assigned to any restaurant"),
            @ApiResponse(responseCode = "409", description = "The order is not in READY status"),
            @ApiResponse(responseCode = "502", description = "messaging-service is unavailable; the order "
                    + "remains in READY and the caller may retry")
    })
    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponseDto> markOrderDelivered(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID orderId,
            @RequestBody DeliverOrderRequestDto request) {
        return ResponseEntity.ok(
                orderHandler.markOrderDelivered(orderId, authenticatedUser.userId(), request.getPin()));
    }

    @Operation(summary = "Cancel a PENDING order (client only; the client is taken from the "
            + "authenticated token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not a client or the order "
                    + "belongs to another client"),
            @ApiResponse(responseCode = "404", description = "The order does not exist"),
            @ApiResponse(responseCode = "409", description = "The order is not in PENDING status and "
                    + "can no longer be cancelled")
    })
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderHandler.cancelOrder(orderId, authenticatedUser.userId()));
    }
}
