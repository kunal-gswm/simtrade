package com.project.trading.order.model;

import java.time.Instant;

/**
 * Tracks shares reserved for open SELL orders to guarantee no overselling.
 */
public class ReservedHolding {

    public enum ReservationStatus {
        RESERVED, COMMITTED, RELEASED
    }

    private Long id;
    private long orderId;
    private long userId;
    private long stockId;
    private int reservedQuantity;
    private ReservationStatus status = ReservationStatus.RESERVED;
    private Instant createdAt;
    private Instant updatedAt;

    public ReservedHolding() {}

    public ReservedHolding(long orderId, long userId, long stockId, int reservedQuantity) {
        this.orderId = orderId;
        this.userId = userId;
        this.stockId = stockId;
        this.reservedQuantity = reservedQuantity;
        this.status = ReservationStatus.RESERVED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getStockId() { return stockId; }
    public void setStockId(long stockId) { this.stockId = stockId; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
