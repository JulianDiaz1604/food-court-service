package com.pragma.foodcourt.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id;
    private UUID clientId;
    private UUID restaurantId;
    private OrderStatus status;
    private UUID assignedEmployeeId;
    private List<OrderItem> items;
}
