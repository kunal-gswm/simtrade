package com.project.trading.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pure math utility for financial and P&L calculations.
 * All arithmetic uses BigDecimal with standard scale and HALF_UP rounding.
 */
public class PnLCalculator {

    public static final BigDecimal STARTING_CAPITAL = new BigDecimal("100000.00");
    public static final int CURRENCY_SCALE = 2;
    public static final int PERCENT_SCALE = 2;
    public static final int AVG_PRICE_SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private PnLCalculator() {}

    /** Total cost of buying qty shares at avgBuyPrice: qty * avgBuyPrice */
    public static BigDecimal calculateInvested(int qty, BigDecimal avgBuyPrice) {
        if (avgBuyPrice == null || qty <= 0) {
            return BigDecimal.ZERO.setScale(CURRENCY_SCALE, ROUNDING);
        }
        return avgBuyPrice.multiply(BigDecimal.valueOf(qty)).setScale(CURRENCY_SCALE, ROUNDING);
    }

    /** Market value of qty shares at currentPrice: qty * currentPrice */
    public static BigDecimal calculateCurrentValue(int qty, BigDecimal currentPrice) {
        if (currentPrice == null || qty <= 0) {
            return BigDecimal.ZERO.setScale(CURRENCY_SCALE, ROUNDING);
        }
        return currentPrice.multiply(BigDecimal.valueOf(qty)).setScale(CURRENCY_SCALE, ROUNDING);
    }

    /** Unrealized gain/loss = currentValue - invested */
    public static BigDecimal calculateUnrealizedPnl(BigDecimal currentValue, BigDecimal invested) {
        BigDecimal cur = currentValue != null ? currentValue : BigDecimal.ZERO;
        BigDecimal inv = invested != null ? invested : BigDecimal.ZERO;
        return cur.subtract(inv).setScale(CURRENCY_SCALE, ROUNDING);
    }

    /** Unrealized P&L as a percentage: (unrealized / invested) * 100 */
    public static BigDecimal calculateUnrealizedPct(BigDecimal unrealized, BigDecimal invested) {
        if (invested == null || invested.compareTo(BigDecimal.ZERO) == 0 || unrealized == null) {
            return BigDecimal.ZERO.setScale(PERCENT_SCALE, ROUNDING);
        }
        return unrealized.divide(invested, 6, ROUNDING)
                .multiply(BigDecimal.valueOf(100))
                .setScale(PERCENT_SCALE, ROUNDING);
    }

    /** Net worth = cash in hand + total current market value of holdings */
    public static BigDecimal calculateNetWorth(BigDecimal cash, BigDecimal totalCurrentValue) {
        BigDecimal c = cash != null ? cash : BigDecimal.ZERO;
        BigDecimal v = totalCurrentValue != null ? totalCurrentValue : BigDecimal.ZERO;
        return c.add(v).setScale(CURRENCY_SCALE, ROUNDING);
    }

    /** Overall P&L = net worth - starting capital */
    public static BigDecimal calculateOverallPnl(BigDecimal netWorth, BigDecimal startingCapital) {
        BigDecimal nw = netWorth != null ? netWorth : BigDecimal.ZERO;
        BigDecimal sc = startingCapital != null ? startingCapital : STARTING_CAPITAL;
        return nw.subtract(sc).setScale(CURRENCY_SCALE, ROUNDING);
    }

    public static BigDecimal calculateOverallPnl(BigDecimal netWorth) {
        return calculateOverallPnl(netWorth, STARTING_CAPITAL);
    }

    /** Helper to round any BigDecimal to 2 decimal places using HALF_UP */
    public static BigDecimal round2(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO.setScale(CURRENCY_SCALE, ROUNDING);
        return value.setScale(CURRENCY_SCALE, ROUNDING);
    }

    // ── Overloaded backward-compatible methods for callers passing double ────

    public static double calculateInvested(int qty, double avgBuyPrice) {
        return calculateInvested(qty, BigDecimal.valueOf(avgBuyPrice)).doubleValue();
    }

    public static double calculateCurrentValue(int qty, double currentPrice) {
        return calculateCurrentValue(qty, BigDecimal.valueOf(currentPrice)).doubleValue();
    }

    public static double calculateUnrealizedPnl(double currentValue, double invested) {
        return calculateUnrealizedPnl(BigDecimal.valueOf(currentValue), BigDecimal.valueOf(invested)).doubleValue();
    }

    public static double calculateUnrealizedPct(double unrealized, double invested) {
        return calculateUnrealizedPct(BigDecimal.valueOf(unrealized), BigDecimal.valueOf(invested)).doubleValue();
    }

    public static double calculateNetWorth(double cash, double totalCurrentValue) {
        return calculateNetWorth(BigDecimal.valueOf(cash), BigDecimal.valueOf(totalCurrentValue)).doubleValue();
    }

    public static double calculateOverallPnl(double netWorth) {
        return calculateOverallPnl(BigDecimal.valueOf(netWorth)).doubleValue();
    }

    public static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(CURRENCY_SCALE, ROUNDING).doubleValue();
    }
}
