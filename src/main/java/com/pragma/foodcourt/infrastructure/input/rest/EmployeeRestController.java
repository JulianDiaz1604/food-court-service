package com.pragma.foodcourt.infrastructure.input.rest;

import com.pragma.foodcourt.application.dto.request.LinkEmployeeRequestDto;
import com.pragma.foodcourt.application.dto.response.LinkEmployeeResponseDto;
import com.pragma.foodcourt.application.handler.IEmployeeHandler;
import com.pragma.foodcourt.infrastructure.configuration.security.AuthenticatedUser;
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

    @Operation(summary = "Link an existing employee to a restaurant (owner of that restaurant only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee linked to restaurant"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Restaurant does not exist"),
            @ApiResponse(responseCode = "409", description = "Employee is already linked to this restaurant")
    })
    @PostMapping
    public ResponseEntity<LinkEmployeeResponseDto> registerEmployee(
            @PathVariable UUID restaurantId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody LinkEmployeeRequestDto request) {
        return new ResponseEntity<>(
                employeeHandler.registerEmployee(request, restaurantId, authenticatedUser.userId()),
                HttpStatus.CREATED);
    }
}
