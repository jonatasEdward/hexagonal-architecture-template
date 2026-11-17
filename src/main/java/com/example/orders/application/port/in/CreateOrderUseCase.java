package com.example.orders.application.port.in;

import com.example.orders.domain.model.Order;

public interface CreateOrderUseCase {

    Order create(CreateOrderCommand command);
}
