package com.pragma.foodcourt.infrastructure.configuration;

import com.pragma.foodcourt.domain.api.IDishServicePort;
import com.pragma.foodcourt.domain.api.IEmployeeServicePort;
import com.pragma.foodcourt.domain.api.IOrderQueryServicePort;
import com.pragma.foodcourt.domain.api.IOrderStatusServicePort;
import com.pragma.foodcourt.domain.api.IPlaceOrderServicePort;
import com.pragma.foodcourt.domain.api.IRestaurantServicePort;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.domain.spi.IEmployeePersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderDeliveryPinPort;
import com.pragma.foodcourt.domain.spi.IOrderPersistencePort;
import com.pragma.foodcourt.domain.spi.IOrderReadyNotificationPort;
import com.pragma.foodcourt.domain.spi.IRestaurantPersistencePort;
import com.pragma.foodcourt.domain.spi.IUserClientPort;
import com.pragma.foodcourt.domain.usecase.DishUseCase;
import com.pragma.foodcourt.domain.service.EmployeeRestaurantResolver;
import com.pragma.foodcourt.domain.usecase.EmployeeUseCase;
import com.pragma.foodcourt.domain.usecase.OrderQueryUseCase;
import com.pragma.foodcourt.domain.usecase.OrderStatusUseCase;
import com.pragma.foodcourt.domain.usecase.PlaceOrderUseCase;
import com.pragma.foodcourt.domain.usecase.RestaurantUseCase;
import com.pragma.foodcourt.domain.validation.DishValidator;
import com.pragma.foodcourt.domain.validation.OrderValidator;
import com.pragma.foodcourt.domain.validation.PaginationValidator;
import com.pragma.foodcourt.domain.validation.RestaurantValidator;
import com.pragma.foodcourt.infrastructure.out.client.AuthorizationForwardingInterceptor;
import com.pragma.foodcourt.infrastructure.out.client.MessagingRestClientAdapter;
import com.pragma.foodcourt.infrastructure.out.client.UsersRestClientAdapter;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

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

    // ---- cross-service HTTP client beans ----

    @Bean
    public AuthorizationForwardingInterceptor authorizationForwardingInterceptor() {
        return new AuthorizationForwardingInterceptor();
    }

    @Bean
    public RestClient usersRestClient(
            @Value("${users-service.base-url}") String baseUrl,
            AuthorizationForwardingInterceptor authorizationForwardingInterceptor) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(authorizationForwardingInterceptor)
                .build();
    }

    @Bean
    public RestClient messagingRestClient(
            @Value("${messaging-service.base-url}") String baseUrl,
            AuthorizationForwardingInterceptor authorizationForwardingInterceptor) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(authorizationForwardingInterceptor)
                .build();
    }

    @Bean
    public IUserClientPort userClientPort(RestClient usersRestClient) {
        return new UsersRestClientAdapter(usersRestClient);
    }

    // One messaging adapter exposed under both messaging-facing ports (notify + PIN validation).
    @Bean
    public MessagingRestClientAdapter messagingRestClientAdapter(RestClient messagingRestClient) {
        return new MessagingRestClientAdapter(messagingRestClient);
    }

    @Bean
    public IOrderReadyNotificationPort orderReadyNotificationPort(
            MessagingRestClientAdapter messagingRestClientAdapter) {
        return messagingRestClientAdapter;
    }

    @Bean
    public IOrderDeliveryPinPort orderDeliveryPinPort(
            MessagingRestClientAdapter messagingRestClientAdapter) {
        return messagingRestClientAdapter;
    }

    @Bean
    public EmployeeRestaurantResolver employeeRestaurantResolver() {
        return new EmployeeRestaurantResolver(employeePersistencePort());
    }

    @Bean
    public IPlaceOrderServicePort placeOrderServicePort() {
        return new PlaceOrderUseCase(orderPersistencePort(), restaurantPersistencePort(),
                dishPersistencePort(), orderValidator());
    }

    @Bean
    public IOrderQueryServicePort orderQueryServicePort() {
        return new OrderQueryUseCase(orderPersistencePort(), orderValidator(),
                paginationValidator(), employeeRestaurantResolver());
    }

    @Bean
    public IOrderStatusServicePort orderStatusServicePort(
            IUserClientPort userClientPort,
            IOrderReadyNotificationPort orderReadyNotificationPort,
            IOrderDeliveryPinPort orderDeliveryPinPort) {
        return new OrderStatusUseCase(orderPersistencePort(), orderValidator(),
                userClientPort, orderReadyNotificationPort, orderDeliveryPinPort,
                employeeRestaurantResolver());
    }
}
