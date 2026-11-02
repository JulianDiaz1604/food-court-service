package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidNitException;
import com.pragma.foodcourt.domain.exception.InvalidPhoneFormatException;
import com.pragma.foodcourt.domain.exception.InvalidRestaurantNameException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestaurantValidatorTest {

    private RestaurantValidator validator;
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        validator = new RestaurantValidator();
        validRestaurant = new Restaurant(null, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", 2L);
    }

    @Test
    @DisplayName("Accepts a fully valid restaurant")
    void validateNewRestaurant_withValidData_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Accepts a name that mixes letters and numbers")
    void validateNewRestaurant_withAlphanumericName_doesNotThrow() {
        validRestaurant.setName("Pizza 123");
        assertDoesNotThrow(() -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a missing required field")
    void validateNewRestaurant_withBlankAddress_throwsRequiredFieldException() {
        validRestaurant.setAddress("  ");
        assertThrows(RequiredFieldException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a null owner id")
    void validateNewRestaurant_withNullOwnerId_throwsRequiredFieldException() {
        validRestaurant.setOwnerId(null);
        assertThrows(RequiredFieldException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a name made only of numbers")
    void validateNewRestaurant_withNumericOnlyName_throwsInvalidRestaurantNameException() {
        validRestaurant.setName("12345");
        assertThrows(InvalidRestaurantNameException.class,
                () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a non-numeric NIT")
    void validateNewRestaurant_withNonNumericNit_throwsInvalidNitException() {
        validRestaurant.setNit("900-ABC");
        assertThrows(InvalidNitException.class, () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a phone longer than 13 characters")
    void validateNewRestaurant_withTooLongPhone_throwsInvalidPhoneFormatException() {
        validRestaurant.setPhone("+5730056983255");
        assertThrows(InvalidPhoneFormatException.class,
                () -> validator.validateNewRestaurant(validRestaurant));
    }

    @Test
    @DisplayName("Rejects a phone with non-numeric characters")
    void validateNewRestaurant_withNonNumericPhone_throwsInvalidPhoneFormatException() {
        validRestaurant.setPhone("300ABC9832");
        assertThrows(InvalidPhoneFormatException.class,
                () -> validator.validateNewRestaurant(validRestaurant));
    }
}
