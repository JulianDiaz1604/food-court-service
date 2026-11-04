package com.pragma.foodcourt.infrastructure.out.jpa.mapper;

import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import com.pragma.foodcourt.infrastructure.out.jpa.entity.EmployeeRestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEmployeeRestaurantEntityMapper {

    EmployeeRestaurantEntity toEntity(EmployeeRestaurant employeeRestaurant);

    EmployeeRestaurant toEmployeeRestaurant(EmployeeRestaurantEntity entity);
}
