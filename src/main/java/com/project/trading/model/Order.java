package com.project.trading.model;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Order {

    private static final AtomicLong SEQUENCE_GENERATOR = new AtomicLong(0);

    public enum Side {
        BUY,
        SELL
    }

    public enum Status {
        PENDING,
        EXECUTED,
        CANCELLED
    }

    private final long sequenceNumber;
    private int id;
    private int userId;
    private String stockSymbol;
    private Side side;
    private int quantity;
    private Status status;

    public Order(int id, int userId, String stockSymbol,
                 Side side, int quantity, Status status) {

        this.sequenceNumber = SEQUENCE_GENERATOR.getAndIncrement();
        this.id = id;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.side = side;
        this.quantity = quantity;
        this.status = status;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Side getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public void reduceQuantity(int amount) {
        this.quantity -= amount;
    }

    public boolean isFullyFilled() {
        return this.quantity <= 0;
    }
}