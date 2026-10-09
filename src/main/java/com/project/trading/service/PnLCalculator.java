package com.project.trading.service;

/**
 * Pure math utility for P&L calculations.
 * All arithmetic uses double (matching the model layer) with rounding to 2dp.
 *
 * Dev 4 — T37.
 */
public class PnLCalculator {

    private PnLCalculator() {}

    /** Total cost of buying qty shares at avgBuyPrice. */
    public static double calculateInvested(int qty, double avgBuyPrice) {
        return round2(qty * avgBuyPrice);
    }

    /** Market value of qty shares at currentPrice. */
    public static double calculateCurrentValue(int qty, double currentPrice) {
        return round2(qty * currentPrice);
    }

    /** Unrealized gain/loss = currentValue - invested. */
    public static double calculateUnrealizedPnl(double currentValue, double invested) {
        return round2(currentValue - invested);
    }

    /** Unrealized P&L as a percentage of amount invested. Returns 0 if invested is 0. */
    public static double calculateUnrealizedPct(double unrealized, double invested) {
        if (invested == 0) return 0.0;
        return round2((unrealized / invested) * 100.0);
    }

    /** Net worth = cash in hand + total current market value of holdings. */
    public static double calculateNetWorth(double cash, double totalCurrentValue) {
        return round2(cash + totalCurrentValue);
    }

    /**
     * Overall P&L = net worth − starting capital.
     * Starting capital for Sim Trade is always ₹1,00,000.
     */
    public static double calculateOverallPnl(double netWorth) {
        return round2(netWorth - 100_000.0);
    }

    /** Helper — round to 2 decimal places using HALF_UP. */
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
