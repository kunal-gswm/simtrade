package com.project.trading.order.model;

public enum BracketRole {
    ENTRY("Entry Order"),
    TARGET_EXIT("Target Exit (Take-Profit)"),
    STOP_LOSS_EXIT("Stop-Loss Exit");

    private final String label;

    BracketRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
