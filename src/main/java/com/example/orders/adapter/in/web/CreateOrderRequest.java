package com.example.orders.adapter.in.web;

import com.example.orders.application.port.in.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank @Size(max = 64) String customerId,
        @NotEmpty @Size(max = 50) List<@Valid OrderItemRequest> items) {

    CreateOrderCommand toCommand() {
        List<CreateOrderCommand.Item> commandItems = items.stream()
                .map(item -> new CreateOrderCommand.Item(item.productId(), item.quantity(), item.unitPrice()))
                .toList();
        return new CreateOrderCommand(customerId, commandItems);
    }
}
