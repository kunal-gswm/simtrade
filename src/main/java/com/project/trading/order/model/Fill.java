package com.project.trading.order.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Execution fill record for paper trades.
 * Tracks executed quantity, fill price, slippage, and simulated fees.
 */
public class Fill {

    private Long id;
    private long orderId;
    private long userId;
    private long stockId;
    private int fillQuantity;
    private BigDecimal fillPrice;
    private BigDecimal totalAmount;
    private BigDecimal slippage = BigDecimal.ZERO;
    private BigDecimal simulatedBrokerage = BigDecimal.ZERO;
    private BigDecimal simulatedTaxes = BigDecimal.ZERO;
    private Instant fillTime;

    public Fill() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getStockId() { return stockId; }
    public void setStockId(long stockId) { this.stockId = stockId; }

    public int getFillQuantity() { return fillQuantity; }
    public void setFillQuantity(int fillQuantity) { this.fillQuantity = fillQuantity; }

    public BigDecimal getFillPrice() { return fillPrice; }
    public void setFillPrice(BigDecimal fillPrice) {
        this.fillPrice = fillPrice != null ? fillPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getSlippage() { return slippage; }
    public void setSlippage(BigDecimal slippage) {
        this.slippage = slippage != null ? slippage.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getSimulatedBrokerage() { return simulatedBrokerage; }
    public void setSimulatedBrokerage(BigDecimal simulatedBrokerage) {
        this.simulatedBrokerage = simulatedBrokerage != null ? simulatedBrokerage.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getSimulatedTaxes() { return simulatedTaxes; }
    public void setSimulatedTaxes(BigDecimal simulatedTaxes) {
        this.simulatedTaxes = simulatedTaxes != null ? simulatedTaxes.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public Instant getFillTime() { return fillTime; }
    public void setFillTime(Instant fillTime) { this.fillTime = fillTime; }

    public BigDecimal getTotalFees() {
        return simulatedBrokerage.add(simulatedTaxes).setScale(2, RoundingMode.HALF_UP);
    }
}
