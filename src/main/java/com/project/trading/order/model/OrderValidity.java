package com.project.trading.order.model;

public enum OrderValidity {
    DAY("Day", "Order expires at market close (15:30 IST)"),
    GTC("GTC", "Good 'Til Cancelled (valid across multiple sessions)"),
    IOC("IOC", "Immediate-Or-Cancel");

    private final String label;
    private final String description;

    OrderValidity(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
