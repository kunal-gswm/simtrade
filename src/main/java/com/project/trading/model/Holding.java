
package com.project.trading.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Represents a stock holding in a user's portfolio. */
public class Holding implements Serializable {
    private static final long serialVersionUID = 1L;

    private long userId;
    private long stockId;
    private String stockSymbol;
    private String stockName;
    private int quantity;
    private BigDecimal avgBuyPrice = BigDecimal.ZERO;
    private double currentPrice;
    private Stock stock;

    public Holding() {}

    public Holding(String stockSymbol, String stockName, int quantity,
                   double averagePrice, double currentPrice) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.quantity = quantity;
        this.avgBuyPrice = BigDecimal.valueOf(averagePrice);
        this.currentPrice = currentPrice;
    }

    public Holding(String stockSymbol, int quantity, double averagePrice) {
        this(stockSymbol, stockSymbol, quantity, averagePrice, averagePrice);
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getStockId() { return stockId; }
    public void setStockId(long stockId) { this.stockId = stockId; }
    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public String getStockName() { return stockName != null ? stockName : stockSymbol; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getAvgBuyPrice() { return avgBuyPrice; }
    public void setAvgBuyPrice(BigDecimal avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice == null ? BigDecimal.ZERO : avgBuyPrice;
    }

    /** Compatibility accessor for callers that use the portfolio UI's double API. */
    public double getAveragePrice() { return avgBuyPrice.doubleValue(); }
    public void setAveragePrice(double averagePrice) { this.avgBuyPrice = BigDecimal.valueOf(averagePrice); }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public double getTotalInvestment() {
        return money(avgBuyPrice.multiply(BigDecimal.valueOf(quantity)).doubleValue());
    }

    public double getCurrentValue() { return money(quantity * currentPrice); }
    public double getProfitLoss() { return money(getCurrentValue() - getTotalInvestment()); }
    public double getProfitLossPercent() {
        double investment = getTotalInvestment();
        return investment <= 0 ? 0.0 : money((getCurrentValue() - investment) / investment * 100.0);
    }
    public boolean isPositive() { return getProfitLoss() >= 0; }

    public void addShares(int additionalQuantity, double purchasePrice) {
        if (additionalQuantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        BigDecimal totalCost = avgBuyPrice.multiply(BigDecimal.valueOf(quantity))
                .add(BigDecimal.valueOf(purchasePrice).multiply(BigDecimal.valueOf(additionalQuantity)));
        quantity += additionalQuantity;
        avgBuyPrice = totalCost.divide(BigDecimal.valueOf(quantity), 10, RoundingMode.HALF_UP);
    }

    public void removeShares(int quantityToRemove) {
        if (quantityToRemove <= 0 || quantityToRemove > quantity)
            throw new IllegalArgumentException("Removal quantity must be positive and no greater than the holding");
        quantity -= quantityToRemove;
    }

    private static double money(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
