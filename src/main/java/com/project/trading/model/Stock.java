package com.project.trading.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class Stock {
    private long id;
    private String symbol;
    private String companyName;
    private String sector;
    private String description;
    private BigDecimal price;
    private BigDecimal prevPrice;
    private boolean active;
    private Timestamp updatedAt;

    public Stock() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrevPrice() {
        return prevPrice;
    }

    public void setPrevPrice(BigDecimal prevPrice) {
        this.prevPrice = prevPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getChangePercent() {
        if (prevPrice == null || prevPrice.compareTo(BigDecimal.ZERO) == 0 || price == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal diff = price.subtract(prevPrice);
        return diff.divide(prevPrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}
