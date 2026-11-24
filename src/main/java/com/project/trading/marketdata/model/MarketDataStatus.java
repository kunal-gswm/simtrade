package com.project.trading.marketdata.model;

/**
 * Explicit connection lifecycle states for market data feeds.
 */
public enum MarketDataStatus {
    CONNECTED("Connected", true),
    CONNECTING("Connecting...", false),
    DISCONNECTED("Disconnected", false),
    RECONNECTING("Reconnecting...", false),
    STALE("Feed Stale", false),
    ERROR("Feed Error", false),
    NOT_CONFIGURED("Not Configured", false);

    private final String label;
    private final boolean orderSubmissionAllowed;

    MarketDataStatus(String label, boolean orderSubmissionAllowed) {
        this.label = label;
        this.orderSubmissionAllowed = orderSubmissionAllowed;
    }

    public String getLabel() {
        return label;
    }

    public boolean isOrderSubmissionAllowed() {
        return orderSubmissionAllowed;
    }
}
