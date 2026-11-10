package com.pragma.foodcourt.domain.service;

import com.pragma.foodcourt.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeRestaurantResolverTest {

    private static final UUID EMPLOYEE_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @Mock
    private IEmployeePersistencePort employeePersistencePort;

    private EmployeeRestaurantResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new EmployeeRestaurantResolver(employeePersistencePort);
    }

    @Test
    @DisplayName("Returns the restaurant id for a linked employee")
    void shouldReturnRestaurantIdForLinkedEmployee() {
        when(employeePersistencePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);

        UUID result = resolver.resolveFor(EMPLOYEE_ID);

        assertEquals(RESTAURANT_ID, result);
    }

    @Test
    @DisplayName("Throws EmployeeNotLinkedToRestaurantException when employee has no restaurant")
    void shouldThrowWhenEmployeeHasNoLinkedRestaurant() {
        when(employeePersistencePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(null);

        assertThrows(EmployeeNotLinkedToRestaurantException.class,
                () -> resolver.resolveFor(EMPLOYEE_ID));
    }
}
