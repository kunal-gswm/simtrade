package com.project.trading.model;

import java.io.Serializable;

/**
 * Represents a major market index (e.g., NIFTY 50, SENSEX, NIFTY BANK).
 */
public class MarketIndex implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private double value;
    private double change;
    private double changePercent;
    private double open;
    private double high;
    private double low;

    public MarketIndex() {}

    public MarketIndex(String name, double value, double change, double changePercent, double open, double high, double low) {
        this.name = name;
        this.value = value;
        this.change = change;
        this.changePercent = changePercent;
        this.open = open;
        this.high = high;
        this.low = low;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public boolean isPositive() {
        return this.change >= 0;
    }
}
