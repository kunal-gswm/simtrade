package com.project.trading.model;

import java.time.LocalDateTime;

public class Trade {

    private int id;
    private int buyOrderId;
    private int sellOrderId;
    private String stockSymbol;
    private int quantity;
    private double price;
    private LocalDateTime executedAt;

    public Trade(int id, int buyOrderId, int sellOrderId,
                 String stockSymbol, int quantity, double price,
                 LocalDateTime executedAt) {

        this.id = id;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.executedAt = executedAt;
    }

    public int getId() {
        return id;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
}