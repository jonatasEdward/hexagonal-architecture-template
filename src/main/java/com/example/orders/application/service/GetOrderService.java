package com.example.orders.application.service;

import com.example.orders.application.exception.OrderNotFoundException;
import com.example.orders.application.port.in.GetOrderUseCase;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getById(OrderId id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
