package com.project.trading.service;

import com.project.trading.DBCreator;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServletDataConsistencyTest {

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
    public void testDatabaseBackedPersistenceAcrossViews() throws Exception {
        User user = userService.register("unified_user", "unified@example.com", "Unified User", "Pass123!");
        long uid = user.getId();

        // 1. Buy 2 shares of Reliance (stock 1)
        Stock s1 = stockService.getById(1L);
        Trade buyTrade = tradingService.buy(uid, 1L, 2);
        assertNotNull(buyTrade);

        // 2. Query through PortfolioService (used by both JSP PortfolioServlet and Vaadin PortfolioView)
        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(uid);
        assertNotNull(summary);
        assertEquals(1, summary.rows.size());
        assertEquals("RELIANCE", summary.rows.get(0).symbol);
        assertEquals(2, summary.rows.get(0).quantity);
        assertEquals(s1.getPrice().setScale(2, RoundingMode.HALF_UP), summary.rows.get(0).avgBuyPrice);

        // 3. Query history through TradingService (used by both JSP HistoryServlet and DashboardView)
        List<Trade> history = tradingService.getHistory(uid, 10);
        assertEquals(1, history.size());
        Trade recordedTrade = history.get(0);
        assertEquals(buyTrade.getId(), recordedTrade.getId());
        assertEquals(s1.getId(), recordedTrade.getStockId());
        assertEquals(2, recordedTrade.getQuantity());
        assertEquals(buyTrade.getTotalAmount(), recordedTrade.getTotalAmount());

        // 4. Verify no session dependence: another instance of PortfolioService sees exact same state
        PortfolioService freshService = new PortfolioService();
        PortfolioService.PortfolioSummary freshSummary = freshService.getSummaryForUser(uid);
        assertEquals(summary.cash, freshSummary.cash);
        assertEquals(summary.totalInvested, freshSummary.totalInvested);
        assertEquals(summary.totalCurrentValue, freshSummary.totalCurrentValue);
        assertEquals(summary.netWorth, freshSummary.netWorth);
    }
}
