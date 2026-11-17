package com.example.orders.application.exception;

import com.example.orders.domain.model.OrderId;

public class ConcurrentOrderUpdateException extends RuntimeException {

    public ConcurrentOrderUpdateException(OrderId id) {
        super("order " + id + " was modified by another operation, reload it and try again");
    }
}
