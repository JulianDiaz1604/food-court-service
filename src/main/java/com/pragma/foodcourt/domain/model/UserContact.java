package com.pragma.foodcourt.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Minimal client contact information fetched from users-service to support order notifications.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserContact {
    private String phone;
    private String name;
}
