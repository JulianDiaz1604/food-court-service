package com.pragma.foodcourt.domain.util;

public final class DomainConstants {

    private DomainConstants() {
    }

    public static final int PHONE_MAX_LENGTH = 13;
    public static final int NIT_MIN_LENGTH = 9;
    public static final int NIT_MAX_LENGTH = 10;

    public static final String PHONE_REGEX = "^\\+?[0-9]+$";
    public static final String NAME_WITH_LETTER_REGEX = ".*[a-zA-Z].*";
    public static final String NIT_REGEX = "^[0-9]+$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String DOCUMENT_REGEX = "^[0-9]+$";

    public static final String FIELD_REQUIRED_MESSAGE = "All fields are required";
    public static final String INVALID_PHONE_MESSAGE =
            "The phone must be numeric, may start with '+', and have at most 13 characters";
    public static final String INVALID_RESTAURANT_NAME_MESSAGE =
            "The restaurant name cannot contain only numbers";
    public static final String INVALID_NIT_MESSAGE =
            "The NIT must be numeric and contain between 9 and 10 digits";
    public static final String NIT_ALREADY_EXISTS_MESSAGE = "A restaurant with this NIT already exists";
    public static final String INVALID_OWNER_MESSAGE =
            "The provided owner id does not belong to a registered owner";

    public static final int DISH_PRICE_MINIMUM = 0;

    public static final String INVALID_PRICE_MESSAGE =
            "The dish price must be a positive number greater than zero";
    public static final String RESTAURANT_NOT_FOUND_MESSAGE = "The restaurant does not exist";
    public static final String NOT_RESTAURANT_OWNER_MESSAGE =
            "This action can only be performed by the owner of the restaurant";
    public static final String DISH_NOT_FOUND_MESSAGE = "The dish does not exist";

    public static final String INVALID_EMAIL_MESSAGE = "The email format is invalid";
    public static final String INVALID_DOCUMENT_MESSAGE = "The document number must contain digits only";
    public static final String EMPLOYEE_ALREADY_EXISTS_MESSAGE =
            "An employee with this email or document number already exists";
}
