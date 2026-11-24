package com.project.trading.order.model;

public enum OrderStatus {
    PENDING("Pending", true),
    TRIGGER_PENDING("Trigger Pending", true),
    OPEN("Open", true),
    PARTIALLY_FILLED("Partially Filled", true),
    FILLED("Filled", false),
    CANCELLED("Cancelled", false),
    REJECTED("Rejected", false),
    EXPIRED("Expired", false);

    private final String displayName;
    private final boolean active;

    OrderStatus(String displayName, boolean active) {
        this.displayName = displayName;
        this.active = active;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isTerminal() {
        return !active;
    }
}
