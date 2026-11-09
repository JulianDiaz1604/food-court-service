package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.LinkEmployeeRequestDto;
import com.pragma.foodcourt.application.dto.response.LinkEmployeeResponseDto;

import java.util.UUID;

public interface IEmployeeHandler {
    LinkEmployeeResponseDto registerEmployee(LinkEmployeeRequestDto request, UUID restaurantId, UUID ownerId);
}
