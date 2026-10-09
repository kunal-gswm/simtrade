package com.project.trading.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's trading portfolio, cash balance, and equity holdings.
 */
public class Portfolio implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private double cashBalance;
    private List<Holding> holdings;

    public Portfolio() {
        this.holdings = new ArrayList<>();
    }

    public Portfolio(int userId, double cashBalance, List<Holding> holdings) {
        this.userId = userId;
        this.cashBalance = cashBalance;
        this.holdings = holdings != null ? holdings : new ArrayList<>();
    }

    public Portfolio(int userId, List<Holding> holdings) {
        this(userId, 100000.0, holdings); // Default 1 Lakh simulated cash
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getCashBalance() {
        return Math.round(cashBalance * 100.0) / 100.0;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Holding> holdings) {
        this.holdings = holdings;
    }

    public synchronized void debit(double amount) {
        this.cashBalance -= amount;
    }

    public synchronized void credit(double amount) {
        this.cashBalance += amount;
    }

    public double getTotalHoldingsInvestment() {
        double total = 0.0;
        for (Holding h : holdings) {
            total += h.getTotalInvestment();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double getTotalHoldingsValue() {
        double total = 0.0;
        for (Holding h : holdings) {
            total += h.getCurrentValue();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double getTotalNetWorth() {
        return Math.round((cashBalance + getTotalHoldingsValue()) * 100.0) / 100.0;
    }

    public double getTotalProfitLoss() {
        return Math.round((getTotalHoldingsValue() - getTotalHoldingsInvestment()) * 100.0) / 100.0;
    }

    public double getTotalProfitLossPercent() {
        double invested = getTotalHoldingsInvestment();
        if (invested <= 0) return 0.0;
        return Math.round(((getTotalHoldingsValue() - invested) / invested * 100.0) * 100.0) / 100.0;
    }

    public Holding getHolding(String symbol) {
        for (Holding h : holdings) {
            if (h.getStockSymbol().equalsIgnoreCase(symbol)) {
                return h;
            }
        }
        return null;
    }

    public synchronized void addOrUpdateHolding(String symbol, String name, int quantity, double price) {
        Holding existing = getHolding(symbol);
        if (existing != null) {
            existing.addShares(quantity, price);
            existing.setCurrentPrice(price);
        } else {
            holdings.add(new Holding(symbol, name, quantity, price, price));
        }
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
