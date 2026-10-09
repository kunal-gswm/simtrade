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
    public void setup() {
        tradingService = new TradingService();
        FaultInjector.failMidTrade = false;
    }

    @AfterEach
    public void teardown() {
        FaultInjector.failMidTrade = false;
    }

    // BUY Tests
    @Test
    @Disabled("Pending Dev 2 and 3 real DAOs (UserDAO/StockDAO)")
    public void testBuy01_ValidBuy() {
        assertDoesNotThrow(() -> {
            tradingService.buy(3, 2, 10);
        });
    }

    @Test
    @Disabled("Pending Dev 2 and 3 real DAOs")
    public void testBuy03_InsufficientBalance() {
        // Assert InsufficientBalanceException in real test
    }

    @Test
    public void testBuy05_InvalidQuantity() {
        assertThrows(InvalidOrderException.class, () -> {
            tradingService.buy(2, 2, 0);
        });
    }

    // SELL Tests
    @Test
    @Disabled("Pending Dev 2 and 3 real DAOs")
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
    @Disabled("Pending MySQL connection and populated DAOs")
    public void testTx01_InjectedFailureOnBuyRollsBack() {
        FaultInjector.failMidTrade = true;
        assertThrows(DataAccessException.class, () -> {
            tradingService.buy(2, 2, 10);
        });
    }

    @Test
    @Disabled("Pending MySQL connection and populated DAOs")
    public void testTx02_InjectedFailureOnSellRollsBack() {
        FaultInjector.failMidTrade = true;
        assertThrows(DataAccessException.class, () -> {
            tradingService.sell(3, 3, 10);
        });
    }

    @Test
    @Disabled("Pending MySQL connection and populated DAOs")
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
                failCount++;
            }
        }
        
        assertEquals(2, successCount);
        assertEquals(8, failCount);
        executor.shutdown();
    }
}
