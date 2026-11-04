package com.pragma.foodcourt.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {
    private UUID id;
    private String name;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private UUID restaurantId;
}
