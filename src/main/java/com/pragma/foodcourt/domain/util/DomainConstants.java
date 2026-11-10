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

    public static final int MIN_PAGE_NUMBER = 0;
    public static final int MIN_PAGE_SIZE = 1;

    public static final String INVALID_PAGINATION_MESSAGE =
            "The page number must be zero or greater and the page size must be greater than zero";

    public static final int ORDER_QUANTITY_MINIMUM = 0;

    public static final String ORDER_EMPTY_MESSAGE = "An order must contain at least one dish";
    public static final String INVALID_QUANTITY_MESSAGE =
            "Each dish quantity must be greater than zero";
    public static final String DISH_NOT_IN_RESTAURANT_MESSAGE =
            "All dishes in an order must belong to the specified restaurant";
    public static final String CLIENT_HAS_ACTIVE_ORDER_MESSAGE =
            "The client already has an order in progress";

    public static final String INVALID_ORDER_STATUS_MESSAGE =
            "The provided order status is invalid";
    public static final String EMPLOYEE_NOT_LINKED_MESSAGE =
            "The employee is not assigned to any restaurant";

    public static final String EMPLOYEE_LINK_ALREADY_EXISTS_MESSAGE =
            "The employee is already linked to this restaurant";

    public static final String ORDER_NOT_FOUND_MESSAGE = "The order does not exist";
    public static final String ORDER_NOT_FROM_EMPLOYEE_RESTAURANT_MESSAGE =
            "The order does not belong to the employee's restaurant";
    public static final String ORDER_NOT_ASSIGNABLE_MESSAGE =
            "Only orders in PENDING status can be assigned and moved to preparation";

    public static final String ORDER_NOT_IN_PREPARATION_MESSAGE =
            "Only orders in IN_PREPARATION status can be marked as ready";

    public static final String ORDER_NOT_READY_MESSAGE =
            "Only orders in READY status can be marked as delivered";

    public static final String INVALID_DELIVERY_PIN_MESSAGE =
            "The security PIN does not match the one sent to the client";

    public static final String USER_SERVICE_UNAVAILABLE_MESSAGE =
            "The users service is currently unavailable; please try again later";

    public static final String MESSAGING_SERVICE_UNAVAILABLE_MESSAGE =
            "The messaging service is currently unavailable; please try again later";

    public static final String CLIENT_NOT_FOUND_MESSAGE =
            "The client associated with this order was not found";
}
