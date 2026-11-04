package com.pragma.foodcourt.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDishRequestDto {
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private String category;
    private UUID restaurantId;
}
