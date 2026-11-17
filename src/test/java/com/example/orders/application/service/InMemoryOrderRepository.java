package com.example.orders.application.service;

import com.example.orders.application.pagination.PageQuery;
import com.example.orders.application.pagination.PageResult;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new LinkedHashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.id(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public PageResult<Order> findAll(PageQuery query) {
        List<Order> page = orders.values().stream()
                .sorted(Comparator.comparing(Order::createdAt).reversed())
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .toList();
        return new PageResult<>(page, query.page(), query.size(), orders.size());
    }

    int count() {
        return orders.size();
    }
}
