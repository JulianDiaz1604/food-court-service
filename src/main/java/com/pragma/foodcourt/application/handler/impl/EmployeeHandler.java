package com.pragma.foodcourt.application.handler.impl;

import com.pragma.foodcourt.application.dto.request.CreateEmployeeRequestDto;
import com.pragma.foodcourt.application.dto.response.EmployeeResponseDto;
import com.pragma.foodcourt.application.handler.IEmployeeHandler;
import com.pragma.foodcourt.application.mapper.IEmployeeRequestMapper;
import com.pragma.foodcourt.application.mapper.IEmployeeResponseMapper;
import com.pragma.foodcourt.domain.api.IEmployeeServicePort;
import com.pragma.foodcourt.domain.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeHandler implements IEmployeeHandler {

    private final IEmployeeServicePort employeeServicePort;
    private final IEmployeeRequestMapper employeeRequestMapper;
    private final IEmployeeResponseMapper employeeResponseMapper;

    @Override
    public EmployeeResponseDto createEmployee(CreateEmployeeRequestDto request, UUID restaurantId, UUID ownerId) {
        Employee employee = employeeRequestMapper.toEmployee(request);
        employee.setRestaurantId(restaurantId);
        Employee createdEmployee = employeeServicePort.createEmployee(employee, ownerId);
        return employeeResponseMapper.toResponse(createdEmployee);
    }
}
