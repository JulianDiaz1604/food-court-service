package com.pragma.foodcourt.infrastructure.out.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Minimal view of the users-service user payload, limited to what this service
 * needs to validate a restaurant owner. Unknown fields are ignored by Jackson.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserClientResponse {
    private UUID id;
    private Long roleId;
}
