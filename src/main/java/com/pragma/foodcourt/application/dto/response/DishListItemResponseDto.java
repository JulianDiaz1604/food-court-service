package com.pragma.foodcourt.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lightweight dish projection for the client-facing menu listing (HU10): only the fields a client
 * needs to browse a restaurant's menu, without exposing internal identifiers or the active flag.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishListItemResponseDto {
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private String category;
}
