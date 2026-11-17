package com.example.orders.config;

import com.example.orders.application.port.in.CancelOrderUseCase;
import com.example.orders.application.port.in.CreateOrderUseCase;
import com.example.orders.application.port.in.GetOrderUseCase;
import com.example.orders.application.port.in.ListOrdersUseCase;
import com.example.orders.application.port.out.OrderEventPublisher;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.application.service.CancelOrderService;
import com.example.orders.application.service.CreateOrderService;
import com.example.orders.application.service.GetOrderService;
import com.example.orders.application.service.ListOrdersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    CreateOrderUseCase createOrderUseCase(OrderRepository orderRepository,
                                          OrderEventPublisher eventPublisher,
                                          Clock clock) {
        return new CreateOrderService(orderRepository, eventPublisher, clock);
    }

    @Bean
    GetOrderUseCase getOrderUseCase(OrderRepository orderRepository) {
        return new GetOrderService(orderRepository);
    }

    @Bean
    ListOrdersUseCase listOrdersUseCase(OrderRepository orderRepository) {
        return new ListOrdersService(orderRepository);
    }

    @Bean
    CancelOrderUseCase cancelOrderUseCase(OrderRepository orderRepository,
                                          OrderEventPublisher eventPublisher,
                                          Clock clock) {
        return new CancelOrderService(orderRepository, eventPublisher, clock);
    }
}
