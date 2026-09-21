package com.project.trading.model;

public class LimitOrder extends Order {

    private double limitPrice;

    public LimitOrder(int id, int userId, String stockSymbol,
                      Side side, int quantity, Status status,
                      double limitPrice) {

        super(id, userId, stockSymbol, side, quantity, status);

        this.limitPrice = limitPrice;
    }

    public double getLimitPrice() {
        return limitPrice;
    }
}