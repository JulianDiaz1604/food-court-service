package com.pragma.foodcourt.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Employee to be created for a restaurant. The personal data is persisted by the users-service
 * (as a user with the EMPLOYEE role); this service only keeps the employee-restaurant link.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private UUID id;
    private String name;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String password;
    private UUID restaurantId;
}
