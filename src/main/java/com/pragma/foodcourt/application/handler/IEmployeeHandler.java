package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.CreateEmployeeRequestDto;
import com.pragma.foodcourt.application.dto.response.EmployeeResponseDto;

import java.util.UUID;

public interface IEmployeeHandler {
    EmployeeResponseDto createEmployee(CreateEmployeeRequestDto request, UUID restaurantId, UUID ownerId);
}
