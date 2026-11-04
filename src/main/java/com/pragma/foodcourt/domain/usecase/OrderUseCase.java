package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.api.IOrderServicePort;
import com.pragma.foodcourt.domain.exception.ClientHasActiveOrderException;
import com.pragma.foodcourt.domain.exception.DishNotFoundException;
import com.pragma.foodcourt.domain.exception.DishNotInRestaurantException;
import com.pragma.foodcourt.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderItem;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;

import java.util.UUID;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IEmployeePersistencePort employeePersistencePort;
    private final OrderValidator orderValidator;
    private final PaginationValidator paginationValidator;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IEmployeePersistencePort employeePersistencePort,
                        OrderValidator orderValidator,
                        PaginationValidator paginationValidator) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.employeePersistencePort = employeePersistencePort;
        this.orderValidator = orderValidator;
        this.paginationValidator = paginationValidator;
    }

    @Override
    public Order placeOrder(Order order, UUID clientId) {
        orderValidator.validateNewOrder(order);
        ensureRestaurantExists(order.getRestaurantId());
        ensureClientHasNoActiveOrder(clientId);
        ensureDishesBelongToRestaurant(order);
        order.setClientId(clientId);
        order.setStatus(OrderStatus.PENDING);
        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public PagedResult<Order> listOrdersByStatus(UUID employeeId, String status, int page, int size) {
        paginationValidator.validate(page, size);
        OrderStatus orderStatus = orderValidator.parseStatus(status);
        UUID restaurantId = resolveEmployeeRestaurant(employeeId);
        return orderPersistencePort.findOrdersByRestaurantAndStatus(restaurantId, orderStatus, page, size);
    }

    private UUID resolveEmployeeRestaurant(UUID employeeId) {
        UUID restaurantId = employeePersistencePort.findRestaurantIdByEmployee(employeeId);
        if (restaurantId == null) {
            throw new EmployeeNotLinkedToRestaurantException();
        }
        return restaurantId;
    }

    private void ensureRestaurantExists(UUID restaurantId) {
        if (restaurantPersistencePort.findRestaurantById(restaurantId) == null) {
            throw new RestaurantNotFoundException();
        }
    }

    private void ensureClientHasNoActiveOrder(UUID clientId) {
        if (orderPersistencePort.existsActiveOrderByClient(clientId)) {
            throw new ClientHasActiveOrderException();
        }
    }

    private void ensureDishesBelongToRestaurant(Order order) {
        for (OrderItem item : order.getItems()) {
            Dish dish = dishPersistencePort.findDishById(item.getDishId());
            if (dish == null) {
                throw new DishNotFoundException();
            }
            if (!dish.getRestaurantId().equals(order.getRestaurantId())) {
                throw new DishNotInRestaurantException();
            }
        }
    }
}
