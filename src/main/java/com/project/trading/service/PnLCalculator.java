package com.project.trading.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PnLCalculator {
    
    private PnLCalculator() {
        // Prevent instantiation
    }

    public static BigDecimal calculateInvested(int qty, BigDecimal avgBuyPrice) {
        if (avgBuyPrice == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(qty)
                .multiply(avgBuyPrice)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateCurrentValue(int qty, BigDecimal currentPrice) {
        if (currentPrice == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(qty)
                .multiply(currentPrice)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateUnrealizedPnl(BigDecimal currentValue, BigDecimal invested) {
        if (currentValue == null || invested == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return currentValue.subtract(invested).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateUnrealizedPct(BigDecimal unrealized, BigDecimal invested) {
        if (invested == null || invested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return unrealized.multiply(new BigDecimal("100"))
                .divide(invested, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateNetWorth(BigDecimal cash, BigDecimal totalCurrentValue) {
        if (cash == null) cash = BigDecimal.ZERO;
        if (totalCurrentValue == null) totalCurrentValue = BigDecimal.ZERO;
        return cash.add(totalCurrentValue).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateOverallPnl(BigDecimal netWorth) {
        if (netWorth == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return netWorth.subtract(new BigDecimal("100000.00")).setScale(2, RoundingMode.HALF_UP);
    }
}
