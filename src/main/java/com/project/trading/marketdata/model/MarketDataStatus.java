package com.project.trading.marketdata.model;

/**
 * Explicit connection lifecycle states for market data feeds.
 * Distinguishes transport readiness, authentication progression, subscription state,
 * and genuine live exchange delivery.
 */
public enum MarketDataStatus {
    NOT_CONFIGURED("Not Configured", false),
    DISCONNECTED("Disconnected", false),
    CONNECTING("Connecting...", false),
    WEBSOCKET_CONNECTED("Transport Connected", false),
    AUTHENTICATING("Authenticating...", false),
    AUTHENTICATED("Authenticated", false),
    SUBSCRIBING("Subscribing...", false),
    LIVE("Live", true),
    STALE("Feed Stale", false),
    AUTHENTICATION_FAILED("Authentication Failed", false),
    RECONNECTING("Reconnecting...", false),

    // Compatibility aliases for simulation and existing tests
    CONNECTED("Connected", true),
    ERROR("Feed Error", false);

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
