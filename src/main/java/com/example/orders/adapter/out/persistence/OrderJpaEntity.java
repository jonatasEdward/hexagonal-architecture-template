package com.example.orders.adapter.out.persistence;

import com.example.orders.domain.model.OrderStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @BatchSize(size = 50)
    private List<OrderItemEmbeddable> items = new ArrayList<>();

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(UUID id, String customerId, OrderStatus status, Instant createdAt, List<OrderItemEmbeddable> items) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
        this.items.addAll(items);
    }

    void updateState(OrderStatus status, List<OrderItemEmbeddable> items) {
        this.status = status;
        this.items.clear();
        this.items.addAll(items);
    }

    UUID getId() {
        return id;
    }

    String getCustomerId() {
        return customerId;
    }

    OrderStatus getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    List<OrderItemEmbeddable> getItems() {
        return items;
    }
}
