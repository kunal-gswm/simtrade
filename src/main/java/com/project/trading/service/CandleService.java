package com.project.trading.service;

import com.project.trading.dao.CandleDAO;
import com.project.trading.dao.StockDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.Candle;
import com.project.trading.model.Stock;
import com.project.trading.model.Timeframe;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Service orchestrating historical OHLCV queries, parameter validation,
 * seamless active candle merging, and realistic initial history seeding.
 */
public class CandleService {

    private final CandleDAO candleDAO;
    private final StockDAO stockDAO;
    private final CandleEngine candleEngine;

    public CandleService() {
        this(new CandleDAO(), new StockDAO(), CandleEngine.getInstance());
    }

    public CandleService(CandleDAO candleDAO, StockDAO stockDAO, CandleEngine candleEngine) {
        this.candleDAO = candleDAO;
        this.stockDAO = stockDAO;
        this.candleEngine = candleEngine;
    }

    /**
     * Retrieves chronological historical candles merged with the live forming candle.
     * Guaranteed chronological order (ASC: oldest to newest).
     */
    public List<Candle> getCandles(long stockId, String timeframeCode, int limit) {
        if (stockId <= 0) {
            throw new IllegalArgumentException("Stock ID must be positive: " + stockId);
        }
        Timeframe tf = Timeframe.fromCode(timeframeCode);
        int clampedLimit = Math.max(5, Math.min(limit <= 0 ? 100 : limit, 1000));

        try (Connection c = DBConnection.getConnection()) {
            // Check if initial seeding is needed for this stock
            long count = candleDAO.countByStockAndTimeframe(c, stockId, tf.getCode());
            if (count == 0) {
                Stock stock = stockDAO.findById(c, stockId);
                if (stock != null) {
                    seedStockHistory(c, stock, tf, 80);
                }
            }

            List<Candle> historical = candleDAO.findByStockAndTimeframe(c, stockId, tf.getCode(), clampedLimit);
            List<Candle> result = new ArrayList<>(historical);

            // Merge active forming candle from in-memory engine
            Candle active = candleEngine != null ? candleEngine.getActiveCandle(stockId, tf) : null;
            if (active != null) {
                if (!result.isEmpty()) {
                    Candle last = result.get(result.size() - 1);
                    if (last.getOpenTime().equals(active.getOpenTime())) {
                        result.set(result.size() - 1, active);
                    } else if (active.getOpenTime().isAfter(last.getOpenTime())) {
                        result.add(active);
                    }
                } else {
                    result.add(active);
                }
            }

            return Collections.unmodifiableList(result);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query candles for stock " + stockId + " and timeframe " + timeframeCode, e);
        }
    }

    /**
     * Seeds initial historical data asynchronously for all active stocks across all timeframes if database is empty.
     */
    public void seedInitialDataIfEmpty() {
        Thread seeder = new Thread(() -> {
            try (Connection c = DBConnection.getConnection()) {
                long total = candleDAO.countTotal(c);
                if (total > 0) {
                    return;
                }
                List<Stock> stocks = stockDAO.findAllActive(c);
                for (Stock s : stocks) {
                    for (Timeframe tf : Timeframe.values()) {
                        seedStockHistory(c, s, tf, 80);
                    }
                }
            } catch (SQLException e) {
                System.err.println("[CandleService] Seed error: " + e.getMessage());
            }
        }, "CandleService-Seeder");
        seeder.setDaemon(true);
        seeder.start();
    }

    /**
     * Generates a realistic backward walk of historical candles based on current price.
     */
    public void seedStockHistory(Connection c, Stock stock, Timeframe tf, int candleCount) throws SQLException {
        if (stock == null || tf == null || candleCount <= 0) return;

        Random rng = new Random(stock.getId() * 31L + tf.getDurationSeconds());
        Instant now = Instant.now();
        Instant currentBoundary = tf.getCandleStart(now);

        List<Candle> generated = new ArrayList<>(candleCount);
        BigDecimal currentPrice = stock.getPrice();

        // Walk backwards in time to determine past anchor, then walk forward to current
        double[] prices = new double[candleCount];
        prices[candleCount - 1] = currentPrice.doubleValue();
        for (int i = candleCount - 2; i >= 0; i--) {
            double changePct = (rng.nextDouble() - 0.495) * 0.015; // realistic 1.5% max volatility per step
            double p = prices[i + 1] / (1.0 + changePct);
            prices[i] = Math.max(5.0, p);
        }

        for (int i = 0; i < candleCount; i++) {
            long offsetSeconds = (candleCount - 1 - i) * tf.getDurationSeconds();
            Instant candleStart = currentBoundary.minusSeconds(offsetSeconds);

            double openD = prices[i];
            double closeD = (i < candleCount - 1) ? prices[i + 1] : currentPrice.doubleValue();
            double spread = Math.abs(closeD - openD);
            double highD = Math.max(openD, closeD) + (spread * rng.nextDouble() * 0.8) + (openD * 0.002);
            double lowD = Math.min(openD, closeD) - (spread * rng.nextDouble() * 0.8) - (openD * 0.002);
            lowD = Math.max(1.0, lowD);

            BigDecimal open = BigDecimal.valueOf(openD).setScale(2, RoundingMode.HALF_UP);
            BigDecimal high = BigDecimal.valueOf(highD).setScale(2, RoundingMode.HALF_UP);
            BigDecimal low = BigDecimal.valueOf(lowD).setScale(2, RoundingMode.HALF_UP);
            BigDecimal close = BigDecimal.valueOf(closeD).setScale(2, RoundingMode.HALF_UP);

            // Ensure mathematical constraints
            if (high.compareTo(open) < 0) high = open;
            if (high.compareTo(close) < 0) high = close;
            if (low.compareTo(open) > 0) low = open;
            if (low.compareTo(close) > 0) low = close;

            long baseVol = 50L + ((stock.getId() * 19L) % 70L);
            long vol = Math.max(20L, Math.round(baseVol * (0.8 + rng.nextDouble() * 1.5) * 10));

            boolean isClosed = (i < candleCount - 1);
            Candle candle = new Candle(stock.getId(), tf, open, high, low, close, vol, candleStart, isClosed);
            generated.add(candle);
        }

        candleDAO.upsertBatch(c, generated);
    }
}
