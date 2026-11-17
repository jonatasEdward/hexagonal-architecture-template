package com.example.orders.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderCommand(String customerId, List<Item> items) {

    public CreateOrderCommand {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public record Item(String productId, int quantity, BigDecimal unitPrice) {
    }
}
