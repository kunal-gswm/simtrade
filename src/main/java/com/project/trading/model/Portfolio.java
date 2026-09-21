package com.project.trading.model;

import java.util.List;

public class Portfolio {

    private int userId;
    private List<Holding> holdings;
    private double cashBalance;

    public Portfolio(int userId, List<Holding> holdings) {
        this.userId = userId;
        this.holdings = holdings;
    }

    public int getUserId() {
        return userId;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void debit(double amount) {
        this.cashBalance -= amount;
    }

    public void credit(double amount) {
        this.cashBalance += amount;
    }
}