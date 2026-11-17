package com.example.orders.adapter.in.web;

import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerId,
        String status,
        List<Item> items,
        BigDecimal total,
        Instant createdAt) {

    static OrderResponse from(Order order) {
        List<Item> items = order.items().stream().map(Item::from).toList();
        return new OrderResponse(
                order.id().value(),
                order.customerId(),
                order.status().name(),
                items,
                order.total().amount(),
                order.createdAt());
    }

    public record Item(String productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {

        static Item from(OrderItem item) {
            return new Item(item.productId(), item.quantity(), item.unitPrice().amount(), item.subtotal().amount());
        }
    }
}
