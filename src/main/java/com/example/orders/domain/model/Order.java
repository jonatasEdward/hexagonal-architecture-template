package com.example.orders.domain.model;

import com.example.orders.domain.exception.DomainValidationException;
import com.example.orders.domain.exception.OrderStateException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class Order {

    private final OrderId id;
    private final String customerId;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private OrderStatus status;

    private Order(OrderId id, String customerId, List<OrderItem> items, OrderStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Order create(String customerId, List<OrderItem> items, Instant createdAt) {
        if (customerId == null || customerId.isBlank()) {
            throw new DomainValidationException("customer id is required");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainValidationException("an order must have at least one item");
        }
        return new Order(OrderId.newId(), customerId, items, OrderStatus.CREATED, createdAt);
    }

    public static Order restore(OrderId id, String customerId, List<OrderItem> items,
                                OrderStatus status, Instant createdAt) {
        return new Order(id, customerId, items, status, createdAt);
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new OrderStateException("order " + id + " is already cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    public Money total() {
        return items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::plus);
    }

    public OrderId id() {
        return id;
    }

    public String customerId() {
        return customerId;
    }

    public List<OrderItem> items() {
        return items;
    }

    public OrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Order order && id.equals(order.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
