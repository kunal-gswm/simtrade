package com.project.trading.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an order placed or executed in Sim Trade.
 */
public class TradeOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String symbol;
    private String side;       // BUY or SELL
    private String orderType;  // MARKET or LIMIT
    private int quantity;
    private double price;
    private String status;     // EXECUTED, PENDING, CANCELLED
    private String timestamp;

    public TradeOrder() {}

    public TradeOrder(String orderId, String symbol, String side, String orderType, int quantity, double price, String status) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSide() {
        return side;
    }

    public String getOrderType() {
        return orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getTotalAmount() {
        return Math.round((quantity * price) * 100.0) / 100.0;
    }
}
