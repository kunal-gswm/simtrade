package com.project.trading.order.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Tracks cash reserved for open BUY orders to guarantee no double-spending.
 */
public class ReservedFund {

    public enum ReservationStatus {
        RESERVED, COMMITTED, RELEASED
    }

    private Long id;
    private long orderId;
    private long userId;
    private BigDecimal reservedAmount;
    private ReservationStatus status = ReservationStatus.RESERVED;
    private Instant createdAt;
    private Instant updatedAt;

    public ReservedFund() {}

    public ReservedFund(long orderId, long userId, BigDecimal reservedAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.reservedAmount = reservedAmount != null ? reservedAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.status = ReservationStatus.RESERVED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public BigDecimal getReservedAmount() { return reservedAmount; }
    public void setReservedAmount(BigDecimal reservedAmount) {
        this.reservedAmount = reservedAmount != null ? reservedAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
