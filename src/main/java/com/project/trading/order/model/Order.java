package com.project.trading.order.model;

import com.project.trading.model.Stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Domain entity representing an order placed within SimTrade's paper trading engine.
 * Full precision preserved with BigDecimal.
 */
public class Order {

    private Long id;
    private Long userId;
    private Long stockId;
    private OrderSide side;
    private OrderType orderType;
    private int quantity;
    private int filledQuantity = 0;
    private int remainingQuantity;
    private BigDecimal limitPrice;
    private BigDecimal triggerPrice;
    private BigDecimal targetPrice;
    private BigDecimal stopLossPrice;
    private BigDecimal averageFillPrice;
    private OrderStatus status = OrderStatus.PENDING;
    private OrderValidity validity = OrderValidity.DAY;
    private Long parentOrderId;
    private BracketRole bracketRole;
    private String idempotencyKey;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiryTime;

    // Transient navigational helpers for UI display
    private Stock stock;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }

    public OrderSide getSide() { return side; }
    public void setSide(OrderSide side) { this.side = side; }

    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.remainingQuantity = quantity - this.filledQuantity;
    }

    public int getFilledQuantity() { return filledQuantity; }
    public void setFilledQuantity(int filledQuantity) {
        this.filledQuantity = filledQuantity;
        this.remainingQuantity = this.quantity - filledQuantity;
    }

    public int getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(int remainingQuantity) { this.remainingQuantity = remainingQuantity; }

    public BigDecimal getLimitPrice() { return limitPrice; }
    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice != null ? limitPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getTriggerPrice() { return triggerPrice; }
    public void setTriggerPrice(BigDecimal triggerPrice) {
        this.triggerPrice = triggerPrice != null ? triggerPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice != null ? targetPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getStopLossPrice() { return stopLossPrice; }
    public void setStopLossPrice(BigDecimal stopLossPrice) {
        this.stopLossPrice = stopLossPrice != null ? stopLossPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getAverageFillPrice() { return averageFillPrice; }
    public void setAverageFillPrice(BigDecimal averageFillPrice) {
        this.averageFillPrice = averageFillPrice != null ? averageFillPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public OrderValidity getValidity() { return validity; }
    public void setValidity(OrderValidity validity) { this.validity = validity; }

    public Long getParentOrderId() { return parentOrderId; }
    public void setParentOrderId(Long parentOrderId) { this.parentOrderId = parentOrderId; }

    public BracketRole getBracketRole() { return bracketRole; }
    public void setBracketRole(BracketRole bracketRole) { this.bracketRole = bracketRole; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Instant expiryTime) { this.expiryTime = expiryTime; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public boolean isCancellable() {
        return status == OrderStatus.OPEN || status == OrderStatus.TRIGGER_PENDING || status == OrderStatus.PARTIALLY_FILLED;
    }
}
