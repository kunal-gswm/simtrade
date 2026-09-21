package com.project.trading.model;

public class Holding {

    private String stockSymbol;
    private int quantity;
    private double averagePrice;

    public Holding(String stockSymbol, int quantity, double averagePrice) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }
    public void addShares(int additionalQuantity, double purchasePrice) {
        double totalCost = (this.averagePrice * this.quantity) + (purchasePrice * additionalQuantity);
        this.quantity += additionalQuantity;
        this.averagePrice = totalCost / this.quantity;
        }

    public void removeShares(int quantityToRemove) {
        this.quantity -= quantityToRemove;
    }
}