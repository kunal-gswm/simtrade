package com.project.trading.demo;

import com.project.trading.dao.StockDAO;
import com.project.trading.dao.UserDAO;
import com.project.trading.model.Stock;
import com.project.trading.model.User;
import com.project.trading.service.TradingService;
import com.project.trading.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentTradeDemo {

    public static void main(String[] args) {
        System.out.println("Starting Concurrent Trade Demo...");
        
        StockDAO stockDAO = new StockDAO();
        UserDAO userDAO = new UserDAO();
        TradingService tradingService = new TradingService();
        
        long userId = 1; // Default to 1
        long stockId = 1; // Default to 1
        
        try (Connection c = DBConnection.getConnection()) {
            // Find Rahul
            String userSql = "SELECT user_id FROM users WHERE username = 'rahul'";
            try (PreparedStatement ps = c.prepareStatement(userSql)) {
                var rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getLong("user_id");
                }
            }
            
            // Find RELIANCE
            String stockSql = "SELECT stock_id FROM stocks WHERE symbol = 'RELIANCE'";
            try (PreparedStatement ps = c.prepareStatement(stockSql)) {
                var rs = ps.executeQuery();
                if (rs.next()) {
                    stockId = rs.getLong("stock_id");
                }
            }
            
            // Setup demo data if doesn't exist
            System.out.println("Using User ID: " + userId + ", Stock ID: " + stockId);
            
            int numThreads = 10;
            int qty = 15;
            
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Callable<String>> tasks = new ArrayList<>();
            
            for (int i = 0; i < numThreads; i++) {
                final int threadNum = i + 1;
                final long uId = userId;
                final long sId = stockId;
                
                tasks.add(() -> {
                    try {
                        tradingService.buy(uId, sId, qty);
                        return "Thread " + threadNum + ": SUCCESS";
                    } catch (Exception e) {
                        return "Thread " + threadNum + ": FAILED - " + e.getMessage();
                    }
                });
            }
            
            System.out.println("Executing " + numThreads + " concurrent buy orders...");
            List<Future<String>> results = executor.invokeAll(tasks);
            
            int successCount = 0;
            int failCount = 0;
            
            for (Future<String> result : results) {
                String outcome = result.get();
                System.out.println(outcome);
                if (outcome.contains("SUCCESS")) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            executor.shutdown();
            
            System.out.println("\nDemo Completed.");
            System.out.println("Successes: " + successCount);
            System.out.println("Failures: " + failCount);
            
            // Print final cash
            String finalCashSql = "SELECT cash_balance FROM users WHERE user_id = ?";
            try (PreparedStatement ps = c.prepareStatement(finalCashSql)) {
                ps.setLong(1, userId);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("Final Cash Balance: " + rs.getBigDecimal("cash_balance"));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
