package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidPaginationException extends DomainException {
    public InvalidPaginationException() {
        super(DomainConstants.INVALID_PAGINATION_MESSAGE);
    }
}
