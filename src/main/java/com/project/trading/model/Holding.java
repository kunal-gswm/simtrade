package com.project.trading.model;

import java.io.Serializable;

/**
 * Represents a stock holding in a user's portfolio.
 */
public class Holding implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stockSymbol;
    private String stockName;
    private int quantity;
    private double averagePrice;
    private double currentPrice;

    public Holding() {}

    public Holding(String stockSymbol, String stockName, int quantity, double averagePrice, double currentPrice) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.currentPrice = currentPrice;
    }

    public Holding(String stockSymbol, int quantity, double averagePrice) {
        this(stockSymbol, stockSymbol, quantity, averagePrice, averagePrice);
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockName() {
        return stockName != null ? stockName : stockSymbol;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getTotalInvestment() {
        return Math.round((this.quantity * this.averagePrice) * 100.0) / 100.0;
    }

    public double getCurrentValue() {
        return Math.round((this.quantity * this.currentPrice) * 100.0) / 100.0;
    }

    public double getProfitLoss() {
        return Math.round((getCurrentValue() - getTotalInvestment()) * 100.0) / 100.0;
    }

    public double getProfitLossPercent() {
        double investment = getTotalInvestment();
        if (investment <= 0) return 0.0;
        return Math.round(((getCurrentValue() - investment) / investment * 100.0) * 100.0) / 100.0;
    }

    public boolean isPositive() {
        return getProfitLoss() >= 0;
    }

    public void addShares(int additionalQuantity, double purchasePrice) {
        double totalCost = (this.averagePrice * this.quantity) + (purchasePrice * additionalQuantity);
        this.quantity += additionalQuantity;
        if (this.quantity > 0) {
            this.averagePrice = totalCost / this.quantity;
        }
    }

    public void removeShares(int quantityToRemove) {
        this.quantity -= quantityToRemove;
    }
}
