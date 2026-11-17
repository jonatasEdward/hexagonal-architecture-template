package com.example.orders.domain.exception;

public class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(message);
    }
}
