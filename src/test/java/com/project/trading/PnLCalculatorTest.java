package com.project.trading;

import com.project.trading.service.PnLCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PnLCalculator.
 * Dev 4 — T37. Run with: mvn test -Dtest=PnLCalculatorTest
 */
public class PnLCalculatorTest {

    // ── calculateInvested ────────────────────────────────────────────────────

    @Test
    void testCalculateInvested_normal() {
        assertEquals(39000.00, PnLCalculator.calculateInvested(10, 3900.00), 0.001);
    }

    @Test
    void testCalculateInvested_zeroQty() {
        assertEquals(0.00, PnLCalculator.calculateInvested(0, 3900.00), 0.001);
    }

    @Test
    void testCalculateInvested_fractionalPrice() {
        // 7 shares @ ₹542.80 = ₹3799.60
        assertEquals(3799.60, PnLCalculator.calculateInvested(7, 542.80), 0.001);
    }

    // ── calculateCurrentValue ─────────────────────────────────────────────────

    @Test
    void testCalculateCurrentValue_normal() {
        assertEquals(40000.00, PnLCalculator.calculateCurrentValue(10, 4000.00), 0.001);
    }

    @Test
    void testCalculateCurrentValue_zeroPrice() {
        assertEquals(0.00, PnLCalculator.calculateCurrentValue(10, 0.0), 0.001);
    }

    // ── calculateUnrealizedPnl ────────────────────────────────────────────────

    @Test
    void testCalculateUnrealizedPnl_profit() {
        assertEquals(1000.00, PnLCalculator.calculateUnrealizedPnl(40000.00, 39000.00), 0.001);
    }

    @Test
    void testCalculateUnrealizedPnl_loss() {
        assertEquals(-1000.00, PnLCalculator.calculateUnrealizedPnl(38000.00, 39000.00), 0.001);
    }

    @Test
    void testCalculateUnrealizedPnl_zero() {
        assertEquals(0.00, PnLCalculator.calculateUnrealizedPnl(39000.00, 39000.00), 0.001);
    }

    // ── calculateUnrealizedPct ────────────────────────────────────────────────

    @Test
    void testCalculateUnrealizedPct_normal() {
        // 1000 / 39000 * 100 = 2.56%
        assertEquals(2.56, PnLCalculator.calculateUnrealizedPct(1000.00, 39000.00), 0.001);
    }

    @Test
    void testCalculateUnrealizedPct_zeroInvested() {
        assertEquals(0.00, PnLCalculator.calculateUnrealizedPct(100.00, 0.00), 0.001);
    }

    @Test
    void testCalculateUnrealizedPct_negative() {
        assertEquals(-2.56, PnLCalculator.calculateUnrealizedPct(-1000.00, 39000.00), 0.001);
    }

    // ── calculateNetWorth ─────────────────────────────────────────────────────

    @Test
    void testCalculateNetWorth_normal() {
        assertEquals(101900.00, PnLCalculator.calculateNetWorth(20000.00, 81900.00), 0.001);
    }

    @Test
    void testCalculateNetWorth_zeroCash() {
        assertEquals(81900.00, PnLCalculator.calculateNetWorth(0.0, 81900.00), 0.001);
    }

    @Test
    void testCalculateNetWorth_noHoldings() {
        assertEquals(100000.00, PnLCalculator.calculateNetWorth(100000.00, 0.0), 0.001);
    }

    // ── calculateOverallPnl ───────────────────────────────────────────────────

    @Test
    void testCalculateOverallPnl_profit() {
        assertEquals(3500.00, PnLCalculator.calculateOverallPnl(103500.00), 0.001);
    }

    @Test
    void testCalculateOverallPnl_loss() {
        assertEquals(-2000.00, PnLCalculator.calculateOverallPnl(98000.00), 0.001);
    }

    @Test
    void testCalculateOverallPnl_breakeven() {
        assertEquals(0.00, PnLCalculator.calculateOverallPnl(100000.00), 0.001);
    }

    // ── round2 helper ─────────────────────────────────────────────────────────

    @Test
    void testRound2_roundsHalfUp() {
        assertEquals(1.24, PnLCalculator.round2(1.235), 0.0001);
    }
}
