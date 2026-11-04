package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.EmployeeAlreadyExistsException;
import com.pragma.foodcourt.domain.exception.InvalidEmailFormatException;
import com.pragma.foodcourt.domain.exception.NotRestaurantOwnerException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Employee;
import com.pragma.foodcourt.domain.model.EmployeeRestaurant;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.spi.IUserCreationPort;
import com.pragma.foodcourt.domain.validation.EmployeeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeUseCaseTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID CREATED_EMPLOYEE_ID = UUID.randomUUID();

    @Mock
    private IUserCreationPort userCreationPort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IEmployeePersistencePort employeePersistencePort;

    private EmployeeUseCase employeeUseCase;
    private Employee validEmployee;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        employeeUseCase = new EmployeeUseCase(
                userCreationPort, restaurantPersistencePort, employeePersistencePort, new EmployeeValidator());
        validEmployee = new Employee(null, "Luis", "Perez", "987654321", "+573001112233",
                "luis.perez@example.com", "plainPassword", RESTAURANT_ID);
        restaurant = new Restaurant(RESTAURANT_ID, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", OWNER_ID);
    }

    @Test
    @DisplayName("Creates the user and links it to the owner's restaurant")
    void shouldCreateEmployeeAndLinkToRestaurant() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userCreationPort.createEmployee(validEmployee)).thenReturn(CREATED_EMPLOYEE_ID);

        Employee result = employeeUseCase.createEmployee(validEmployee, OWNER_ID);

        ArgumentCaptor<EmployeeRestaurant> captor = ArgumentCaptor.forClass(EmployeeRestaurant.class);
        verify(employeePersistencePort).saveEmployeeRestaurant(captor.capture());
        EmployeeRestaurant link = captor.getValue();
        assertEquals(CREATED_EMPLOYEE_ID, link.getEmployeeId());
        assertEquals(RESTAURANT_ID, link.getRestaurantId());
        assertEquals(CREATED_EMPLOYEE_ID, result.getId());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("Rejects when the restaurant does not exist")
    void shouldThrowWhenRestaurantNotFound() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class,
                () -> employeeUseCase.createEmployee(validEmployee, OWNER_ID));
        verify(userCreationPort, never()).createEmployee(any());
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }

    @Test
    @DisplayName("Rejects when the requester is not the owner of the restaurant")
    void shouldThrowWhenRequesterIsNotRestaurantOwner() {
        UUID anotherOwnerId = UUID.randomUUID();
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        assertThrows(NotRestaurantOwnerException.class,
                () -> employeeUseCase.createEmployee(validEmployee, anotherOwnerId));
        verify(userCreationPort, never()).createEmployee(any());
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }

    @Test
    @DisplayName("Does not touch persistence or the users-service when field validation fails")
    void shouldNotReachCollaboratorsWhenValidationFails() {
        validEmployee.setEmail("not-an-email");

        assertThrows(InvalidEmailFormatException.class,
                () -> employeeUseCase.createEmployee(validEmployee, OWNER_ID));
        verify(restaurantPersistencePort, never()).findRestaurantById(any());
        verify(userCreationPort, never()).createEmployee(any());
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }

    @Test
    @DisplayName("Propagates a conflict when the users-service rejects a duplicate employee")
    void shouldPropagateConflictFromUsersService() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userCreationPort.createEmployee(validEmployee)).thenThrow(new EmployeeAlreadyExistsException());

        assertThrows(EmployeeAlreadyExistsException.class,
                () -> employeeUseCase.createEmployee(validEmployee, OWNER_ID));
        verify(employeePersistencePort, never()).saveEmployeeRestaurant(any());
    }
}
