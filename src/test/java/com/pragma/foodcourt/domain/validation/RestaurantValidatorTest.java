package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidNitException;
import com.pragma.foodcourt.domain.exception.InvalidPhoneFormatException;
import com.pragma.foodcourt.domain.exception.InvalidRestaurantNameException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestaurantValidatorTest {

    private final RestaurantValidator validator = new RestaurantValidator();
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        validRestaurant = new Restaurant(null, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", UUID.randomUUID());
    }

    @Test
    @DisplayName("Accepts a fully valid restaurant")
    void shouldAcceptFullyValidRestaurant() {
        assertDoesNotThrow(() -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Accepts a name that mixes letters and numbers")
    void shouldAcceptAlphanumericName() {
        validRestaurant.setName("Pizza 123");
        assertDoesNotThrow(() -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a missing required field")
    void shouldThrowWhenAddressIsBlank() {
        validRestaurant.setAddress("  ");
        assertThrows(RequiredFieldException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a null owner id")
    void shouldThrowWhenOwnerIdIsNull() {
        validRestaurant.setOwnerId(null);
        assertThrows(RequiredFieldException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a name made only of numbers")
    void shouldThrowWhenNameIsNumericOnly() {
        validRestaurant.setName("12345");
        assertThrows(InvalidRestaurantNameException.class,
                () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a non-numeric NIT")
    void shouldThrowWhenNitIsNonNumeric() {
        validRestaurant.setNit("900-ABC");
        assertThrows(InvalidNitException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a NIT shorter than 9 digits")
    void shouldThrowWhenNitIsTooShort() {
        validRestaurant.setNit("12345678");
        assertThrows(InvalidNitException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a NIT longer than 10 digits")
    void shouldThrowWhenNitIsTooLong() {
        validRestaurant.setNit("12345678901");
        assertThrows(InvalidNitException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Accepts a 10-digit NIT")
    void shouldAcceptTenDigitNit() {
        validRestaurant.setNit("9001234567");
        assertDoesNotThrow(() -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a phone longer than 13 characters")
    void shouldThrowWhenPhoneTooLong() {
        validRestaurant.setPhone("+5730056983255");
        assertThrows(InvalidPhoneFormatException.class,
                () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a phone with non-numeric characters")
    void shouldThrowWhenPhoneIsNonNumeric() {
        validRestaurant.setPhone("300ABC9832");
        assertThrows(InvalidPhoneFormatException.class,
                () -> validator.validateNewRestaurant(validRestaurant));
    }
}
