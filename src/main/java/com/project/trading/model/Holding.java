package com.project.trading.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Represents a stock holding in a user's portfolio and in the database. */
public class Holding implements Serializable {
    private static final long serialVersionUID = 1L;

    private long userId;
    private long stockId;
    private String stockSymbol;
    private String stockName;
    private int quantity;
    private BigDecimal avgBuyPrice = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    private BigDecimal currentPrice = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private Stock stock;

    public Holding() {}

    public Holding(String stockSymbol, String stockName, int quantity,
                   BigDecimal averagePrice, BigDecimal currentPrice) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.quantity = quantity;
        this.avgBuyPrice = averagePrice != null ? averagePrice.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        this.currentPrice = currentPrice != null ? currentPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public Holding(String stockSymbol, String stockName, int quantity,
                   double averagePrice, double currentPrice) {
        this(stockSymbol, stockName, quantity, BigDecimal.valueOf(averagePrice), BigDecimal.valueOf(currentPrice));
    }

    public Holding(String stockSymbol, int quantity, double averagePrice) {
        this(stockSymbol, stockSymbol, quantity, averagePrice, averagePrice);
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getStockId() { return stockId; }
    public void setStockId(long stockId) { this.stockId = stockId; }

    public String getStockSymbol() {
        return stockSymbol != null ? stockSymbol : (stock == null ? null : stock.getSymbol());
    }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public String getStockName() {
        if (stockName != null) return stockName;
        if (stock != null && stock.getCompanyName() != null) return stock.getCompanyName();
        return getStockSymbol();
    }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getAvgBuyPrice() { return avgBuyPrice; }
    public void setAvgBuyPrice(BigDecimal avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice == null ? BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP) : avgBuyPrice.setScale(4, RoundingMode.HALF_UP);
    }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) {
        this.stock = stock;
        if (stock != null && stock.getPrice() != null) {
            this.currentPrice = stock.getPrice();
        }
    }

    public BigDecimal getCurrentPriceBigDecimal() {
        if (stock != null && stock.getPrice() != null) {
            return stock.getPrice();
        }
        return currentPrice;
    }

    public void setCurrentPriceBigDecimal(BigDecimal currentPrice) {
        this.currentPrice = currentPrice != null ? currentPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public double getAveragePrice() { return avgBuyPrice.doubleValue(); }
    public void setAveragePrice(double averagePrice) { setAvgBuyPrice(BigDecimal.valueOf(averagePrice)); }
    public double getCurrentPrice() { return getCurrentPriceBigDecimal().doubleValue(); }
    public void setCurrentPrice(double currentPrice) { setCurrentPriceBigDecimal(BigDecimal.valueOf(currentPrice)); }

    public BigDecimal getTotalInvestmentBigDecimal() {
        return avgBuyPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCurrentValueBigDecimal() {
        return getCurrentPriceBigDecimal().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getProfitLossBigDecimal() {
        return getCurrentValueBigDecimal().subtract(getTotalInvestmentBigDecimal());
    }

    public BigDecimal getProfitLossPercentBigDecimal() {
        BigDecimal inv = getTotalInvestmentBigDecimal();
        if (inv.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return getProfitLossBigDecimal().divide(inv, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public double getTotalInvestment() { return getTotalInvestmentBigDecimal().doubleValue(); }
    public double getCurrentValue() { return getCurrentValueBigDecimal().doubleValue(); }
    public double getProfitLoss() { return getProfitLossBigDecimal().doubleValue(); }
    public double getProfitLossPercent() { return getProfitLossPercentBigDecimal().doubleValue(); }
    public boolean isPositive() { return getProfitLossBigDecimal().compareTo(BigDecimal.ZERO) >= 0; }

    public void addShares(int additionalQuantity, BigDecimal purchasePrice) {
        BigDecimal currentTotal = avgBuyPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal addedTotal = purchasePrice.multiply(BigDecimal.valueOf(additionalQuantity));
        quantity += additionalQuantity;
        if (quantity > 0) {
            avgBuyPrice = currentTotal.add(addedTotal).divide(BigDecimal.valueOf(quantity), 4, RoundingMode.HALF_UP);
        }
    }

    public void addShares(int additionalQuantity, double purchasePrice) {
        addShares(additionalQuantity, BigDecimal.valueOf(purchasePrice));
    }

    public void removeShares(int quantityToRemove) {
        quantity -= quantityToRemove;
    }
}
