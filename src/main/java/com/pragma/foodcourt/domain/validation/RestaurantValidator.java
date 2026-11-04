package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidNitException;
import com.pragma.foodcourt.domain.exception.InvalidPhoneFormatException;
import com.pragma.foodcourt.domain.exception.InvalidRestaurantNameException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.util.DomainConstants;

public class RestaurantValidator {

    public void validateNewRestaurant(Restaurant restaurant) {
        validateRequiredFields(restaurant);
        validateName(restaurant.getName());
        validateNit(restaurant.getNit());
        validatePhone(restaurant.getPhone());
    }

    private void validateRequiredFields(Restaurant restaurant) {
        if (isBlank(restaurant.getName())
                || isBlank(restaurant.getNit())
                || isBlank(restaurant.getAddress())
                || isBlank(restaurant.getPhone())
                || isBlank(restaurant.getLogoUrl())
                || restaurant.getOwnerId() == null) {
            throw new RequiredFieldException();
        }
    }

    private void validateName(String name) {
        if (!name.matches(DomainConstants.NAME_WITH_LETTER_REGEX)) {
            throw new InvalidRestaurantNameException();
        }
    }

    private void validateNit(String nit) {
        if (!nit.matches(DomainConstants.NIT_REGEX)
                || nit.length() < DomainConstants.NIT_MIN_LENGTH
                || nit.length() > DomainConstants.NIT_MAX_LENGTH) {
            throw new InvalidNitException();
        }
    }

    private void validatePhone(String phone) {
        if (phone.length() > DomainConstants.PHONE_MAX_LENGTH || !phone.matches(DomainConstants.PHONE_REGEX)) {
            throw new InvalidPhoneFormatException();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
