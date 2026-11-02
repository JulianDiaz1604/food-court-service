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
public class RestaurantResponseDto {
    private UUID id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String logoUrl;
    private UUID ownerId;
}
