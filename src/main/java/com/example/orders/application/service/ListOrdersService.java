package com.example.orders.application.service;

import com.example.orders.application.pagination.PageQuery;
import com.example.orders.application.pagination.PageResult;
import com.example.orders.application.port.in.ListOrdersUseCase;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.model.Order;

public class ListOrdersService implements ListOrdersUseCase {

    private final OrderRepository orderRepository;

    public ListOrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PageResult<Order> list(PageQuery query) {
        return orderRepository.findAll(query);
    }
}
