package com.project.trading.order;

import com.project.trading.DBCreator;
import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.dhan.DhanLiveMarketDataProvider;
import com.project.trading.marketdata.service.InstrumentMappingService;
import com.project.trading.order.dao.OrderDAO;
import com.project.trading.order.engine.PaperExecutionEngine;
import com.project.trading.order.model.BracketRole;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderService;
import com.project.trading.service.PortfolioService;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 5 Failure & Concurrency Audit Tests:
 * - Duplicate & out-of-order tick protection
 * - Concurrent bracket exit mutual exclusion (OCO race conditions)
 * - Engine restart recovery of pending/open orders
 * - Concurrent multi-threaded order submissions and double-spending protection
 */
public class FailureAndConcurrencyAuditTest {

    private PaperExecutionEngine engine;
    private OrderService orderService;
    private OrderDAO orderDAO;
    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() throws Exception {
        DBCreator.main(null);
        engine = new PaperExecutionEngine();
        orderService = new OrderService();
        orderDAO = new OrderDAO();
        portfolioService = new PortfolioService();
    }

    private MarketQuote createQuote(long stockId, String symbol, BigDecimal ltp, BigDecimal bid, BigDecimal ask, Instant exchangeTime) {
        return new MarketQuote(
                stockId,
                symbol,
                "NSE",
                "NSE_EQ",
                MarketDataSource.LIVE_PROVIDER,
                exchangeTime != null ? exchangeTime : Instant.now(),
                Instant.now(),
                ltp,
                ltp,
                ltp, ltp, ltp,
                100000L,
                bid, 1000,
                ask, 1000,
                false,
                "VALID"
        );
    }

    private byte[] createBinaryTickerPacket(int securityId, float ltp, int lttSec) {
        ByteBuffer buf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 2);          // Response code TICKER
        buf.putShort((short) 16);   // Message length
        buf.put((byte) 1);          // Segment NSE_EQ
        buf.putInt(securityId);     // Security ID
        buf.putFloat(ltp);          // LTP
        buf.putInt(lttSec);         // LTT in seconds
        return buf.array();
    }

    @Test
    public void testDuplicateAndOutOfOrderTicksRejected() {
        DhanLiveMarketDataProvider provider = new DhanLiveMarketDataProvider(
                "dummyClient", "dummyToken", false, InstrumentMappingService.getInstance()
        );

        // Packet 1: Time T = 100, LTP = 2850.00 (RELIANCE = secId 1333, stockId 1)
        byte[] packet1 = createBinaryTickerPacket(1333, 2850.00f, 100);
        provider.ingestBinaryPacket(packet1);

        MarketQuote q1 = provider.getLatestQuote(1L);
        assertNotNull(q1);
        assertEquals(new BigDecimal("2850.00"), q1.ltp());
        assertEquals(Instant.ofEpochSecond(100), q1.exchangeTimestamp());

        // Packet 2: Out of order! Time T = 90 (< 100), LTP = 2840.00
        byte[] packetOld = createBinaryTickerPacket(1333, 2840.00f, 90);
        provider.ingestBinaryPacket(packetOld);

        // Verify out-of-order packet was rejected: latest quote remains T = 100, LTP = 2850.00
        MarketQuote qAfterOld = provider.getLatestQuote(1L);
        assertEquals(new BigDecimal("2850.00"), qAfterOld.ltp(), "Out-of-order tick must be rejected");
        assertEquals(Instant.ofEpochSecond(100), qAfterOld.exchangeTimestamp());

        // Packet 3: Newer tick Time T = 110, LTP = 2860.00 -> Accepted!
        byte[] packetNew = createBinaryTickerPacket(1333, 2860.00f, 110);
        provider.ingestBinaryPacket(packetNew);

        MarketQuote qNew = provider.getLatestQuote(1L);
        assertEquals(new BigDecimal("2860.00"), qNew.ltp());
        assertEquals(Instant.ofEpochSecond(110), qNew.exchangeTimestamp());
    }

    @Test
    public void testConcurrentBracketExitTriggers_MutualExclusion() throws Exception {
        // User 2 places Bracket Buy order for Stock 1 (RELIANCE): limit 2800, target 2900, stopLoss 2750
        Order bracket = new Order();
        bracket.setUserId(2L);
        bracket.setStockId(1L);
        bracket.setSide(OrderSide.BUY);
        bracket.setOrderType(OrderType.BRACKET);
        bracket.setQuantity(5);
        bracket.setRemainingQuantity(5);
        bracket.setLimitPrice(new BigDecimal("2800.00"));
        bracket.setTargetPrice(new BigDecimal("2900.00"));
        bracket.setStopLossPrice(new BigDecimal("2750.00"));
        bracket.setBracketRole(BracketRole.ENTRY);
        bracket.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(bracket);

        // Fill entry order
        MarketQuote entryQuote = createQuote(1L, "RELIANCE", new BigDecimal("2795.00"), new BigDecimal("2794.90"), new BigDecimal("2795.00"), Instant.now());
        engine.evaluateEligibleOrders(entryQuote);

        long targetOrderId;
        long slOrderId;
        try (Connection c = DBConnection.getConnection()) {
            List<Order> children = orderDAO.findChildOrders(c, placed.getId());
            assertEquals(2, children.size());
            targetOrderId = children.stream().filter(o -> o.getBracketRole() == BracketRole.TARGET_EXIT).findFirst().orElseThrow().getId();
            slOrderId = children.stream().filter(o -> o.getBracketRole() == BracketRole.STOP_LOSS_EXIT).findFirst().orElseThrow().getId();
        }

        // Now simulate a market shock that triggers both target (e.g. quote high) and stop-loss (quote low)
        // simultaneously across 2 concurrent threads to test mutual exclusion!
        MarketQuote targetHitQuote = createQuote(1L, "RELIANCE", new BigDecimal("2910.00"), new BigDecimal("2910.00"), new BigDecimal("2910.10"), Instant.now());
        MarketQuote slHitQuote = createQuote(1L, "RELIANCE", new BigDecimal("2740.00"), new BigDecimal("2740.00"), new BigDecimal("2740.10"), Instant.now());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Future<?> f1 = executor.submit(() -> {
            try {
                startLatch.await();
                engine.evaluateSingleOrder(targetHitQuote, targetOrderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Future<?> f2 = executor.submit(() -> {
            try {
                startLatch.await();
                engine.evaluateSingleOrder(slHitQuote, slOrderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        startLatch.countDown();
        f1.get(5, TimeUnit.SECONDS);
        f2.get(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify Mutual Exclusion Invariants:
        // Exactly one child must be FILLED, and the other must be CANCELLED!
        try (Connection c = DBConnection.getConnection()) {
            Order targetCheck = orderDAO.findById(c, targetOrderId);
            Order slCheck = orderDAO.findById(c, slOrderId);

            boolean targetFilled = targetCheck.getStatus() == OrderStatus.FILLED;
            boolean slFilled = slCheck.getStatus() == OrderStatus.FILLED;

            assertTrue(targetFilled ^ slFilled, "Exactly ONE exit order must be FILLED; never both (mutual exclusion)!");
            if (targetFilled) {
                assertEquals(OrderStatus.CANCELLED, slCheck.getStatus(), "Sibling SL order must be CANCELLED");
            } else {
                assertEquals(OrderStatus.CANCELLED, targetCheck.getStatus(), "Sibling Target order must be CANCELLED");
            }

            // Verify holdings never went negative or double sold
            var summary = portfolioService.getSummaryForUser(2L);
            var relianceOpt = summary.rows.stream().filter(h -> h.stockId == 1L).findFirst();
            // Since User 2 had 0 RELIANCE initially, bought 5, and sold 5 via one exit leg, holding should be 0 (deleted)
            assertTrue(relianceOpt.isEmpty(), "Holding must be completely closed with 0 remaining shares");
        }
    }

    @Test
    public void testRestartRecovery_PendingOrdersProcessedAfterRestart() throws Exception {
        // Place Limit Buy order with original engine
        Order order = new Order();
        order.setUserId(2L);
        order.setStockId(1L);
        order.setSide(OrderSide.BUY);
        order.setOrderType(OrderType.LIMIT);
        order.setQuantity(10);
        order.setRemainingQuantity(10);
        order.setLimitPrice(new BigDecimal("2800.00"));
        order.setValidity(OrderValidity.DAY);

        Order placed = orderService.placeOrder(order);
        assertEquals(OrderStatus.OPEN, placed.getStatus());

        // Simulate crash / application restart: stop engine, instantiate brand new engine
        engine.stop();
        PaperExecutionEngine recoveredEngine = new PaperExecutionEngine();
        recoveredEngine.start();

        // Matching quote arrives to the recovered engine
        MarketQuote matchQuote = createQuote(1L, "RELIANCE", new BigDecimal("2790.00"), new BigDecimal("2789.90"), new BigDecimal("2790.00"), Instant.now());
        recoveredEngine.onQuote(matchQuote);

        try (Connection c = DBConnection.getConnection()) {
            Order check = orderDAO.findById(c, placed.getId());
            assertEquals(OrderStatus.FILLED, check.getStatus(), "Persistent order must be recovered and filled after restart");
            assertEquals(10, check.getFilledQuantity());
        } finally {
            recoveredEngine.stop();
        }
    }

    @Test
    public void testConcurrentOrderPlacements_DoubleSpendingProtection() throws Exception {
        // User 3 has 21,600 cash.
        // 10 concurrent threads try to place Limit Buy orders for 10 shares @ 1500 = ₹15,000 each.
        // Only 1 order (requiring 15,000 out of 21,600) can succeed; the rest must fail due to InsufficientBalance!
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            tasks.add(() -> {
                startLatch.await();
                try {
                    Order o = new Order();
                    o.setUserId(3L);
                    o.setStockId(3L);
                    o.setSide(OrderSide.BUY);
                    o.setOrderType(OrderType.LIMIT);
                    o.setQuantity(10);
                    o.setRemainingQuantity(10);
                    o.setLimitPrice(new BigDecimal("1500.00"));
                    o.setValidity(OrderValidity.DAY);
                    orderService.placeOrder(o);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
        }

        List<Future<Boolean>> futures = new ArrayList<>();
        for (Callable<Boolean> task : tasks) {
            futures.add(executor.submit(task));
        }

        startLatch.countDown();

        int successCount = 0;
        int failCount = 0;
        for (Future<Boolean> f : futures) {
            if (f.get(5, TimeUnit.SECONDS)) {
                successCount++;
            } else {
                failCount++;
            }
        }
        executor.shutdown();

        assertEquals(1, successCount, "Exactly 1 order can be funded by ₹21,600 balance");
        assertEquals(9, failCount, "9 concurrent attempts must be rejected with insufficient funds");

        var summary = portfolioService.getSummaryForUser(3L);
        assertEquals(new BigDecimal("21600.00"), summary.cash);
        assertEquals(new BigDecimal("15000.00"), summary.reservedCash);
        assertEquals(new BigDecimal("6600.00"), summary.availableCash);
        assertTrue(summary.availableCash.compareTo(BigDecimal.ZERO) >= 0, "Available cash must never be negative");
    }
}
