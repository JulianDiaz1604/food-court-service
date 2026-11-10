package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IOrderQueryServicePort;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.service.EmployeeRestaurantResolver;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;

import java.util.UUID;

public class OrderQueryUseCase implements IOrderQueryServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final OrderValidator orderValidator;
    private final PaginationValidator paginationValidator;
    private final EmployeeRestaurantResolver employeeRestaurantResolver;

    public OrderQueryUseCase(IOrderPersistencePort orderPersistencePort,
                             OrderValidator orderValidator,
                             PaginationValidator paginationValidator,
                             EmployeeRestaurantResolver employeeRestaurantResolver) {
        this.orderPersistencePort = orderPersistencePort;
        this.orderValidator = orderValidator;
        this.paginationValidator = paginationValidator;
        this.employeeRestaurantResolver = employeeRestaurantResolver;
    }

    @Override
    public PagedResult<Order> listOrdersByStatus(UUID employeeId, String status, int page, int size) {
        paginationValidator.validate(page, size);
        OrderStatus orderStatus = orderValidator.parseStatus(status);
        UUID restaurantId = employeeRestaurantResolver.resolveFor(employeeId);
        return orderPersistencePort.findOrdersByRestaurantAndStatus(restaurantId, orderStatus, page, size);
    }
}
