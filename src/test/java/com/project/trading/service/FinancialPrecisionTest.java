package com.project.trading.service;

import com.project.trading.DBCreator;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialPrecisionTest {

    private TradingService tradingService;
    private PortfolioService portfolioService;
    private UserService userService;
    private StockService stockService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        tradingService = new TradingService();
        portfolioService = new PortfolioService();
        userService = new UserService();
        stockService = new StockService();
    }

    @Test
    public void testEquityAndPnLInvariants_AfterBuy() throws Exception {
        User user = userService.register("investor1", "inv1@example.com", "Investor One", "Pass123!");
        long uid = user.getId();
        BigDecimal initialCash = user.getCashBalance(); // 100,000.00

        // Stock 1: Reliance (price ~2850.00)
        Stock s1 = stockService.getById(1L);
        int buyQty = 10;
        Trade buyTrade = tradingService.buy(uid, 1L, buyQty);
        BigDecimal expectedCost = s1.getPrice().multiply(BigDecimal.valueOf(buyQty)).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedCost, buyTrade.getTotalAmount());

        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(uid);

        // Invariant 1: Total Equity = Available Cash + Current Holdings Value
        BigDecimal totalEquity = summary.cash.add(summary.totalCurrentValue).setScale(2, RoundingMode.HALF_UP);
        assertEquals(totalEquity, summary.netWorth, "Total Equity must exactly equal Net Worth (Cash + Holdings Value)");

        // Invariant 2: Total P&L = Realized P&L + Unrealized P&L
        BigDecimal combinedPnl = summary.realizedPnl.add(summary.totalUnrealizedPnl).setScale(2, RoundingMode.HALF_UP);
        assertEquals(combinedPnl, summary.overallPnl, "Overall P&L must reconcile Realized + Unrealized P&L");

        // Invariant 3: Overall P&L = Net Worth - Initial Cash
        BigDecimal pnlFromNetWorth = summary.netWorth.subtract(initialCash).setScale(2, RoundingMode.HALF_UP);
        assertEquals(pnlFromNetWorth, summary.overallPnl, "Overall P&L must equal Net Worth minus Initial Capital");
    }

    @Test
    public void testPartialSell_RealizedAndUnrealizedPnL() throws Exception {
        User user = userService.register("investor2", "inv2@example.com", "Investor Two", "Pass123!");
        long uid = user.getId();

        // 1. Buy 10 shares of Stock 1
        Stock s1 = stockService.getById(1L);
        BigDecimal buyPrice = s1.getPrice();
        tradingService.buy(uid, 1L, 10);

        // 2. Change market price higher to simulate price gain
        BigDecimal higherPrice = buyPrice.add(new BigDecimal("200.00"));
        stockService.updatePrice(1L, higherPrice);

        // 3. Partial sell: sell 4 shares at higher price
        Trade sellTrade = tradingService.sell(uid, 1L, 4);
        BigDecimal expectedRealizedPnl = higherPrice.subtract(buyPrice).multiply(new BigDecimal("4")).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedRealizedPnl, sellTrade.getRealizedPnl(), "Sell trade must calculate correct realized P&L");

        // 4. Verify portfolio summary
        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(uid);
        assertEquals(1, summary.rows.size());
        PortfolioService.HoldingRow row = summary.rows.get(0);
        assertEquals(6, row.quantity, "Remaining shares must be 6");
        assertEquals(buyPrice.setScale(2, RoundingMode.HALF_UP), row.avgBuyPrice, "Average buy price must remain unchanged after partial sell");

        BigDecimal expectedUnrealizedPnl = higherPrice.subtract(buyPrice).multiply(new BigDecimal("6")).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedUnrealizedPnl, summary.totalUnrealizedPnl);
        assertEquals(expectedRealizedPnl, summary.realizedPnl);

        // Reconcile overall P&L
        BigDecimal expectedOverall = expectedRealizedPnl.add(expectedUnrealizedPnl).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedOverall, summary.overallPnl);
    }

    @Test
    public void testFullSell_HoldingsClearedAndRealizedPnLRecorded() throws Exception {
        User user = userService.register("investor3", "inv3@example.com", "Investor Three", "Pass123!");
        long uid = user.getId();

        Stock s1 = stockService.getById(1L);
        BigDecimal buyPrice = s1.getPrice();
        tradingService.buy(uid, 1L, 5);

        BigDecimal higherPrice = buyPrice.add(new BigDecimal("150.00"));
        stockService.updatePrice(1L, higherPrice);

        // Sell all 5 shares
        tradingService.sell(uid, 1L, 5);

        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(uid);
        assertTrue(summary.rows.isEmpty(), "Holdings table must be empty after full sell");
        assertEquals(new BigDecimal("0.00"), summary.totalInvested);
        assertEquals(new BigDecimal("0.00"), summary.totalCurrentValue);
        assertEquals(new BigDecimal("0.00"), summary.totalUnrealizedPnl);

        BigDecimal expectedRealized = new BigDecimal("150.00").multiply(new BigDecimal("5")).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedRealized, summary.realizedPnl);
        assertEquals(expectedRealized, summary.overallPnl);
    }
}
