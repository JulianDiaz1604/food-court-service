package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.response.EmployeeResponseDto;
import com.pragma.foodcourt.domain.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEmployeeResponseMapper {
    EmployeeResponseDto toResponse(Employee employee);
}
