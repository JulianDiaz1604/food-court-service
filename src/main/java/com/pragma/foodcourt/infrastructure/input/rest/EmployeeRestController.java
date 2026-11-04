package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.CreateEmployeeRequestDto;
import com.pragma.foodcourt.application.dto.response.EmployeeResponseDto;
import com.pragma.foodcourt.application.handler.IEmployeeHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Restaurant employee management endpoints")
public class EmployeeRestController {

    private final IEmployeeHandler employeeHandler;

    @Operation(summary = "Create an employee for a restaurant (owner of that restaurant only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee account created"),
            @ApiResponse(responseCode = "400", description = "Invalid employee data"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Restaurant does not exist"),
            @ApiResponse(responseCode = "409", description = "Email or document number already registered")
    })
    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @PathVariable UUID restaurantId,
            @AuthenticationPrincipal String ownerId,
            @RequestBody CreateEmployeeRequestDto request) {
        return new ResponseEntity<>(
                employeeHandler.createEmployee(request, restaurantId, UUID.fromString(ownerId)),
                HttpStatus.CREATED);
    }
}
