package com.project.trading.service;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.model.Candle;
import com.project.trading.model.Timeframe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CandleEngineTest {

    private CandleEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new CandleEngine();
        engine.resetInMemoryState();
    }

    @Test
    public void testCandleOHLCProgression() {
        long stockId = 1L;
        Instant t0 = Instant.parse("2026-11-13T10:00:10Z");

        // Tick 1: Open = 100.00
        engine.onPriceTick(stockId, new BigDecimal("100.00"), new BigDecimal("100.00"), t0);
        Candle c1 = engine.getActiveCandle(stockId, Timeframe.ONE_MIN);
        assertNotNull(c1);
        assertEquals(new BigDecimal("100.00"), c1.getOpenPrice());
        assertEquals(new BigDecimal("100.00"), c1.getHighPrice());
        assertEquals(new BigDecimal("100.00"), c1.getLowPrice());
        assertEquals(new BigDecimal("100.00"), c1.getClosePrice());
        assertTrue(c1.getVolume() > 0);
        assertFalse(c1.isClosed());

        // Tick 2: High moves to 105.50
        Instant t1 = t0.plusSeconds(5);
        engine.onPriceTick(stockId, new BigDecimal("105.50"), new BigDecimal("100.00"), t1);
        Candle c2 = engine.getActiveCandle(stockId, Timeframe.ONE_MIN);
        assertEquals(new BigDecimal("100.00"), c2.getOpenPrice(), "Open must stay first observed price");
        assertEquals(new BigDecimal("105.50"), c2.getHighPrice(), "High must track maximum price");
        assertEquals(new BigDecimal("100.00"), c2.getLowPrice(), "Low must track minimum price");
        assertEquals(new BigDecimal("105.50"), c2.getClosePrice(), "Close must track latest price");

        // Tick 3: Low moves down to 96.25
        Instant t2 = t1.plusSeconds(5);
        engine.onPriceTick(stockId, new BigDecimal("96.25"), new BigDecimal("105.50"), t2);
        Candle c3 = engine.getActiveCandle(stockId, Timeframe.ONE_MIN);
        assertEquals(new BigDecimal("100.00"), c3.getOpenPrice());
        assertEquals(new BigDecimal("105.50"), c3.getHighPrice());
        assertEquals(new BigDecimal("96.25"), c3.getLowPrice(), "Low must update to lower price");
        assertEquals(new BigDecimal("96.25"), c3.getClosePrice());

        // Tick 4: Rebound to 98.75
        Instant t3 = t2.plusSeconds(5);
        engine.onPriceTick(stockId, new BigDecimal("98.75"), new BigDecimal("96.25"), t3);
        Candle c4 = engine.getActiveCandle(stockId, Timeframe.ONE_MIN);
        assertEquals(new BigDecimal("100.00"), c4.getOpenPrice());
        assertEquals(new BigDecimal("105.50"), c4.getHighPrice());
        assertEquals(new BigDecimal("96.25"), c4.getLowPrice());
        assertEquals(new BigDecimal("98.75"), c4.getClosePrice());
    }

    @Test
    public void testBoundaryRollover() {
        long stockId = 2L;
        Instant t0 = Instant.parse("2026-11-13T10:00:15Z");

        // First candle during 10:00 window
        engine.onPriceTick(stockId, new BigDecimal("250.00"), new BigDecimal("250.00"), t0);
        Candle bar1 = engine.getActiveCandle(stockId, Timeframe.ONE_MIN);
        assertEquals(Instant.parse("2026-11-13T10:00:00Z"), bar1.getOpenTime());

        // Cross boundary into 10:01 window (65 seconds later)
        Instant t1 = t0.plusSeconds(65);
        engine.onPriceTick(stockId, new BigDecimal("254.00"), new BigDecimal("250.00"), t1);
        Candle bar2 = engine.getActiveCandle(stockId, Timeframe.ONE_MIN);
        assertEquals(Instant.parse("2026-11-13T10:01:00Z"), bar2.getOpenTime());
        assertEquals(new BigDecimal("254.00"), bar2.getOpenPrice(), "New candle open must match boundary tick");
    }

    @Test
    public void testMultiTimeframeAggregation() {
        long stockId = 3L;
        Instant t0 = Instant.parse("2026-11-13T10:14:30Z");

        engine.onPriceTick(stockId, new BigDecimal("500.00"), new BigDecimal("500.00"), t0);

        // Every supported timeframe must have an active candle
        for (Timeframe tf : Timeframe.values()) {
            Candle c = engine.getActiveCandle(stockId, tf);
            assertNotNull(c, "Engine must track candle for timeframe " + tf.getCode());
            assertEquals(tf, c.getTimeframe());
            assertEquals(new BigDecimal("500.00"), c.getClosePrice());
            assertEquals(tf.getCandleStart(t0), c.getOpenTime());
        }
    }

    @Test
    public void testVolumeModel() {
        long stockId = 5L;
        BigDecimal price = new BigDecimal("1000.00");
        BigDecimal calmPrev = new BigDecimal("1000.10"); // tiny delta
        BigDecimal shockPrev = new BigDecimal("950.00");   // ~5% shock

        long calmVol = engine.calculateTickVolume(stockId, price, calmPrev);
        long shockVol = engine.calculateTickVolume(stockId, price, shockPrev);

        assertTrue(calmVol >= 10, "Volume must be at least minimum threshold");
        assertTrue(shockVol >= calmVol, "Price shock should elevate trading volume");
    }

    @Test
    public void testLiveCandleBroadcasterRegistration() {
        long stockId = 7L;
        Instant t0 = Instant.parse("2026-11-13T12:00:00Z");

        AtomicInteger eventCount = new AtomicInteger(0);
        AtomicReference<Candle> lastReceived = new AtomicReference<>();

        MarketDataBroadcaster.Registration reg = MarketDataBroadcaster.registerCandleListener(candle -> {
            if (candle.getStockId() == stockId) {
                eventCount.incrementAndGet();
                lastReceived.set(candle);
            }
        });

        try {
            engine.onPriceTick(stockId, new BigDecimal("123.45"), new BigDecimal("123.00"), t0);
            assertTrue(eventCount.get() > 0, "Broadcast listener must receive candle updates");
            assertNotNull(lastReceived.get());
            assertEquals(new BigDecimal("123.45"), lastReceived.get().getClosePrice());
        } finally {
            reg.remove();
        }
    }
}
