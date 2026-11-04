package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidPaginationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaginationValidatorTest {

    private PaginationValidator paginationValidator;

    @BeforeEach
    void setUp() {
        paginationValidator = new PaginationValidator();
    }

    @Test
    @DisplayName("Accepts a zero-based page and a positive size")
    void shouldAcceptValidPagination() {
        assertDoesNotThrow(() -> paginationValidator.validate(0, 10));
    }

    @Test
    @DisplayName("Rejects a negative page number")
    void shouldRejectNegativePage() {
        assertThrows(InvalidPaginationException.class, () -> paginationValidator.validate(-1, 10));
    }

    @Test
    @DisplayName("Rejects a page size of zero")
    void shouldRejectZeroSize() {
        assertThrows(InvalidPaginationException.class, () -> paginationValidator.validate(0, 0));
    }

    @Test
    @DisplayName("Rejects a negative page size")
    void shouldRejectNegativeSize() {
        assertThrows(InvalidPaginationException.class, () -> paginationValidator.validate(0, -5));
    }
}
