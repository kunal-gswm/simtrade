package com.project.trading.service;

import com.project.trading.DBCreator;
import com.project.trading.model.Trade;
import com.project.trading.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardDataTest {

    private TradingService tradingService;
    private PortfolioService portfolioService;
    private UserService userService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        tradingService = new TradingService();
        portfolioService = new PortfolioService();
        userService = new UserService();
    }

    @Test
    public void testRecentTrades_IsolationAndLimit() throws Exception {
        // User 2 (rahul) does trades
        tradingService.buy(2, 1, 1);
        tradingService.buy(2, 2, 2);
        tradingService.buy(2, 3, 3);
        tradingService.buy(2, 4, 4);
        tradingService.buy(2, 5, 5);
        tradingService.buy(2, 6, 6); // 6 trades total for user 2

        // User 3 (priya) does trades
        tradingService.buy(3, 1, 1);

        List<Trade> rahulTrades = tradingService.getHistory(2, 5);
        assertEquals(5, rahulTrades.size(), "Should limit to 5 most recent trades");
        
        // Assert descending order (newest first, which was stock 6, 5, 4, 3, 2)
        assertEquals(6L, rahulTrades.get(0).getStockId());
        assertEquals(5L, rahulTrades.get(1).getStockId());
        
        // Assert isolation
        for (Trade t : rahulTrades) {
            assertEquals(2L, t.getUserId(), "Trade must belong to user 2");
        }
    }

    @Test
    public void testEmptyPortfolioMetrics() throws Exception {
        // Create new user with no trades
        User user = userService.register("emptydash", "empty@example.com", "Empty", "Pass123!");
        
        PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(
                portfolioService.getPortfolioForUser(user.getId()));
        
        assertEquals(100000.0, summary.cash, 0.01);
        assertEquals(0.0, summary.totalCurrentValue, 0.01);
        assertEquals(0.0, summary.totalInvested, 0.01);
        assertEquals(100000.0, summary.netWorth, 0.01); // 100k cash + 0 stock
        assertEquals(0.0, summary.overallPnl, 0.01);
    }
}
