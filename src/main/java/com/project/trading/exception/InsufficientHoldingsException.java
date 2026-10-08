package com.project.trading.exception;

public class InsufficientHoldingsException extends AppException {
    private final int held;
    private final int requested;

    public InsufficientHoldingsException(int held, int requested) {
        super("You hold only " + held + " shares.");
        this.held = held;
        this.requested = requested;
    }

    public int getHeld() {
        return held;
    }

    public int getRequested() {
        return requested;
    }
}
