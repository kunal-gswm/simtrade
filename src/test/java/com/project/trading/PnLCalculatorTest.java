package com.project.trading;

import com.project.trading.service.PnLCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

public class PnLCalculatorTest {

    @Test
    public void testCalculateInvested() {
        BigDecimal result = PnLCalculator.calculateInvested(10, new BigDecimal("3900.0000"));
        assertEquals(new BigDecimal("39000.00"), result);
    }

    @Test
    public void testCalculateCurrentValue() {
        BigDecimal result = PnLCalculator.calculateCurrentValue(10, new BigDecimal("4000.00"));
        assertEquals(new BigDecimal("40000.00"), result);
    }

    @Test
    public void testCalculateUnrealizedPnl() {
        BigDecimal currentValue = new BigDecimal("40000.00");
        BigDecimal invested = new BigDecimal("39000.00");
        BigDecimal result = PnLCalculator.calculateUnrealizedPnl(currentValue, invested);
        assertEquals(new BigDecimal("1000.00"), result);
    }

    @Test
    public void testCalculateUnrealizedPct() {
        BigDecimal unrealized = new BigDecimal("1000.00");
        BigDecimal invested = new BigDecimal("39000.00");
        BigDecimal result = PnLCalculator.calculateUnrealizedPct(unrealized, invested);
        // 1000 / 39000 * 100 = 2.564... -> 2.56
        assertEquals(new BigDecimal("2.56"), result);
    }

    @Test
    public void testCalculateUnrealizedPctZeroInvested() {
        BigDecimal result = PnLCalculator.calculateUnrealizedPct(new BigDecimal("100.00"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    public void testCalculateNetWorth() {
        BigDecimal cash = new BigDecimal("20000.00");
        BigDecimal totalCurrentValue = new BigDecimal("81900.00");
        BigDecimal result = PnLCalculator.calculateNetWorth(cash, totalCurrentValue);
        assertEquals(new BigDecimal("101900.00"), result);
    }

    @Test
    public void testCalculateOverallPnl() {
        BigDecimal netWorth = new BigDecimal("103500.00");
        BigDecimal result = PnLCalculator.calculateOverallPnl(netWorth);
        assertEquals(new BigDecimal("3500.00"), result);
    }
}
