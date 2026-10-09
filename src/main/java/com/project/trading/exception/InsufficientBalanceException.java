package com.project.trading.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends AppException {
    private static final long serialVersionUID = 1L;
    private final BigDecimal available;
    private final BigDecimal required;

    public InsufficientBalanceException(BigDecimal available, BigDecimal required) {
        super("Insufficient funds. Available ₹" + available + ", required ₹" + required + ".");
        this.available = available;
        this.required = required;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getRequired() {
        return required;
    }
}
