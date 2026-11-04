package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidOrderStatusException extends DomainException {
    public InvalidOrderStatusException() {
        super(DomainConstants.INVALID_ORDER_STATUS_MESSAGE);
    }
}
