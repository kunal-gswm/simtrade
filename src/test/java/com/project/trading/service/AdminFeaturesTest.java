package com.project.trading.service;

import com.project.trading.DBCreator;
import com.project.trading.exception.AuthenticationException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.PlatformStats;
import com.project.trading.model.Role;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.User;
import com.project.trading.simulator.PriceSimulator;
import com.project.trading.util.FaultInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminFeaturesTest {

    private UserService userService;
    private StockService stockService;
    private AdminService adminService;
    private TradingService tradingService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        userService = new UserService();
        stockService = new StockService();
        adminService = new AdminService();
        tradingService = new TradingService();
    }

    @Test
    public void testBlockedUserCannotAuthenticate() throws Exception {
        User admin = userService.getById(1); // admin seeded at ID 1
        User user = userService.getById(2); // rahul seeded at ID 2

        // Block user
        userService.setBlocked(admin.getId(), user.getId(), true);

        // Try to authenticate
        assertThrows(AuthenticationException.class, () -> {
            userService.login(user.getUsername(), "Demo@123");
        }, "Blocked user should not be able to log in");

        // Unblock user
        userService.setBlocked(admin.getId(), user.getId(), false);
        
        // Should authenticate now
        assertDoesNotThrow(() -> {
            userService.login(user.getUsername(), "Demo@123");
        });
    }

    @Test
    public void testStockManagement() throws Exception {
        // Add valid stock
        Stock newStock = stockService.add("NEWSTOCK", "New Company", "IT", "Desc", new BigDecimal("100.50"));
        assertNotNull(newStock);
        assertTrue(newStock.isActive());

        // Duplicate symbol
        assertThrows(ValidationException.class, () -> {
            stockService.add("NEWSTOCK", "Another", "IT", "", new BigDecimal("200.00"));
        });

        // Activation toggle
        stockService.setActive(newStock.getId(), false);
        Stock inactiveStock = stockService.getById(newStock.getId());
        assertFalse(inactiveStock.isActive());

        // Manual price update validation
        assertThrows(ValidationException.class, () -> {
            stockService.updatePrice(newStock.getId(), new BigDecimal("-10.00")); // negative price
        });
        
        stockService.updatePrice(newStock.getId(), new BigDecimal("150.00"));
        Stock updatedPriceStock = stockService.getById(newStock.getId());
        assertEquals(new BigDecimal("150.00"), updatedPriceStock.getPrice());
    }

    @Test
    public void testGlobalTradeMonitor() throws Exception {
        // Run some trades
        tradingService.buy(2, 1, 1);
        tradingService.buy(2, 2, 1);
        tradingService.buy(2, 3, 1);
        
        List<Trade> recent = adminService.getRecentTrades(200);
        assertTrue(recent.size() >= 3);
        
        // Newest first
        assertTrue(recent.get(0).getExecutedAt().getTime() >= recent.get(1).getExecutedAt().getTime());
    }

    @Test
    public void testFaultInjectionRollback() throws Exception {
        User user = userService.getById(2);
        BigDecimal initialCash = user.getCashBalance();
        
        FaultInjector.failMidTrade = true;
        try {
            assertThrows(RuntimeException.class, () -> {
                tradingService.buy(2, 1, 10);
            });
        } finally {
            FaultInjector.failMidTrade = false;
        }

        // Cash should be unchanged
        User userAfter = userService.getById(2);
        assertEquals(initialCash, userAfter.getCashBalance(), "Cash should rollback after fault injection");
    }

    @Test
    public void testSimulatorIdempotency() {
        PriceSimulator simulator = new PriceSimulator();
        assertFalse(simulator.isRunning());
        
        simulator.start(5);
        assertTrue(simulator.isRunning());
        
        simulator.start(5); // idempotent
        assertTrue(simulator.isRunning());
        
        simulator.stop();
        assertFalse(simulator.isRunning());
        
        simulator.stop(); // idempotent
        assertFalse(simulator.isRunning());
    }
}
