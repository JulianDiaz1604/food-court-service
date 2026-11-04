package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidDocumentNumberException;
import com.pragma.foodcourt.domain.exception.InvalidEmailFormatException;
import com.pragma.foodcourt.domain.exception.InvalidPhoneFormatException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Employee;
import com.pragma.foodcourt.domain.util.DomainConstants;

public class EmployeeValidator {

    public void validateNewEmployee(Employee employee) {
        validateRequiredFields(employee);
        validateEmailFormat(employee.getEmail());
        validatePhoneFormat(employee.getPhone());
        validateDocumentNumber(employee.getDocumentNumber());
    }

    private void validateRequiredFields(Employee employee) {
        if (isBlank(employee.getName())
                || isBlank(employee.getLastName())
                || isBlank(employee.getDocumentNumber())
                || isBlank(employee.getPhone())
                || isBlank(employee.getEmail())
                || isBlank(employee.getPassword())
                || employee.getRestaurantId() == null) {
            throw new RequiredFieldException();
        }
    }

    private void validateEmailFormat(String email) {
        if (!email.matches(DomainConstants.EMAIL_REGEX)) {
            throw new InvalidEmailFormatException();
        }
    }

    private void validatePhoneFormat(String phone) {
        if (phone.length() > DomainConstants.PHONE_MAX_LENGTH || !phone.matches(DomainConstants.PHONE_REGEX)) {
            throw new InvalidPhoneFormatException();
        }
    }

    private void validateDocumentNumber(String documentNumber) {
        if (!documentNumber.matches(DomainConstants.DOCUMENT_REGEX)) {
            throw new InvalidDocumentNumberException();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
