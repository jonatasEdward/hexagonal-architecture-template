package com.example.orders.domain.model;

import com.example.orders.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void normalizesScaleToTwoDecimalPlaces() {
        assertThat(Money.of("10").amount()).isEqualTo(new BigDecimal("10.00"));
        assertThat(Money.of("10.005").amount()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    void addsAndMultipliesValues() {
        Money result = Money.of("2.50").times(3).plus(Money.of("0.50"));

        assertThat(result).isEqualTo(Money.of("8.00"));
    }

    @Test
    void rejectsNegativeAmounts() {
        assertThatThrownBy(() -> Money.of("-0.01"))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> new Money(null))
                .isInstanceOf(DomainValidationException.class);
    }
}
