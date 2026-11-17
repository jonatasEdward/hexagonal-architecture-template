package com.example.orders.application.service;

import com.example.orders.application.port.in.CreateOrderCommand;
import com.example.orders.application.port.in.CreateOrderUseCase;
import com.example.orders.application.port.out.OrderEventPublisher;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.event.OrderCreated;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderItem;

import java.time.Clock;
import java.util.List;

public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final Clock clock;

    public CreateOrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher, Clock clock) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    public Order create(CreateOrderCommand command) {
        List<OrderItem> items = command.items().stream()
                .map(item -> new OrderItem(item.productId(), item.quantity(), new Money(item.unitPrice())))
                .toList();

        Order order = Order.create(command.customerId(), items, clock.instant());
        Order saved = orderRepository.save(order);

        eventPublisher.publish(new OrderCreated(saved.id(), saved.customerId(), saved.total(), clock.instant()));
        return saved;
    }
}
