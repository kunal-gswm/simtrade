package com.project.trading.service;

import com.project.trading.exception.DataAccessException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.model.Trade;
import com.project.trading.util.FaultInjector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class TradingServiceTest {

    private TradingService tradingService;

    @BeforeEach
    public void setup() throws Exception {
        com.project.trading.DBCreator.main(null);
        tradingService = new TradingService();
        FaultInjector.failMidTrade = false;
    }

    @AfterEach
    public void teardown() {
        FaultInjector.failMidTrade = false;
    }

    // BUY Tests
    @Test
    
    public void testBuy01_ValidBuy() {
        assertDoesNotThrow(() -> {
            tradingService.buy(2, 11, 10); // WIPRO (cost 4800), leaves enough balance
        });
    }

    @Test
    
    public void testBuy03_InsufficientBalance() {
        assertThrows(com.project.trading.exception.InsufficientBalanceException.class, () -> {
            tradingService.buy(2, 2, 1000); // User 2 has 14500, 1000 shares of 3900 is 3.9 million
        });
    }

    @Test
    public void testBuy05_InvalidQuantity() {
        assertThrows(InvalidOrderException.class, () -> {
            tradingService.buy(2, 2, 0);
        });
    }

    // SELL Tests
    @Test
    
    public void testSell01_ValidPartialSell() {
        assertDoesNotThrow(() -> {
            tradingService.sell(3, 2, 5);
        });
    }

    @Test
    public void testSell05_InvalidQuantity() {
        assertThrows(InvalidOrderException.class, () -> {
            tradingService.sell(2, 1, 0);
        });
    }

    // Transactions and Concurrency
    @Test
    
    public void testTx01_InjectedFailureOnBuyRollsBack() {
        FaultInjector.failMidTrade = true;
        assertThrows(DataAccessException.class, () -> {
            tradingService.buy(2, 11, 10); // WIPRO
        });
    }

    @Test
    
    public void testTx02_InjectedFailureOnSellRollsBack() {
        FaultInjector.failMidTrade = true;
        assertThrows(DataAccessException.class, () -> {
            tradingService.sell(3, 3, 10);
        });
    }

    @Test
    
    public void testCon01_ConcurrentBuy() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<Trade>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> tradingService.buy(2, 1, 15));
        }
        
        List<Future<Trade>> results = executor.invokeAll(tasks);
        int successCount = 0;
        int failCount = 0;
        
        for (Future<Trade> f : results) {
            try {
                f.get();
                successCount++;
            } catch (Exception e) {
                e.printStackTrace();
                failCount++;
            }
        }
        
        assertEquals(2, successCount, "Exactly 2 out of 10 buys should succeed");
        assertEquals(8, failCount, "Exactly 8 out of 10 buys should fail due to insufficient funds");
        
        try (java.sql.Connection c = com.project.trading.util.DBConnection.getConnection()) {
            com.project.trading.dao.UserDAO userDAO = new com.project.trading.dao.UserDAO();
            com.project.trading.dao.HoldingDAO holdingDAO = new com.project.trading.dao.HoldingDAO();
            
            // Check final cash
            java.math.BigDecimal cash = userDAO.findById(c, 2).getCashBalance();
            assertEquals(0, new java.math.BigDecimal("14500.00").compareTo(cash), "Final cash should be exactly 14500.00");
            
            // Check final holdings (2 * 15 = 30 shares of stock 1)
            com.project.trading.model.Holding holding = holdingDAO.findByUserAndStock(c, 2, 1);
            assertNotNull(holding, "Holding should exist");
            assertEquals(30, holding.getQuantity(), "Holding quantity should be 30");
            
            // Check trade history
            java.util.List<com.project.trading.model.Trade> trades = tradingService.getHistory(2, 100);
            long stock1Trades = trades.stream().filter(t -> t.getStockId() == 1).count();
            assertEquals(2, stock1Trades, "Exactly 2 trades for stock 1 should exist in history");
        }
        
        executor.shutdown();
    }
}
