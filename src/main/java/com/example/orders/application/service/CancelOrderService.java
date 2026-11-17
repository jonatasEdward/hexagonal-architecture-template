package com.example.orders.application.service;

import com.example.orders.application.exception.OrderNotFoundException;
import com.example.orders.application.port.in.CancelOrderUseCase;
import com.example.orders.application.port.out.OrderEventPublisher;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.event.OrderCancelled;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

import java.time.Clock;

public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final Clock clock;

    public CancelOrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher, Clock clock) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    public Order cancel(OrderId id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.cancel();
        Order saved = orderRepository.save(order);

        eventPublisher.publish(new OrderCancelled(saved.id(), clock.instant()));
        return saved;
    }
}
