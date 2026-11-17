package com.example.orders.domain.event;

import com.example.orders.domain.model.OrderId;

import java.time.Instant;

public sealed interface OrderEvent permits OrderCreated, OrderCancelled {

    OrderId orderId();

    Instant occurredAt();
}
