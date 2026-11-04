package com.pragma.foodcourt.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lightweight restaurant projection for the client-facing listing: only the fields HU9 requires
 * (restaurant name and logo URL).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListItemResponseDto {
    private String name;
    private String logoUrl;
}
