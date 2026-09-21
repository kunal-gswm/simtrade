package com.project.trading.model;

public class MarketOrder extends Order {

    public MarketOrder(int id, int userId, String stockSymbol,
                       Side side, int quantity, Status status) {

        super(id, userId, stockSymbol, side, quantity, status);
    }
}