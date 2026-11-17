package com.example.orders.adapter.out.persistence;

import com.example.orders.application.exception.ConcurrentOrderUpdateException;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderItem;
import com.example.orders.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class OrderPersistenceAdapterTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void incrementsVersionOnEachUpdate() {
        Order created = orderRepository.save(newOrder());
        assertThat(created.version()).isZero();

        Order loaded = orderRepository.findById(created.id()).orElseThrow();
        loaded.cancel();
        Order updated = orderRepository.save(loaded);

        assertThat(updated.version()).isEqualTo(1);
        assertThat(updated.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void rejectsUpdateBasedOnStaleVersion() {
        Order created = orderRepository.save(newOrder());

        Order firstCopy = orderRepository.findById(created.id()).orElseThrow();
        Order secondCopy = orderRepository.findById(created.id()).orElseThrow();

        firstCopy.cancel();
        orderRepository.save(firstCopy);

        secondCopy.cancel();
        assertThatThrownBy(() -> orderRepository.save(secondCopy))
                .isInstanceOf(ConcurrentOrderUpdateException.class);

        assertThat(orderRepository.findById(created.id()).orElseThrow().version()).isEqualTo(1);
    }

    private static Order newOrder() {
        return Order.create("customer-7", List.of(new OrderItem("book-1", 1, Money.of("10.00"))), NOW);
    }
}
