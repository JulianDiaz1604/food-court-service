package com.pragma.foodcourt.domain.validation;

import com.pragma.foodcourt.domain.exception.InvalidPriceException;
import com.pragma.foodcourt.domain.exception.RequiredFieldException;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.util.DomainConstants;

public class DishValidator {

    public void validateNewDish(Dish dish) {
        validateRequiredFields(dish);
        validatePrice(dish.getPrice());
    }

    public void validateDishUpdate(Integer price, String description) {
        if (price == null || isBlank(description)) {
            throw new RequiredFieldException();
        }
        validatePrice(price);
    }

    private void validateRequiredFields(Dish dish) {
        if (isBlank(dish.getName())
                || isBlank(dish.getDescription())
                || isBlank(dish.getImageUrl())
                || isBlank(dish.getCategory())
                || dish.getRestaurantId() == null
                || dish.getPrice() == null) {
            throw new RequiredFieldException();
        }
    }

    private void validatePrice(Integer price) {
        if (price <= DomainConstants.DISH_PRICE_MINIMUM) {
            throw new InvalidPriceException();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
