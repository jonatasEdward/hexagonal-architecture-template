package com.example.orders.adapter.out.persistence;

import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;
import com.example.orders.domain.model.OrderItem;

import java.util.List;

final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {
    }

    static OrderJpaEntity toEntity(Order order) {
        return new OrderJpaEntity(
                order.id().value(),
                order.customerId(),
                order.status(),
                order.createdAt(),
                toEmbeddables(order.items()));
    }

    static List<OrderItemEmbeddable> toEmbeddables(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemEmbeddable(item.productId(), item.quantity(), item.unitPrice().amount()))
                .toList();
    }

    static Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(item -> new OrderItem(item.getProductId(), item.getQuantity(), new Money(item.getUnitPrice())))
                .toList();

        return Order.restore(
                new OrderId(entity.getId()),
                entity.getCustomerId(),
                items,
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
