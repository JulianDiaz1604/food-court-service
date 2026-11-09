package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.EmployeeLinkAlreadyExistsException;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeUseCaseTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID EMPLOYEE_ID = UUID.randomUUID();

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IEmployeePersistencePort employeePersistencePort;

    private EmployeeUseCase employeeUseCase;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        employeeUseCase = new EmployeeUseCase(restaurantPersistencePort, employeePersistencePort);
        restaurant = new Restaurant(RESTAURANT_ID, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", OWNER_ID);
    }

    @Test
    @DisplayName("Saves the employee-restaurant link when restaurant exists and owner matches (EAC-S01)")
    void shouldSaveLinkWhenRestaurantExistsAndOwnerMatches() {
        EmployeeRestaurant savedLink = new EmployeeRestaurant(UUID.randomUUID(), EMPLOYEE_ID, RESTAURANT_ID);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(employeePersistencePort.existsLink(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(false);
        when(employeePersistencePort.saveEmployeeRestaurant(any(EmployeeRestaurant.class)))
                .thenReturn(savedLink);

        EmployeeRestaurant result = employeeUseCase.registerEmployee(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID);

        ArgumentCaptor<EmployeeRestaurant> captor = ArgumentCaptor.forClass(EmployeeRestaurant.class);
        verify(employeePersistencePort).saveEmployeeRestaurant(captor.capture());
        EmployeeRestaurant captured = captor.getValue();
        assertEquals(EMPLOYEE_ID, captured.getEmployeeId());
        assertEquals(RESTAURANT_ID, captured.getRestaurantId());
        assertEquals(EMPLOYEE_ID, result.getEmployeeId());
        assertEquals(RESTAURANT_ID, result.getRestaurantId());
    }

    @Test
    @DisplayName("Throws RestaurantNotFoundException when the restaurant does not exist")
    void shouldThrowWhenRestaurantNotFoundOnRegisterEmployee() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class,
                () -> employeeUseCase.registerEmployee(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID));
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }

    @Test
    @DisplayName("Throws NotRestaurantOwnerException when requester does not own the restaurant (EAC-S08)")
    void shouldThrowWhenRequesterIsNotOwnerOnRegisterEmployee() {
        UUID anotherOwnerId = UUID.randomUUID();
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        assertThrows(NotRestaurantOwnerException.class,
                () -> employeeUseCase.registerEmployee(EMPLOYEE_ID, RESTAURANT_ID, anotherOwnerId));
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }

    @Test
    @DisplayName("Throws EmployeeLinkAlreadyExistsException when the link already exists (409)")
    void shouldThrowWhenEmployeeLinkAlreadyExists() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(employeePersistencePort.existsLink(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);

        assertThrows(EmployeeLinkAlreadyExistsException.class,
                () -> employeeUseCase.registerEmployee(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID));
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }
}
