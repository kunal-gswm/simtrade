package com.project.trading;

import com.project.trading.exception.AppException;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;

public class TestVerifier {
    public static void main(String[] args) {
        System.out.println("Starting backend verification...");
        try {
            TradingService tradingService = new TradingService();
            PortfolioService portfolioService = new PortfolioService();
            
            long userId = 2; // rahul has cash 100000
            
            // 1. Initial State
            Portfolio portfolio = portfolioService.getPortfolioForUser(userId);
            PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(portfolio);
            
            System.out.println("Initial Cash: " + summary.cash);
            System.out.println("Initial Holdings: " + summary.rows.size());
            
            // 2. Buy valid quantity of stock 1 (Reliance, price ~2850)
            long stockId = 1;
            int buyQty = 2;
            Trade buyTrade = tradingService.buy(userId, stockId, buyQty);
            System.out.println("Bought " + buyQty + " shares. Amount: " + buyTrade.getTotalAmount());
            
            // 3. Verify state after buy
            portfolio = portfolioService.getPortfolioForUser(userId);
            summary = portfolioService.buildSummary(portfolio);
            System.out.println("Cash after buy: " + summary.cash);
            
            // 4. Exceed cash
            try {
                tradingService.buy(userId, stockId, 1000000);
                System.out.println("FAIL: Allowed buying exceeding cash!");
            } catch (AppException e) {
                System.out.println("PASS: Blocked buying exceeding cash: " + e.getMessage());
            }
            
            // 5. Sell valid quantity
            Trade sellTrade = tradingService.sell(userId, stockId, 1);
            System.out.println("Sold 1 share. Amount: " + sellTrade.getTotalAmount());
            
            // 6. Sell exceeding quantity
            try {
                tradingService.sell(userId, stockId, 1000);
                System.out.println("FAIL: Allowed selling more than held!");
            } catch (AppException e) {
                System.out.println("PASS: Blocked selling exceeding holdings: " + e.getMessage());
            }

            // 7. Verify calculation logic
            portfolio = portfolioService.getPortfolioForUser(userId);
            summary = portfolioService.buildSummary(portfolio);
            System.out.println("Final Cash: " + summary.cash);
            System.out.println("Final Invested: " + summary.totalInvested);
            System.out.println("Final Current Value: " + summary.totalCurrentValue);
            System.out.println("Final Net Worth: " + summary.netWorth);
            
            System.out.println("Verification Complete.");
            
            // Clean up by selling the last share so cash returns to normal for tests
            tradingService.sell(userId, stockId, 1);
            System.out.println("Cleaned up remaining shares.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
