package com.example.orders.application.exception;

import com.example.orders.domain.model.OrderId;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(OrderId id) {
        super("order " + id + " was not found");
    }
}
