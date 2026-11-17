package com.example.orders.domain.model;

import com.example.orders.domain.exception.DomainValidationException;

public record OrderItem(String productId, int quantity, Money unitPrice) {

    public OrderItem {
        if (productId == null || productId.isBlank()) {
            throw new DomainValidationException("product id is required");
        }
        if (quantity <= 0) {
            throw new DomainValidationException("quantity must be greater than zero");
        }
        if (unitPrice == null) {
            throw new DomainValidationException("unit price is required");
        }
    }

    public Money subtotal() {
        return unitPrice.times(quantity);
    }
}
