package com.pragma.foodcourt.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Link between an employee (a user living in the users-service) and the restaurant they belong to.
 * Later stories (e.g. listing orders by the employee's restaurant) rely on this relation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRestaurant {
    private UUID id;
    private UUID employeeId;
    private UUID restaurantId;
}
