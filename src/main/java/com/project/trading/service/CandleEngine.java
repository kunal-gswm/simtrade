package com.project.trading.service;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.dao.CandleDAO;
import com.project.trading.model.Candle;
import com.project.trading.model.Timeframe;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * High-performance candle generation engine that converts discrete PriceSimulator ticks
 * into persistent OHLCV candles across multiple timeframes (1m, 5m, 15m, 1h, 1d).
 *
 * Volume Simulation Model:
 * Each price tick derives simulated volume based on:
 *   TickVolume = max(10, round(BaseVolume * (1.0 + VolatilityMultiplier) * Noise))
 * where:
 *   - BaseVolume = 40 + ((stockId * 17) % 60) [predictable per-stock baseline liquidity]
 *   - PriceDelta = |newPrice - prevPrice| / prevPrice [absolute relative price return]
 *   - VolatilityMultiplier = min(15.0, 50.0 * PriceDelta) [price shocks trigger volume surges]
 *   - Noise = 0.85 + (0.3 * random) [natural market variance]
 */
public class CandleEngine {

    public record CandleKey(long stockId, Timeframe timeframe) {
        public CandleKey {
            Objects.requireNonNull(timeframe, "Timeframe cannot be null");
        }
    }

    private final CandleDAO candleDAO;
    private final Random random = new Random();
    private final ConcurrentHashMap<CandleKey, Candle> activeCandles = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Candle> persistenceQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledExecutorService flushScheduler;

    private static final CandleEngine INSTANCE = new CandleEngine();

    public static CandleEngine getInstance() {
        return INSTANCE;
    }

    public CandleEngine() {
        this(new CandleDAO());
    }

    public CandleEngine(CandleDAO candleDAO) {
        this.candleDAO = candleDAO;
    }

    public synchronized void start(int flushIntervalSeconds) {
        if (running.compareAndSet(false, true)) {
            flushScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "CandleEngine-Flusher");
                    t.setDaemon(true);
                    return t;
                }
            });
            int interval = flushIntervalSeconds <= 0 ? 10 : flushIntervalSeconds;
            flushScheduler.scheduleWithFixedDelay(this::flushPending, interval, interval, TimeUnit.SECONDS);
        }
    }

    public synchronized void stop() {
        if (running.compareAndSet(true, false)) {
            if (flushScheduler != null) {
                flushScheduler.shutdown();
                try {
                    if (!flushScheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                        flushScheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    flushScheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                flushScheduler = null;
            }
            flushAll();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Ingests a price tick from PriceSimulator and aggregates it into all supported timeframes.
     */
    public void onPriceTick(long stockId, BigDecimal newPrice, BigDecimal prevPrice, Instant tickTime) {
        if (newPrice == null || tickTime == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        long volume = calculateTickVolume(stockId, newPrice, prevPrice);

        for (Timeframe tf : Timeframe.values()) {
            CandleKey key = new CandleKey(stockId, tf);
            Instant candleStart = tf.getCandleStart(tickTime);

            Candle candleToBroadcast = null;
            synchronized (this) {
                Candle current = activeCandles.get(key);
                if (current == null) {
                    Candle newCandle = Candle.createInitial(stockId, tf, newPrice, volume, candleStart);
                    activeCandles.put(key, newCandle);
                    candleToBroadcast = newCandle.copy();
                } else if (current.getOpenTime().equals(candleStart)) {
                    current.updateWithTick(newPrice, volume);
                    candleToBroadcast = current.copy();
                } else if (candleStart.isAfter(current.getOpenTime())) {
                    // Boundary crossed: finalize previous candle and queue for DB write
                    current.setClosed(true);
                    persistenceQueue.add(current.copy());

                    // Start new candle for new boundary
                    Candle nextCandle = Candle.createInitial(stockId, tf, newPrice, volume, candleStart);
                    activeCandles.put(key, nextCandle);
                    candleToBroadcast = nextCandle.copy();
                }
            }

            if (candleToBroadcast != null) {
                MarketDataBroadcaster.broadcastCandle(candleToBroadcast);
            }
        }
    }

    /**
     * Calculates realistic simulated trading volume for a price tick.
     */
    public long calculateTickVolume(long stockId, BigDecimal newPrice, BigDecimal prevPrice) {
        long baseVolume = 40L + ((stockId * 17L) % 60L);
        double delta = 0.0;
        if (prevPrice != null && prevPrice.compareTo(BigDecimal.ZERO) > 0) {
            delta = newPrice.subtract(prevPrice).abs()
                    .divide(prevPrice, 4, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        double volatilityMultiplier = Math.min(15.0, delta * 50.0);
        double noise = 0.85 + (0.3 * random.nextDouble());
        long calculated = Math.round(baseVolume * (1.0 + volatilityMultiplier) * noise);
        return Math.max(10L, calculated);
    }

    /**
     * Returns an active (unclosed) in-memory candle if one exists for the given stock and timeframe.
     */
    public Candle getActiveCandle(long stockId, Timeframe tf) {
        Candle c = activeCandles.get(new CandleKey(stockId, tf));
        return c != null ? c.copy() : null;
    }

    /**
     * Persists queued completed candles and snapshots active candles to MySQL.
     */
    public void flushPending() {
        try {
            List<Candle> batch = new ArrayList<>();
            Candle queued;
            while ((queued = persistenceQueue.poll()) != null) {
                batch.add(queued);
            }

            // Also snapshot active in-memory candles so DB is continuously up to date
            for (Candle active : activeCandles.values()) {
                if (active != null) {
                    batch.add(active.copy());
                }
            }

            if (!batch.isEmpty()) {
                try (Connection c = DBConnection.getConnection()) {
                    candleDAO.upsertBatch(c, batch);
                }
            }
        } catch (Throwable t) {
            System.err.println("[CandleEngine] Error flushing candles: " + t.getMessage());
        }
    }

    /**
     * Flushes all queued and active candles synchronously. Called on graceful shutdown.
     */
    public void flushAll() {
        flushPending();
    }

    /**
     * Clears in-memory active candles. Primarily for clean test isolation.
     */
    public void resetInMemoryState() {
        activeCandles.clear();
        persistenceQueue.clear();
    }
}
