package application.domain.valueobjects;

import application.domain.exceptions.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object: an amount of money in a specific currency.
 *
 * <p>Invariants: amount and currency are mandatory and the amount is never negative.
 * Instances are immutable and compared by value.
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null) {
            throw new DomainException("Money amount must not be null.");
        }
        if (currency == null) {
            throw new DomainException("Money currency must not be null.");
        }
        if (amount.signum() < 0) {
            throw new DomainException("Money amount must not be negative.");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean hasSameCurrency(Money other) {
        return other != null && currency.equals(other.currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new DomainException("Money cannot be multiplied by a negative factor.");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(factor)), currency);
    }

    public Money percentage(BigDecimal rate) {
        if (rate == null || rate.signum() < 0) {
            throw new DomainException("Percentage rate must not be negative.");
        }
        return new Money(amount.multiply(rate), currency);
    }

    private void requireSameCurrency(Money other) {
        if (!hasSameCurrency(other)) {
            throw new DomainException("Currency mismatch between money amounts.");
        }
    }
}
