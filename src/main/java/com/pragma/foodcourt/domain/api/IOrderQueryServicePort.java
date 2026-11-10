package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Order;

import java.util.UUID;

public interface IOrderQueryServicePort {
    PagedResult<Order> listOrdersByStatus(UUID employeeId, String status, int page, int size);
}
