package com.pragma.foodcourt.infrastructure.configuration;

import com.pragma.foodcourt.domain.api.IDishServicePort;
import com.pragma.foodcourt.domain.api.IEmployeeServicePort;
import com.pragma.foodcourt.domain.api.IOrderServicePort;
import com.pragma.foodcourt.domain.api.IRestaurantServicePort;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.usecase.DishUseCase;
import com.pragma.foodcourt.domain.usecase.EmployeeUseCase;
import com.pragma.foodcourt.domain.usecase.OrderUseCase;
import com.pragma.foodcourt.domain.usecase.RestaurantUseCase;
import com.pragma.foodcourt.domain.validation.DishValidator;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import com.pragma.foodcourt.domain.validation.RestaurantValidator;
import com.pragma.foodcourt.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.foodcourt.infrastructure.out.jpa.adapter.EmployeeRestaurantJpaAdapter;
import com.pragma.foodcourt.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.foodcourt.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IEmployeeRestaurantEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IEmployeeRestaurantRepository;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final IEmployeeRestaurantRepository employeeRestaurantRepository;
    private final IEmployeeRestaurantEntityMapper employeeRestaurantEntityMapper;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Bean
    public IEmployeePersistencePort employeePersistencePort() {
        return new EmployeeRestaurantJpaAdapter(employeeRestaurantRepository, employeeRestaurantEntityMapper);
    }

    @Bean
    public IEmployeeServicePort employeeServicePort() {
        return new EmployeeUseCase(restaurantPersistencePort(), employeePersistencePort());
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public RestaurantValidator restaurantValidator() {
        return new RestaurantValidator();
    }

    @Bean
    public PaginationValidator paginationValidator() {
        return new PaginationValidator();
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(),
                restaurantValidator(), paginationValidator());
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public DishValidator dishValidator() {
        return new DishValidator();
    }

    @Bean
    public IDishServicePort dishServicePort() {
        return new DishUseCase(dishPersistencePort(), restaurantPersistencePort(),
                dishValidator(), paginationValidator());
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public OrderValidator orderValidator() {
        return new OrderValidator();
    }

    @Bean
    public IOrderServicePort orderServicePort() {
        return new OrderUseCase(orderPersistencePort(), restaurantPersistencePort(),
                dishPersistencePort(), employeePersistencePort(), orderValidator(), paginationValidator());
    }
}
