package com.pragma.foodcourt.domain.usecase;

import com.pragma.foodcourt.domain.exception.ClientHasActiveOrderException;
import com.pragma.foodcourt.domain.exception.DishNotFoundException;
import com.pragma.foodcourt.domain.exception.DishNotInRestaurantException;
import com.pragma.foodcourt.domain.exception.EmptyOrderException;
import com.pragma.foodcourt.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.foodcourt.domain.exception.InvalidOrderStatusException;
import com.pragma.foodcourt.domain.exception.InvalidPaginationException;
import com.pragma.foodcourt.domain.exception.RestaurantNotFoundException;
import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.Order;
import com.pragma.foodcourt.domain.model.OrderItem;
import com.pragma.foodcourt.domain.model.OrderStatus;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.model.Restaurant;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID DISH_ID = UUID.randomUUID();
    private static final UUID EMPLOYEE_ID = UUID.randomUUID();

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IEmployeePersistencePort employeePersistencePort;

    private OrderUseCase orderUseCase;
    private Order validOrder;
    private Restaurant restaurant;
    private Dish dish;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderPersistencePort, restaurantPersistencePort,
                dishPersistencePort, employeePersistencePort, new OrderValidator(),
                new PaginationValidator());
        validOrder = new Order(null, null, RESTAURANT_ID, null,
                new ArrayList<>(List.of(new OrderItem(DISH_ID, 2))));
        restaurant = new Restaurant(RESTAURANT_ID, "Tablaito", "900123456",
                "Calle 1 # 2-3", "+573005698325", "http://logo/tablaito.png", UUID.randomUUID());
        dish = new Dish(DISH_ID, "Bandeja Paisa", 25000, "Traditional dish",
                "http://img/bandeja.png", "Main course", RESTAURANT_ID, true);
    }

    @Test
    @DisplayName("Persists the order as PENDING and binds it to the authenticated client")
    void shouldPlaceOrderWithPendingStatusAndClientFromToken() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderPersistencePort.existsActiveOrderByClient(CLIENT_ID)).thenReturn(false);
        when(dishPersistencePort.findDishById(DISH_ID)).thenReturn(dish);
        when(orderPersistencePort.saveOrder(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderUseCase.placeOrder(validOrder, CLIENT_ID);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).saveOrder(captor.capture());
        Order saved = captor.getValue();
        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertEquals(CLIENT_ID, saved.getClientId());
        assertEquals(RESTAURANT_ID, saved.getRestaurantId());
    }

    @Test
    @DisplayName("Rejects placing an order when the restaurant does not exist")
    void shouldThrowWhenRestaurantNotFound() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class,
                () -> orderUseCase.placeOrder(validOrder, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects placing an order when the client already has one in progress")
    void shouldThrowWhenClientHasActiveOrder() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderPersistencePort.existsActiveOrderByClient(CLIENT_ID)).thenReturn(true);

        assertThrows(ClientHasActiveOrderException.class,
                () -> orderUseCase.placeOrder(validOrder, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects placing an order when one of the dishes does not exist")
    void shouldThrowWhenDishNotFound() {
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderPersistencePort.existsActiveOrderByClient(CLIENT_ID)).thenReturn(false);
        when(dishPersistencePort.findDishById(DISH_ID)).thenReturn(null);

        assertThrows(DishNotFoundException.class,
                () -> orderUseCase.placeOrder(validOrder, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Rejects placing an order with a dish that belongs to another restaurant")
    void shouldThrowWhenDishBelongsToAnotherRestaurant() {
        dish.setRestaurantId(UUID.randomUUID());
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderPersistencePort.existsActiveOrderByClient(CLIENT_ID)).thenReturn(false);
        when(dishPersistencePort.findDishById(DISH_ID)).thenReturn(dish);

        assertThrows(DishNotInRestaurantException.class,
                () -> orderUseCase.placeOrder(validOrder, CLIENT_ID));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Does not touch persistence when the order fails field validation")
    void shouldNotReachPersistenceWhenValidationFails() {
        Order emptyOrder = new Order(null, null, RESTAURANT_ID, null, new ArrayList<>());

        assertThrows(EmptyOrderException.class,
                () -> orderUseCase.placeOrder(emptyOrder, CLIENT_ID));
        verify(restaurantPersistencePort, never()).findRestaurantById(any());
        verify(orderPersistencePort, never()).existsActiveOrderByClient(any());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Checks every dish in the order belongs to the restaurant before saving")
    void shouldValidateAllDishesBelongToRestaurant() {
        UUID secondDishId = UUID.randomUUID();
        validOrder.getItems().add(new OrderItem(secondDishId, 1));
        Dish secondDish = new Dish(secondDishId, "Arepa", 8000, "Corn cake",
                "http://img/arepa.png", "Side", RESTAURANT_ID, true);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderPersistencePort.existsActiveOrderByClient(CLIENT_ID)).thenReturn(false);
        when(dishPersistencePort.findDishById(DISH_ID)).thenReturn(dish);
        when(dishPersistencePort.findDishById(secondDishId)).thenReturn(secondDish);
        when(orderPersistencePort.saveOrder(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderUseCase.placeOrder(validOrder, CLIENT_ID);

        verify(dishPersistencePort).findDishById(DISH_ID);
        verify(dishPersistencePort).findDishById(secondDishId);
        verify(orderPersistencePort).saveOrder(any(Order.class));
    }

    @Test
    @DisplayName("Lists the orders of the employee's restaurant filtered by status and paginated")
    void shouldListOrdersByStatusForEmployeeRestaurant() {
        PagedResult<Order> expected = new PagedResult<>(List.of(), 0, 10, 0L, 0);
        when(employeePersistencePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(RESTAURANT_ID);
        when(orderPersistencePort.findOrdersByRestaurantAndStatus(
                RESTAURANT_ID, OrderStatus.PENDING, 0, 10)).thenReturn(expected);

        PagedResult<Order> result = orderUseCase.listOrdersByStatus(EMPLOYEE_ID, "PENDING", 0, 10);

        assertSame(expected, result);
        verify(orderPersistencePort).findOrdersByRestaurantAndStatus(
                RESTAURANT_ID, OrderStatus.PENDING, 0, 10);
    }

    @Test
    @DisplayName("Rejects listing orders with an unknown status value")
    void shouldThrowWhenStatusIsInvalid() {
        assertThrows(InvalidOrderStatusException.class,
                () -> orderUseCase.listOrdersByStatus(EMPLOYEE_ID, "NOT_A_STATUS", 0, 10));
        verify(orderPersistencePort, never())
                .findOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rejects listing orders when the employee is not assigned to any restaurant")
    void shouldThrowWhenEmployeeHasNoRestaurant() {
        when(employeePersistencePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(null);

        assertThrows(EmployeeNotLinkedToRestaurantException.class,
                () -> orderUseCase.listOrdersByStatus(EMPLOYEE_ID, "PENDING", 0, 10));
        verify(orderPersistencePort, never())
                .findOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rejects listing orders with invalid pagination before resolving the restaurant")
    void shouldThrowWhenPaginationIsInvalid() {
        assertThrows(InvalidPaginationException.class,
                () -> orderUseCase.listOrdersByStatus(EMPLOYEE_ID, "PENDING", -1, 10));
        verify(employeePersistencePort, never()).findRestaurantIdByEmployee(any());
        verify(orderPersistencePort, never())
                .findOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }
}
