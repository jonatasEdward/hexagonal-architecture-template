package com.example.orders.domain.model;

import com.example.orders.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) implements Comparable<Money> {

    private static final int SCALE = 2;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (amount == null) {
            throw new DomainValidationException("amount is required");
        }
        if (amount.signum() < 0) {
            throw new DomainValidationException("amount must not be negative");
        }
        amount = amount.setScale(SCALE, RoundingMode.HALF_EVEN);
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money times(int multiplier) {
        return new Money(amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }
}
