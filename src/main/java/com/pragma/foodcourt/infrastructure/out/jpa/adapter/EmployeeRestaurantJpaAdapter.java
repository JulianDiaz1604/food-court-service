package com.pragma.foodcourt.infrastructure.out.jpa.adapter;

import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.infrastructure.out.jpa.entity.EmployeeRestaurantEntity;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IEmployeeRestaurantEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IEmployeeRestaurantRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class EmployeeRestaurantJpaAdapter implements IEmployeePersistencePort {

    private final IEmployeeRestaurantRepository employeeRestaurantRepository;
    private final IEmployeeRestaurantEntityMapper employeeRestaurantEntityMapper;

    @Override
    public EmployeeRestaurant saveEmployeeRestaurant(EmployeeRestaurant employeeRestaurant) {
        EmployeeRestaurantEntity savedEntity = employeeRestaurantRepository.save(
                employeeRestaurantEntityMapper.toEntity(employeeRestaurant));
        return employeeRestaurantEntityMapper.toEmployeeRestaurant(savedEntity);
    }

    @Override
    public UUID findRestaurantIdByEmployee(UUID employeeId) {
        return employeeRestaurantRepository.findByEmployeeId(employeeId)
                .map(EmployeeRestaurantEntity::getRestaurantId)
                .orElse(null);
    }

    @Override
    public boolean existsLink(UUID employeeId, UUID restaurantId) {
        return employeeRestaurantRepository.existsByEmployeeIdAndRestaurantId(employeeId, restaurantId);
    }
}
