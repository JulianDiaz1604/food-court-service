package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidPaginationException;
import com.pragma.foodcourt.domain.util.DomainConstants;

/**
 * Pure domain validator for pagination parameters. Reused by every paginated listing use case:
 * the page index must be zero-based and non-negative, and the page size must be greater than zero.
 */
public class PaginationValidator {

    public void validate(int page, int size) {
        if (page < DomainConstants.MIN_PAGE_NUMBER || size < DomainConstants.MIN_PAGE_SIZE) {
            throw new InvalidPaginationException();
        }
    }
}
