package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidDocumentNumberException;
import com.pragma.foodcourt.domain.exception.InvalidEmailFormatException;
import com.pragma.foodcourt.domain.exception.InvalidPhoneFormatException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeValidatorTest {

    private final EmployeeValidator employeeValidator = new EmployeeValidator();
    private Employee validEmployee;

    @BeforeEach
    void setUp() {
        validEmployee = new Employee(null, "Luis", "Perez", "987654321", "+573001112233",
                "luis.perez@example.com", "plainPassword", UUID.randomUUID());
    }

    @Test
    @DisplayName("Accepts a valid employee")
    void shouldAcceptValidEmployee() {
        assertDoesNotThrow(() -> employeeValidator.validateNewEmployee(validEmployee));
    }

    @Test
    @DisplayName("Rejects a blank required field")
    void shouldThrowWhenRequiredFieldIsBlank() {
        validEmployee.setName("  ");
        assertThrows(RequiredFieldException.class, () -> employeeValidator.validateNewEmployee(validEmployee));
    }

    @Test
    @DisplayName("Rejects a null restaurant id")
    void shouldThrowWhenRestaurantIdIsNull() {
        validEmployee.setRestaurantId(null);
        assertThrows(RequiredFieldException.class, () -> employeeValidator.validateNewEmployee(validEmployee));
    }

    @Test
    @DisplayName("Rejects an invalid email")
    void shouldThrowWhenEmailIsInvalid() {
        validEmployee.setEmail("not-an-email");
        assertThrows(InvalidEmailFormatException.class, () -> employeeValidator.validateNewEmployee(validEmployee));
    }

    @Test
    @DisplayName("Rejects a phone longer than 13 characters")
    void shouldThrowWhenPhoneTooLong() {
        validEmployee.setPhone("+5730011122334");
        assertThrows(InvalidPhoneFormatException.class, () -> employeeValidator.validateNewEmployee(validEmployee));
    }

    @Test
    @DisplayName("Rejects a non-numeric document number")
    void shouldThrowWhenDocumentIsNonNumeric() {
        validEmployee.setDocumentNumber("ABC123");
        assertThrows(InvalidDocumentNumberException.class, () -> employeeValidator.validateNewEmployee(validEmployee));
    }
}
