package com.example.orders.domain.event;

import com.example.orders.domain.model.OrderId;

import java.time.Instant;

public record OrderCancelled(OrderId orderId, Instant occurredAt) implements OrderEvent {
}
