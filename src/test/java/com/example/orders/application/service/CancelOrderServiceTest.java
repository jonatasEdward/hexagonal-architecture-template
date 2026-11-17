package com.example.orders.application.service;

import com.example.orders.application.exception.OrderNotFoundException;
import com.example.orders.domain.event.OrderCancelled;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;
import com.example.orders.domain.model.OrderItem;
import com.example.orders.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelOrderServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final RecordingOrderEventPublisher publisher = new RecordingOrderEventPublisher();
    private final CancelOrderService service =
            new CancelOrderService(repository, publisher, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void cancelsExistingOrder() {
        Order order = repository.save(newOrder());

        Order cancelled = service.cancel(order.id());

        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(repository.findById(order.id())).get()
                .extracting(Order::status)
                .isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void publishesOrderCancelledEvent() {
        Order order = repository.save(newOrder());

        service.cancel(order.id());

        assertThat(publisher.events()).containsExactly(new OrderCancelled(order.id(), NOW));
    }

    @Test
    void failsWhenOrderDoesNotExist() {
        OrderId unknown = OrderId.newId();

        assertThatThrownBy(() -> service.cancel(unknown))
                .isInstanceOf(OrderNotFoundException.class);
        assertThat(publisher.events()).isEmpty();
    }

    private static Order newOrder() {
        return Order.create("customer-1", List.of(new OrderItem("product-1", 1, Money.of("10.00"))), NOW);
    }
}
