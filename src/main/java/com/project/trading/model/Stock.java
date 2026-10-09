package com.project.trading.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/** Stock record used by JDBC services and the portfolio market dashboard. */
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String symbol;
    private String companyName;
    private String sector;
    private String description;
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal prevPrice = BigDecimal.ZERO;
    private boolean active = true;
    private Timestamp updatedAt;

    // Additional quote data used by the dashboard's simulated market view.
    private long volume;
    private double high;
    private double low;
    private double week52High;
    private double week52Low;

    public Stock() {}

    /** Builds a dashboard quote while keeping price values compatible with JDBC's BigDecimal API. */
    public Stock(String symbol, String name, double price, double previousClose, long volume,
                 double high, double low, double week52High, double week52Low) {
        this.symbol = symbol;
        this.companyName = name;
        this.price = BigDecimal.valueOf(price);
        this.prevPrice = BigDecimal.valueOf(previousClose);
        this.volume = volume;
        this.high = high;
        this.low = low;
        this.week52High = week52High;
        this.week52Low = week52Low;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getName() { return companyName; }
    public void setName(String name) { this.companyName = name; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setPrice(double price) { this.price = BigDecimal.valueOf(price); }
    public BigDecimal getPrevPrice() { return prevPrice; }
    public void setPrevPrice(BigDecimal prevPrice) { this.prevPrice = prevPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public double getPriceAsDouble() { return price == null ? 0.0 : price.doubleValue(); }
    public double getPreviousClose() { return prevPrice == null ? 0.0 : prevPrice.doubleValue(); }
    public void setPreviousClose(double previousClose) { this.prevPrice = BigDecimal.valueOf(previousClose); }
    public double getChange() { return getPriceAsDouble() - getPreviousClose(); }
    public BigDecimal getChangePercent() {
        if (price == null || prevPrice == null || prevPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return price.subtract(prevPrice).multiply(BigDecimal.valueOf(100))
                .divide(prevPrice, 4, RoundingMode.HALF_UP);
    }
    public double getChangePercentAsDouble() { return getChangePercent().doubleValue(); }
    public void recalculateChange() { /* Change values are derived from price and previous close. */ }
    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }
    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }
    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }
    public double getWeek52High() { return week52High; }
    public void setWeek52High(double week52High) { this.week52High = week52High; }
    public double getWeek52Low() { return week52Low; }
    public void setWeek52Low(double week52Low) { this.week52Low = week52Low; }
    public boolean isPositive() { return getChange() >= 0; }
}
