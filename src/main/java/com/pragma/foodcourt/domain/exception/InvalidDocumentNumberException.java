package com.pragma.foodcourt.domain.exception;

import com.pragma.foodcourt.domain.util.DomainConstants;

public class InvalidDocumentNumberException extends DomainException {
    public InvalidDocumentNumberException() {
        super(DomainConstants.INVALID_DOCUMENT_MESSAGE);
    }
}
