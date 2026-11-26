package com.project.trading.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Institutional simulated cost and slippage calculator.
 *
 * Cost Model Specifications (Indian Equities):
 *   - Brokerage: Min(₹20.00, 0.03% * Turnover)
 *   - Regulatory/Taxes (STT, Stamp Duty, GST, Exchange charges): 0.00345% of Turnover
 *   - Slippage: Configurable basis points (default 2 bps = 0.02%) when level-2 book is unavailable.
 */
public class SimulationCostModel {

    private final BigDecimal brokerageRate = new BigDecimal("0.0003"); // 0.03%
    private final BigDecimal maxBrokeragePerOrder = new BigDecimal("20.00"); // ₹20 flat cap
    private final BigDecimal statutoryRate = new BigDecimal("0.0000345"); // 0.00345%
    private final BigDecimal slippageBps = new BigDecimal("0.0002"); // 2 bps (0.02%)

    public BigDecimal calculateBrokerage(BigDecimal turnover) {
        if (turnover == null || turnover.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal calculated = turnover.multiply(brokerageRate).setScale(2, RoundingMode.HALF_UP);
        return calculated.min(maxBrokeragePerOrder);
    }

    public BigDecimal calculateTaxesAndCharges(BigDecimal turnover) {
        if (turnover == null || turnover.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return turnover.multiply(statutoryRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalFees(BigDecimal turnover) {
        return calculateBrokerage(turnover).add(calculateTaxesAndCharges(turnover)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal applySlippage(BigDecimal referencePrice, boolean isBuy) {
        if (referencePrice == null || referencePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return referencePrice;
        }
        BigDecimal delta = referencePrice.multiply(slippageBps).setScale(4, RoundingMode.HALF_UP);
        if (isBuy) {
            return referencePrice.add(delta).setScale(2, RoundingMode.HALF_UP);
        } else {
            return referencePrice.subtract(delta).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal getSlippageAmount(BigDecimal originalPrice, BigDecimal executedPrice) {
        if (originalPrice == null || executedPrice == null) return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        return executedPrice.subtract(originalPrice).abs().setScale(4, RoundingMode.HALF_UP);
    }
}
