package com.example.orders.application.port.out;

import com.example.orders.application.pagination.PageQuery;
import com.example.orders.application.pagination.PageResult;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    PageResult<Order> findAll(PageQuery query);
}
