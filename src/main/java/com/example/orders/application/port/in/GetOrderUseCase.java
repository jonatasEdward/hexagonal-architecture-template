package com.example.orders.application.port.in;

import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

public interface GetOrderUseCase {

    Order getById(OrderId id);
}
