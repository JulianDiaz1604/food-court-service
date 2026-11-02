package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidPriceException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Dish;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DishValidatorTest {

    private final DishValidator validator = new DishValidator();
    private Dish validDish;

    @BeforeEach
    void setUp() {
        validDish = new Dish(null, "Bandeja Paisa", 25000,
                "Traditional dish", "http://img/bandeja.png", "Main course",
                UUID.randomUUID(), false);
    }

    @Test
    @DisplayName("Accepts a fully valid dish")
    void shouldAcceptFullyValidDish() {
        assertDoesNotThrow(() -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a blank name")
    void shouldThrowWhenNameIsBlank() {
        validDish.setName("  ");
        assertThrows(RequiredFieldException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a blank description")
    void shouldThrowWhenDescriptionIsBlank() {
        validDish.setDescription("");
        assertThrows(RequiredFieldException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a blank image url")
    void shouldThrowWhenImageUrlIsBlank() {
        validDish.setImageUrl(null);
        assertThrows(RequiredFieldException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a blank category")
    void shouldThrowWhenCategoryIsBlank() {
        validDish.setCategory("  ");
        assertThrows(RequiredFieldException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a null restaurant id")
    void shouldThrowWhenRestaurantIdIsNull() {
        validDish.setRestaurantId(null);
        assertThrows(RequiredFieldException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a null price")
    void shouldThrowWhenPriceIsNull() {
        validDish.setPrice(null);
        assertThrows(RequiredFieldException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a zero price")
    void shouldThrowWhenPriceIsZero() {
        validDish.setPrice(0);
        assertThrows(InvalidPriceException.class, () -> validator.validateNewDish(validDish));
    }

    @Test
    @DisplayName("Rejects a negative price")
    void shouldThrowWhenPriceIsNegative() {
        validDish.setPrice(-10);
        assertThrows(InvalidPriceException.class, () -> validator.validateNewDish(validDish));
    }

    @Nested
    @DisplayName("validateDishUpdate")
    class ValidateDishUpdate {

        @Test
        @DisplayName("Accepts a positive price and a non-blank description")
        void shouldAcceptValidUpdate() {
            assertDoesNotThrow(() -> validator.validateDishUpdate(30000, "New description"));
        }

        @Test
        @DisplayName("Rejects a null price")
        void shouldThrowWhenPriceIsNull() {
            assertThrows(RequiredFieldException.class,
                    () -> validator.validateDishUpdate(null, "New description"));
        }

        @Test
        @DisplayName("Rejects a blank description")
        void shouldThrowWhenDescriptionIsBlank() {
            assertThrows(RequiredFieldException.class,
                    () -> validator.validateDishUpdate(30000, "   "));
        }

        @Test
        @DisplayName("Rejects a null description")
        void shouldThrowWhenDescriptionIsNull() {
            assertThrows(RequiredFieldException.class,
                    () -> validator.validateDishUpdate(30000, null));
        }

        @Test
        @DisplayName("Rejects a zero price")
        void shouldThrowWhenPriceIsZero() {
            assertThrows(InvalidPriceException.class,
                    () -> validator.validateDishUpdate(0, "New description"));
        }

        @Test
        @DisplayName("Rejects a negative price")
        void shouldThrowWhenPriceIsNegative() {
            assertThrows(InvalidPriceException.class,
                    () -> validator.validateDishUpdate(-5, "New description"));
        }
    }
}
