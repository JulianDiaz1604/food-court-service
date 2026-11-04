package com.pragma.foodcourt.infrastructure.out.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload sent to the users-service to create an employee user. Matches the users-service
 * {@code CreateEmployeeRequestDto} (name, lastName, documentNumber, phone, email, password).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeClientRequest {
    private String name;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String password;
}
