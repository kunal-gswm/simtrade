package com.project.trading.model;

import java.io.Serializable;

/**
 * Represents a stock/equity listed on the Sim Trade exchange.
 */
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;

    private String symbol;
    private String name;
    private double price;
    private double previousClose;
    private double change;
    private double changePercent;
    private long volume;
    private double high;
    private double low;
    private double week52High;
    private double week52Low;

    public Stock() {}

    public Stock(String symbol, String name, double price, double previousClose, long volume, 
                 double high, double low, double week52High, double week52Low) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.previousClose = previousClose;
        this.volume = volume;
        this.high = high;
        this.low = low;
        this.week52High = week52High;
        this.week52Low = week52Low;
        this.recalculateChange();
    }

    public void recalculateChange() {
        this.change = Math.round((this.price - this.previousClose) * 100.0) / 100.0;
        if (this.previousClose > 0) {
            this.changePercent = Math.round(((this.price - this.previousClose) / this.previousClose * 100.0) * 100.0) / 100.0;
        } else {
            this.changePercent = 0.0;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        if (price > this.high) this.high = price;
        if (price < this.low && price > 0) this.low = price;
        recalculateChange();
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
        recalculateChange();
    }

    public double getChange() {
        return change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
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

    public double getWeek52High() {
        return week52High;
    }

    public void setWeek52High(double week52High) {
        this.week52High = week52High;
    }

    public double getWeek52Low() {
        return week52Low;
    }

    public void setWeek52Low(double week52Low) {
        this.week52Low = week52Low;
    }

    public boolean isPositive() {
        return this.change >= 0;
    }
}
