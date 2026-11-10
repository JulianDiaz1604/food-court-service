package com.pragma.foodcourt.infrastructure.out.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Minimal projection of the users-service {@code GET /api/v1/users/{id}} response.
 * Only the fields required for the order-ready notification are captured here.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserContactResponseDto {
    private UUID id;
    private String name;
    private String lastName;
    private String phone;
}
