package com.project.trading.service;

import com.project.trading.DBCreator;
import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.dao.StockDAO;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.User;
import com.project.trading.util.DBConnection;
import com.vaadin.flow.shared.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Realtime Portfolio Engine & Data Consistency Tests")
public class RealtimePortfolioEngineTest {

    private TradingService tradingService;
    private PortfolioService portfolioService;
    private UserService userService;
    private StockService stockService;
    private StockDAO stockDAO;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        tradingService = new TradingService();
        portfolioService = new PortfolioService();
        userService = new UserService();
        stockService = new StockService();
        stockDAO = new StockDAO();
    }

    @Test
    @DisplayName("Verify price changes update holding market value and unrealized P&L in real time")
    public void testPriceChangeUpdatesHoldingsAndUnrealizedPnl() throws Exception {
        User user = userService.register("trader_rt1", "rt1@example.com", "Realtime Trader", "Pass123!");
        long uid = user.getId();
        BigDecimal initialCash = user.getCashBalance(); // 100,000.00

        Stock stock = stockService.getById(1L); // Reliance
        BigDecimal buyPrice = stock.getPrice();
        int quantity = 10;
        tradingService.buy(uid, stock.getId(), quantity);

        // Verify initial state
        PortfolioService.PortfolioSummary initialSummary = portfolioService.getSummaryForUser(uid);
        assertEquals(0, initialSummary.totalUnrealizedPnl.compareTo(BigDecimal.ZERO.setScale(2)));

        // Simulate market price surge (+150 per share)
        BigDecimal newPrice = buyPrice.add(BigDecimal.valueOf(150.00)).setScale(2, RoundingMode.HALF_UP);
        Map<Long, BigDecimal> newPrices = new HashMap<>();
        newPrices.put(stock.getId(), newPrice);

        // Update database and broadcast price change
        try (Connection c = DBConnection.getConnection()) {
            stockDAO.updatePricesBatch(c, newPrices);
        }
        MarketDataBroadcaster.broadcastPrices(newPrices);

        // Re-read portfolio summary
        PortfolioService.PortfolioSummary updatedSummary = portfolioService.getSummaryForUser(uid);

        BigDecimal expectedHoldingsValue = newPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedHoldingsValue, updatedSummary.totalCurrentValue, "Holdings value must reflect new market price");

        BigDecimal expectedUnrealized = BigDecimal.valueOf(150.00).multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedUnrealized, updatedSummary.totalUnrealizedPnl, "Unrealized P&L must reflect gain from price surge");
        assertEquals(expectedUnrealized, updatedSummary.overallPnl, "Overall P&L must equal unrealized P&L when realized P&L is 0");

        // Verify Net Worth increased by exactly the unrealized gain
        BigDecimal expectedNetWorth = initialCash.add(expectedUnrealized).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedNetWorth, updatedSummary.netWorth, "Net worth must increase by unrealized P&L");
    }

    @Test
    @DisplayName("Verify price drop updates holding market value to negative unrealized P&L")
    public void testPriceDropUpdatesHoldingMarketValueToNegative() throws Exception {
        User user = userService.register("trader_rt2", "rt2@example.com", "Realtime Trader 2", "Pass123!");
        long uid = user.getId();
        BigDecimal initialCash = user.getCashBalance();

        Stock stock = stockService.getById(2L); // TCS
        BigDecimal buyPrice = stock.getPrice();
        int quantity = 5;
        tradingService.buy(uid, stock.getId(), quantity);

        // Simulate market price dip (-200 per share)
        BigDecimal newPrice = buyPrice.subtract(BigDecimal.valueOf(200.00)).setScale(2, RoundingMode.HALF_UP);
        Map<Long, BigDecimal> newPrices = new HashMap<>();
        newPrices.put(stock.getId(), newPrice);

        try (Connection c = DBConnection.getConnection()) {
            stockDAO.updatePricesBatch(c, newPrices);
        }
        MarketDataBroadcaster.broadcastPrices(newPrices);

        PortfolioService.PortfolioSummary updatedSummary = portfolioService.getSummaryForUser(uid);

        BigDecimal expectedLoss = BigDecimal.valueOf(-200.00).multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedLoss, updatedSummary.totalUnrealizedPnl, "Unrealized P&L must reflect negative loss");
        assertEquals(expectedLoss, updatedSummary.overallPnl, "Overall P&L must reflect net loss");

        BigDecimal expectedNetWorth = initialCash.add(expectedLoss).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedNetWorth, updatedSummary.netWorth, "Net worth must decline by unrealized loss");
    }

    @Test
    @DisplayName("Verify buy/sell executions trigger real-time trade broadcast immediately")
    public void testTradingExecutionTriggersBroadcast() throws Exception {
        User user = userService.register("trader_rt3", "rt3@example.com", "Broadcast Tester", "Pass123!");
        long uid = user.getId();

        Stock stock = stockService.getById(1L);
        int buyQty = 4;

        AtomicReference<Trade> broadcastReceived = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Registration reg = MarketDataBroadcaster.registerTradeListener(trade -> {
            broadcastReceived.set(trade);
            latch.countDown();
        });

        try {
            Trade executedTrade = tradingService.buy(uid, stock.getId(), buyQty);

            boolean received = latch.await(2, TimeUnit.SECONDS);
            assertTrue(received, "Trade broadcast must be received within timeout");
            assertNotNull(broadcastReceived.get(), "Broadcast trade must not be null");
            assertEquals(executedTrade.getId(), broadcastReceived.get().getId(), "Broadcasted trade must match executed trade ID");
            assertEquals(executedTrade.getStockSymbol(), broadcastReceived.get().getStockSymbol());
            assertEquals(executedTrade.getQuantity(), broadcastReceived.get().getQuantity());
        } finally {
            reg.remove();
        }
    }

    @Test
    @DisplayName("Verify financial invariants hold across price fluctuations and partial liquidation")
    public void testFinancialInvariantsAcrossTradingLifecycle() throws Exception {
        User user = userService.register("trader_rt4", "rt4@example.com", "Lifecycle Tester", "Pass123!");
        long uid = user.getId();
        BigDecimal initialCapital = user.getCashBalance();

        Stock stock = stockService.getById(1L);
        int initialBuyQty = 10;
        tradingService.buy(uid, stock.getId(), initialBuyQty);

        // Price changes
        BigDecimal intermediatePrice = stock.getPrice().add(BigDecimal.valueOf(100.00)).setScale(2, RoundingMode.HALF_UP);
        Map<Long, BigDecimal> priceMap = Collections.singletonMap(stock.getId(), intermediatePrice);
        try (Connection c = DBConnection.getConnection()) {
            stockDAO.updatePricesBatch(c, priceMap);
        }
        MarketDataBroadcaster.broadcastPrices(priceMap);

        // Sell 4 shares at intermediate price
        tradingService.sell(uid, stock.getId(), 4);

        // Another price change
        BigDecimal finalPrice = intermediatePrice.subtract(BigDecimal.valueOf(50.00)).setScale(2, RoundingMode.HALF_UP);
        Map<Long, BigDecimal> finalPriceMap = Collections.singletonMap(stock.getId(), finalPrice);
        try (Connection c = DBConnection.getConnection()) {
            stockDAO.updatePricesBatch(c, finalPriceMap);
        }
        MarketDataBroadcaster.broadcastPrices(finalPriceMap);

        // Fetch summary
        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(uid);

        // Invariant 1: Total Equity = Available Cash + Current Holdings Value
        BigDecimal calculatedEquity = summary.cash.add(summary.totalCurrentValue).setScale(2, RoundingMode.HALF_UP);
        assertEquals(calculatedEquity, summary.netWorth, "Invariant 1 violated: Net worth must equal Cash + Current Holdings Value");

        // Invariant 2: Overall P&L = Realized P&L + Unrealized P&L
        BigDecimal combinedPnl = summary.realizedPnl.add(summary.totalUnrealizedPnl).setScale(2, RoundingMode.HALF_UP);
        assertEquals(combinedPnl, summary.overallPnl, "Invariant 2 violated: Overall P&L must reconcile Realized + Unrealized");

        // Invariant 3: Net Worth = Initial Capital + Overall P&L
        BigDecimal equityFromPnl = initialCapital.add(summary.overallPnl).setScale(2, RoundingMode.HALF_UP);
        assertEquals(equityFromPnl, summary.netWorth, "Invariant 3 violated: Net Worth must equal Initial Capital + Overall P&L");
    }

    @Test
    @DisplayName("Verify multi-session safety and detached listener cleanup with zero leaks")
    public void testMultiSessionBroadcasterSafetyAndCleanup() {
        int initialCount = MarketDataBroadcaster.getListenerCount();

        List<Registration> registrations = new ArrayList<>();
        AtomicInteger eventCount = new AtomicInteger(0);

        // Simulate 5 connected client views
        for (int i = 0; i < 5; i++) {
            registrations.add(MarketDataBroadcaster.registerPriceListener(event -> eventCount.incrementAndGet()));
        }

        assertEquals(initialCount + 5, MarketDataBroadcaster.getListenerCount(), "Listener count must reflect 5 registered views");

        // Broadcast an event
        MarketDataBroadcaster.broadcastPrices(Collections.singletonMap(999L, BigDecimal.valueOf(100.00)));
        assertEquals(5, eventCount.get(), "All 5 sessions must receive the broadcast");

        // Detach 3 sessions
        registrations.get(0).remove();
        registrations.get(1).remove();
        registrations.get(2).remove();

        assertEquals(initialCount + 2, MarketDataBroadcaster.getListenerCount(), "Listener count must decrement upon unregister");

        // Broadcast another event
        eventCount.set(0);
        MarketDataBroadcaster.broadcastPrices(Collections.singletonMap(999L, BigDecimal.valueOf(105.00)));
        assertEquals(2, eventCount.get(), "Only active sessions must receive the subsequent broadcast");

        // Detach remaining 2
        registrations.get(3).remove();
        registrations.get(4).remove();

        assertEquals(initialCount, MarketDataBroadcaster.getListenerCount(), "Listener count must return to baseline after all views detach");
    }
}
