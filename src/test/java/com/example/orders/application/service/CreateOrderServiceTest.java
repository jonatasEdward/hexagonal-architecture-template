package com.example.orders.application.service;

import com.example.orders.application.port.in.CreateOrderCommand;
import com.example.orders.domain.event.OrderCreated;
import com.example.orders.domain.exception.DomainValidationException;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateOrderServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final RecordingOrderEventPublisher publisher = new RecordingOrderEventPublisher();
    private final CreateOrderService service =
            new CreateOrderService(repository, publisher, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void createsAndStoresOrder() {
        Order order = service.create(command("customer-1", item("product-1", 2, "15.00")));

        assertThat(repository.findById(order.id())).contains(order);
        assertThat(order.total()).isEqualTo(Money.of("30.00"));
        assertThat(order.createdAt()).isEqualTo(NOW);
    }

    @Test
    void publishesOrderCreatedEvent() {
        Order order = service.create(command("customer-1", item("product-1", 1, "10.00")));

        assertThat(publisher.events())
                .singleElement()
                .isInstanceOfSatisfying(OrderCreated.class, event -> {
                    assertThat(event.orderId()).isEqualTo(order.id());
                    assertThat(event.total()).isEqualTo(Money.of("10.00"));
                });
    }

    @Test
    void doesNotStoreOrPublishWhenOrderIsInvalid() {
        assertThatThrownBy(() -> service.create(new CreateOrderCommand("customer-1", List.of())))
                .isInstanceOf(DomainValidationException.class);

        assertThat(repository.count()).isZero();
        assertThat(publisher.events()).isEmpty();
    }

    private static CreateOrderCommand command(String customerId, CreateOrderCommand.Item... items) {
        return new CreateOrderCommand(customerId, List.of(items));
    }

    private static CreateOrderCommand.Item item(String productId, int quantity, String unitPrice) {
        return new CreateOrderCommand.Item(productId, quantity, new BigDecimal(unitPrice));
    }
}
