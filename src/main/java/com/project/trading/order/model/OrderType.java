package com.project.trading.order.model;

public enum OrderType {
    MARKET("Market", "Executes immediately at best available market price"),
    LIMIT("Limit", "Executes only at specified limit price or better"),
    STOP_LOSS_MARKET("SL-M", "Triggers when price hits stop threshold and executes at market"),
    STOP_LOSS_LIMIT("SL", "Triggers when price hits stop threshold and becomes a limit order"),
    TARGET("Target", "Take-profit exit order linked to existing position"),
    BRACKET("Bracket", "Entry order with linked stop-loss and target exit orders (OCO)");

    private final String displayName;
    private final String description;

    OrderType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
