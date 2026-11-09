package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.LinkEmployeeRequestDto;
import com.pragma.foodcourt.application.dto.response.LinkEmployeeResponseDto;
import com.pragma.foodcourt.application.handler.IEmployeeHandler;
import com.pragma.foodcourt.domain.api.IEmployeeServicePort;
import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeHandler implements IEmployeeHandler {

    private final IEmployeeServicePort employeeServicePort;

    @Override
    public LinkEmployeeResponseDto registerEmployee(LinkEmployeeRequestDto request,
                                                    UUID restaurantId,
                                                    UUID ownerId) {
        EmployeeRestaurant link = employeeServicePort.registerEmployee(
                request.getEmployeeId(), restaurantId, ownerId);
        return new LinkEmployeeResponseDto(link.getEmployeeId(), link.getRestaurantId());
    }
}
