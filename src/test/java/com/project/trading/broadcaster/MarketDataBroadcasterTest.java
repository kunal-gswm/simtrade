package com.project.trading.broadcaster;

import com.project.trading.model.Trade;
import com.project.trading.model.TradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class MarketDataBroadcasterTest {

    @BeforeEach
    public void setup() {
        MarketDataBroadcaster.clearListeners();
    }

    @Test
    public void testRegisterAndUnregister() {
        assertEquals(0, MarketDataBroadcaster.getListenerCount());

        AtomicInteger callCount = new AtomicInteger(0);
        MarketDataBroadcaster.Registration reg = MarketDataBroadcaster.register(new MarketDataBroadcaster.BroadcastListener() {
            @Override
            public void onPriceUpdate(MarketDataBroadcaster.PriceUpdate event) {
                callCount.incrementAndGet();
            }
        });

        assertEquals(1, MarketDataBroadcaster.getListenerCount());

        MarketDataBroadcaster.broadcastPrices(Map.of(1L, new BigDecimal("2500.00")));
        assertEquals(1, callCount.get());

        reg.remove();
        assertEquals(0, MarketDataBroadcaster.getListenerCount());

        MarketDataBroadcaster.broadcastPrices(Map.of(1L, new BigDecimal("2600.00")));
        assertEquals(1, callCount.get(), "Unregistered listener must not receive further events");
    }

    @Test
    public void testAutomaticPruningOnDeliveryFailure() {
        assertEquals(0, MarketDataBroadcaster.getListenerCount());

        AtomicBoolean healthyCalled = new AtomicBoolean(false);

        // Healthy listener
        MarketDataBroadcaster.register(new MarketDataBroadcaster.BroadcastListener() {
            @Override
            public void onPriceUpdate(MarketDataBroadcaster.PriceUpdate event) {
                healthyCalled.set(true);
            }
        });

        // Failing / Detached listener
        MarketDataBroadcaster.register(new MarketDataBroadcaster.BroadcastListener() {
            @Override
            public void onPriceUpdate(MarketDataBroadcaster.PriceUpdate event) {
                throw new IllegalStateException("UI is detached from session");
            }
        });

        assertEquals(2, MarketDataBroadcaster.getListenerCount());

        // Broadcast should deliver to healthy and automatically purge the failing one
        MarketDataBroadcaster.broadcastPrices(Map.of(1L, new BigDecimal("1500.00")));

        assertTrue(healthyCalled.get(), "Healthy listener must receive event");
        assertEquals(1, MarketDataBroadcaster.getListenerCount(), "Failing listener must be pruned from broadcaster");
    }

    @Test
    public void testTradeBroadcastAndFailingPruning() {
        assertEquals(0, MarketDataBroadcaster.getListenerCount());

        AtomicBoolean receivedTrade = new AtomicBoolean(false);

        MarketDataBroadcaster.registerTradeListener(trade -> {
            receivedTrade.set(true);
        });

        MarketDataBroadcaster.registerTradeListener(trade -> {
            throw new RuntimeException("Simulated socket disconnect");
        });

        assertEquals(2, MarketDataBroadcaster.getListenerCount());

        Trade trade = new Trade();
        trade.setId(101L);
        trade.setType(TradeType.BUY);
        trade.setPrice(new BigDecimal("100.00"));

        MarketDataBroadcaster.broadcastTrade(trade);

        assertTrue(receivedTrade.get());
        assertEquals(1, MarketDataBroadcaster.getListenerCount(), "Failed trade listener must be purged");
    }

    @Test
    public void testConcurrentRegistrationAndBroadcastThreadSafety() throws Exception {
        int threads = 10;
        int operations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operations; j++) {
                        MarketDataBroadcaster.Registration reg = MarketDataBroadcaster.register(new MarketDataBroadcaster.BroadcastListener() {});
                        MarketDataBroadcaster.broadcastPrices(Map.of(1L, new BigDecimal("100.00")));
                        reg.remove();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean finished = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(finished, "Concurrent operations must finish within timeout");
        assertEquals(0, MarketDataBroadcaster.getListenerCount(), "All listeners should have been removed cleanly");
    }
}
