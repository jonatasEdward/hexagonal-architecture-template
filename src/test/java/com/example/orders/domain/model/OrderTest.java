package com.example.orders.domain.model;

import com.example.orders.domain.exception.DomainValidationException;
import com.example.orders.domain.exception.OrderStateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Test
    void createsOrderWithCreatedStatus() {
        Order order = Order.create("customer-1", List.of(item("product-1", 1, "10.00")), NOW);

        assertThat(order.id()).isNotNull();
        assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.createdAt()).isEqualTo(NOW);
    }

    @Test
    void calculatesTotalFromItems() {
        Order order = Order.create("customer-1", List.of(
                item("product-1", 2, "10.50"),
                item("product-2", 1, "5.00")), NOW);

        assertThat(order.total()).isEqualTo(Money.of("26.00"));
    }

    @Test
    void rejectsOrderWithoutItems() {
        assertThatThrownBy(() -> Order.create("customer-1", List.of(), NOW))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    void rejectsOrderWithoutCustomer() {
        assertThatThrownBy(() -> Order.create(" ", List.of(item("product-1", 1, "1.00")), NOW))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void cancelsOrder() {
        Order order = Order.create("customer-1", List.of(item("product-1", 1, "10.00")), NOW);

        order.cancel();

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void doesNotCancelAnOrderTwice() {
        Order order = Order.create("customer-1", List.of(item("product-1", 1, "10.00")), NOW);
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderStateException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    void keepsItemsImmutable() {
        Order order = Order.create("customer-1", List.of(item("product-1", 1, "10.00")), NOW);

        assertThatThrownBy(() -> order.items().add(item("product-2", 1, "1.00")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static OrderItem item(String productId, int quantity, String unitPrice) {
        return new OrderItem(productId, quantity, Money.of(unitPrice));
    }
}
