package com.example.orders.domain.event;

import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.OrderId;

import java.time.Instant;

public record OrderCreated(OrderId orderId, String customerId, Money total, Instant occurredAt) implements OrderEvent {
}
