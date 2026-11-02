package com.pragma.foodcourt.domain.util;

public final class DomainConstants {

    private DomainConstants() {
    }

    public static final int PHONE_MAX_LENGTH = 13;

    public static final String PHONE_REGEX = "^\\+?[0-9]+$";
    public static final String NAME_WITH_LETTER_REGEX = ".*[a-zA-Z].*";
    public static final String NIT_REGEX = "^[0-9]+$";

    public static final String FIELD_REQUIRED_MESSAGE = "All fields are required";
    public static final String INVALID_PHONE_MESSAGE =
            "The phone must be numeric, may start with '+', and have at most 13 characters";
    public static final String INVALID_RESTAURANT_NAME_MESSAGE =
            "The restaurant name cannot contain only numbers";
    public static final String INVALID_NIT_MESSAGE = "The NIT must contain digits only";
    public static final String INVALID_OWNER_MESSAGE =
            "The provided owner id does not belong to a registered owner";

    public static final int DISH_PRICE_MINIMUM = 0;

    public static final String INVALID_PRICE_MESSAGE =
            "The dish price must be a positive number greater than zero";
    public static final String RESTAURANT_NOT_FOUND_MESSAGE = "The restaurant does not exist";
    public static final String NOT_RESTAURANT_OWNER_MESSAGE =
            "The dish can only be created by the owner of the restaurant";
}
