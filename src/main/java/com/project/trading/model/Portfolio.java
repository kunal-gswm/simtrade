package com.project.trading.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's trading portfolio, cash balance, and equity holdings.
 */
public class Portfolio implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private BigDecimal cash = BigDecimal.valueOf(100000.00).setScale(2, RoundingMode.HALF_UP);
    private List<Holding> holdings;

    public Portfolio() {
        this.holdings = new ArrayList<>();
    }

    public Portfolio(int userId, BigDecimal cash, List<Holding> holdings) {
        this.userId = userId;
        this.cash = cash != null ? cash.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.holdings = holdings != null ? holdings : new ArrayList<>();
    }

    public Portfolio(int userId, double cashBalance, List<Holding> holdings) {
        this(userId, BigDecimal.valueOf(cashBalance), holdings);
    }

    public Portfolio(int userId, List<Holding> holdings) {
        this(userId, BigDecimal.valueOf(100000.00), holdings);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash != null ? cash.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public double getCashBalance() {
        return cash.doubleValue();
    }

    public void setCashBalance(double cashBalance) {
        setCash(BigDecimal.valueOf(cashBalance));
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Holding> holdings) {
        this.holdings = holdings != null ? holdings : new ArrayList<>();
    }

    public synchronized void debit(BigDecimal amount) {
        if (amount != null) {
            this.cash = this.cash.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public synchronized void credit(BigDecimal amount) {
        if (amount != null) {
            this.cash = this.cash.add(amount).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public synchronized void debit(double amount) {
        debit(BigDecimal.valueOf(amount));
    }

    public synchronized void credit(double amount) {
        credit(BigDecimal.valueOf(amount));
    }

    public BigDecimal getTotalHoldingsInvestmentBigDecimal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding h : holdings) {
            total = total.add(h.getTotalInvestmentBigDecimal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalHoldingsValueBigDecimal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding h : holdings) {
            total = total.add(h.getCurrentValueBigDecimal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalNetWorthBigDecimal() {
        return cash.add(getTotalHoldingsValueBigDecimal()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalProfitLossBigDecimal() {
        return getTotalHoldingsValueBigDecimal().subtract(getTotalHoldingsInvestmentBigDecimal()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalProfitLossPercentBigDecimal() {
        BigDecimal invested = getTotalHoldingsInvestmentBigDecimal();
        if (invested.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return getTotalProfitLossBigDecimal().divide(invested, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public double getTotalHoldingsInvestment() {
        return getTotalHoldingsInvestmentBigDecimal().doubleValue();
    }

    public double getTotalHoldingsValue() {
        return getTotalHoldingsValueBigDecimal().doubleValue();
    }

    public double getTotalNetWorth() {
        return getTotalNetWorthBigDecimal().doubleValue();
    }

    public double getTotalProfitLoss() {
        return getTotalProfitLossBigDecimal().doubleValue();
    }

    public double getTotalProfitLossPercent() {
        return getTotalProfitLossPercentBigDecimal().doubleValue();
    }

    public Holding getHolding(String symbol) {
        if (symbol == null) return null;
        for (Holding h : holdings) {
            if (h.getStockSymbol() != null && h.getStockSymbol().equalsIgnoreCase(symbol)) {
                return h;
            }
        }
        return null;
    }

    public synchronized void addOrUpdateHolding(String symbol, String name, int quantity, BigDecimal price) {
        Holding existing = getHolding(symbol);
        if (existing != null) {
            existing.addShares(quantity, price);
            existing.setCurrentPriceBigDecimal(price);
        } else {
            holdings.add(new Holding(symbol, name, quantity, price, price));
        }
    }

    public synchronized void addOrUpdateHolding(String symbol, String name, int quantity, double price) {
        addOrUpdateHolding(symbol, name, quantity, BigDecimal.valueOf(price));
    }

    public synchronized boolean removeOrReduceHolding(String symbol, int quantity) {
        Holding existing = getHolding(symbol);
        if (existing == null || existing.getQuantity() < quantity) {
            return false;
        }
        existing.removeShares(quantity);
        if (existing.getQuantity() <= 0) {
            holdings.remove(existing);
        }
        return true;
    }
}
