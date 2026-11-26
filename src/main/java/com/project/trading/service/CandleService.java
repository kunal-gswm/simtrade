package com.project.trading.service;

import com.project.trading.dao.CandleDAO;
import com.project.trading.dao.StockDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.Candle;
import com.project.trading.model.Candle.CandleSource;
import com.project.trading.model.MarketSessionCalendar;
import com.project.trading.model.Stock;
import com.project.trading.model.Timeframe;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Service orchestrating historical OHLCV queries, session-aligned seeding,
 * multi-timeframe mathematical reconciliation, and active candle merging.
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
                    seedStockHistory(c, stock);
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
                    seedStockHistory(c, s);
                }
            } catch (SQLException e) {
                System.err.println("[CandleService] Seed error: " + e.getMessage());
            }
        }, "CandleService-Seeder");
        seeder.setDaemon(true);
        seeder.start();
    }

    /**
     * Compatibility method matching legacy signature.
     */
    public void seedStockHistory(Connection c, Stock stock, Timeframe tf, int candleCount) throws SQLException {
        seedStockHistory(c, stock);
    }

    /**
     * Generates a fully reconciled, Indian market session-aligned historical dataset for a stock.
     *
     * Principles:
     * 1. Indian regular trading hours: 09:15 to 15:30 IST (375 minutes per day).
     * 2. No weekends (Sat/Sun) or exchange holidays.
     * 3. 100% Mathematical Reconciliation: Higher timeframe candles (5m, 15m, 1h, 1d)
     *    are aggregated directly from underlying 1m base candles:
     *    - Open = first 1m candle's Open
     *    - High = max(1m Highs)
     *    - Low = min(1m Lows)
     *    - Close = last 1m candle's Close
     *    - Volume = sum(1m Volumes)
     * 4. Explicit provenance tag: SYNTHETIC_BACKFILL.
     */
    public void seedStockHistory(Connection c, Stock stock) throws SQLException {
        if (stock == null || stock.getId() <= 0) return;

        long stockId = stock.getId();
        BigDecimal currentPrice = stock.getPrice();
        Random rng = new Random(stockId * 7919L + 101L);

        // Fetch recent 5 trading days for high-resolution 1m base sequence
        LocalDate today = LocalDate.now(MarketSessionCalendar.ZONE_IST);
        List<LocalDate> baseDays = MarketSessionCalendar.getRecentTradingDays(today, 5);
        if (baseDays.isEmpty()) return;

        int total1mCandles = baseDays.size() * 375;
        List<Instant> timestamps1m = new ArrayList<>(total1mCandles);

        for (LocalDate day : baseDays) {
            for (int m = 0; m < 375; m++) {
                Instant t = day.atTime(9, 15).plusMinutes(m).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
                timestamps1m.add(t);
            }
        }

        // Generate coherent random-walk price trajectory backwards from currentPrice
        double[] prices = new double[total1mCandles];
        prices[total1mCandles - 1] = currentPrice.doubleValue();

        for (int i = total1mCandles - 2; i >= 0; i--) {
            // Realistic micro-volatility: ~0.04% per minute
            double ret = (rng.nextDouble() - 0.498) * 0.0008;
            double p = prices[i + 1] / (1.0 + ret);
            prices[i] = Math.max(5.0, p);
        }

        // Build 1m candles
        List<Candle> base1mList = new ArrayList<>(total1mCandles);
        for (int i = 0; i < total1mCandles; i++) {
            Instant t = timestamps1m.get(i);
            double openD = prices[i];
            double closeD = (i < total1mCandles - 1) ? prices[i + 1] : currentPrice.doubleValue();
            double spread = Math.abs(closeD - openD);
            double highD = Math.max(openD, closeD) + (spread * rng.nextDouble() * 0.5) + (openD * 0.0002);
            double lowD = Math.min(openD, closeD) - (spread * rng.nextDouble() * 0.5) - (openD * 0.0002);
            lowD = Math.max(1.0, lowD);

            BigDecimal open = BigDecimal.valueOf(openD).setScale(2, RoundingMode.HALF_UP);
            BigDecimal high = BigDecimal.valueOf(highD).setScale(2, RoundingMode.HALF_UP);
            BigDecimal low = BigDecimal.valueOf(lowD).setScale(2, RoundingMode.HALF_UP);
            BigDecimal close = BigDecimal.valueOf(closeD).setScale(2, RoundingMode.HALF_UP);

            if (high.compareTo(open) < 0) high = open;
            if (high.compareTo(close) < 0) high = close;
            if (low.compareTo(open) > 0) low = open;
            if (low.compareTo(close) > 0) low = close;

            long baseVol = 30L + ((stockId * 13L) % 50L);
            long vol = Math.max(10L, Math.round(baseVol * (0.8 + rng.nextDouble() * 1.2)));

            base1mList.add(new Candle(stockId, Timeframe.ONE_MIN, open, high, low, close, vol, t, true, CandleSource.SYNTHETIC_BACKFILL));
        }

        // Aggregate 5m, 15m, 1h, 1d from 1m base list for 100% reconciliation
        List<Candle> aggregated5m = aggregateFrom1m(stockId, Timeframe.FIVE_MIN, base1mList);
        List<Candle> aggregated15m = aggregateFrom1m(stockId, Timeframe.FIFTEEN_MIN, base1mList);
        List<Candle> aggregated1h = aggregateFrom1m(stockId, Timeframe.ONE_HOUR, base1mList);
        List<Candle> aggregated1d = aggregateFrom1m(stockId, Timeframe.ONE_DAY, base1mList);

        // Build additional historical daily candles to supply ~80 daily bars for the 1d chart
        List<LocalDate> extendedDailyDays = MarketSessionCalendar.getRecentTradingDays(baseDays.get(0).minusDays(1), 75);
        List<Candle> extendedDaily = new ArrayList<>(extendedDailyDays.size());
        double lastDayOpen = aggregated1d.get(0).getOpenPrice().doubleValue();

        double[] dailyWalk = new double[extendedDailyDays.size()];
        if (extendedDailyDays.size() > 0) {
            dailyWalk[extendedDailyDays.size() - 1] = lastDayOpen;
            for (int i = extendedDailyDays.size() - 2; i >= 0; i--) {
                double dayRet = (rng.nextDouble() - 0.495) * 0.015;
                dailyWalk[i] = Math.max(5.0, dailyWalk[i + 1] / (1.0 + dayRet));
            }

            for (int i = 0; i < extendedDailyDays.size(); i++) {
                LocalDate d = extendedDailyDays.get(i);
                Instant t = d.atTime(9, 15).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
                double oD = dailyWalk[i];
                double cD = (i < extendedDailyDays.size() - 1) ? dailyWalk[i + 1] : lastDayOpen;
                double sp = Math.abs(cD - oD);
                double hD = Math.max(oD, cD) + (sp * rng.nextDouble() * 0.8) + (oD * 0.005);
                double lD = Math.min(oD, cD) - (sp * rng.nextDouble() * 0.8) - (oD * 0.005);
                lD = Math.max(1.0, lD);

                BigDecimal o = BigDecimal.valueOf(oD).setScale(2, RoundingMode.HALF_UP);
                BigDecimal h = BigDecimal.valueOf(hD).setScale(2, RoundingMode.HALF_UP);
                BigDecimal l = BigDecimal.valueOf(lD).setScale(2, RoundingMode.HALF_UP);
                BigDecimal cPrice = BigDecimal.valueOf(cD).setScale(2, RoundingMode.HALF_UP);

                if (h.compareTo(o) < 0) h = o;
                if (h.compareTo(cPrice) < 0) h = cPrice;
                if (l.compareTo(o) > 0) l = o;
                if (l.compareTo(cPrice) > 0) l = cPrice;

                long v = 3000L + Math.round(rng.nextDouble() * 5000L);
                extendedDaily.add(new Candle(stockId, Timeframe.ONE_DAY, o, h, l, cPrice, v, t, true, CandleSource.SYNTHETIC_BACKFILL));
            }
        }

        // Additional 1h candles to ensure 80+ hourly bars (7 trading days = 49 hourly candles)
        List<LocalDate> extendedHourlyDays = MarketSessionCalendar.getRecentTradingDays(baseDays.get(0).minusDays(1), 7);
        List<Candle> extendedHourly = new ArrayList<>();
        double hourlyAnchor = aggregated1h.get(0).getOpenPrice().doubleValue();

        for (int dIdx = extendedHourlyDays.size() - 1; dIdx >= 0; dIdx--) {
            LocalDate d = extendedHourlyDays.get(dIdx);
            int[] hourMinutes = {15, 75, 135, 195, 255, 315, 375}; // 09:15, 10:15, 11:15, 12:15, 13:15, 14:15, 15:15
            for (int hIdx = 6; hIdx >= 0; hIdx--) {
                Instant t = d.atTime(9, 15).plusMinutes(hIdx * 60).atZone(MarketSessionCalendar.ZONE_IST).toInstant();
                double ret = (rng.nextDouble() - 0.495) * 0.005;
                double oD = hourlyAnchor / (1.0 + ret);
                double cD = hourlyAnchor;
                hourlyAnchor = oD;

                double sp = Math.abs(cD - oD);
                double hD = Math.max(oD, cD) + (sp * rng.nextDouble() * 0.6) + (oD * 0.001);
                double lD = Math.min(oD, cD) - (sp * rng.nextDouble() * 0.6) - (oD * 0.001);
                lD = Math.max(1.0, lD);

                BigDecimal o = BigDecimal.valueOf(oD).setScale(2, RoundingMode.HALF_UP);
                BigDecimal h = BigDecimal.valueOf(hD).setScale(2, RoundingMode.HALF_UP);
                BigDecimal l = BigDecimal.valueOf(lD).setScale(2, RoundingMode.HALF_UP);
                BigDecimal cPrice = BigDecimal.valueOf(cD).setScale(2, RoundingMode.HALF_UP);

                if (h.compareTo(o) < 0) h = o;
                if (h.compareTo(cPrice) < 0) h = cPrice;
                if (l.compareTo(o) > 0) l = o;
                if (l.compareTo(cPrice) > 0) l = cPrice;

                long v = 400L + Math.round(rng.nextDouble() * 600L);
                extendedHourly.add(new Candle(stockId, Timeframe.ONE_HOUR, o, h, l, cPrice, v, t, true, CandleSource.SYNTHETIC_BACKFILL));
            }
        }
        Collections.reverse(extendedHourly);

        // Combine all candles and batch insert
        List<Candle> allCandles = new ArrayList<>();
        allCandles.addAll(base1mList);
        allCandles.addAll(aggregated5m);
        allCandles.addAll(aggregated15m);
        allCandles.addAll(extendedHourly);
        allCandles.addAll(aggregated1h);
        allCandles.addAll(extendedDaily);
        allCandles.addAll(aggregated1d);

        candleDAO.upsertBatch(c, allCandles);
    }

    /**
     * Reconciles a higher timeframe by aggregating directly from 1m base candles.
     */
    private List<Candle> aggregateFrom1m(long stockId, Timeframe targetTf, List<Candle> base1mList) {
        Map<Instant, List<Candle>> buckets = new LinkedHashMap<>();

        for (Candle c1m : base1mList) {
            Instant bucketStart = MarketSessionCalendar.getCandleStart(targetTf, c1m.getOpenTime());
            buckets.computeIfAbsent(bucketStart, k -> new ArrayList<>()).add(c1m);
        }

        List<Candle> aggregated = new ArrayList<>(buckets.size());
        for (Map.Entry<Instant, List<Candle>> entry : buckets.entrySet()) {
            Instant bucketTime = entry.getKey();
            List<Candle> group = entry.getValue();
            if (group.isEmpty()) continue;

            BigDecimal open = group.get(0).getOpenPrice();
            BigDecimal close = group.get(group.size() - 1).getClosePrice();
            BigDecimal high = group.get(0).getHighPrice();
            BigDecimal low = group.get(0).getLowPrice();
            long totalVolume = 0L;

            for (Candle c : group) {
                if (c.getHighPrice().compareTo(high) > 0) high = c.getHighPrice();
                if (c.getLowPrice().compareTo(low) < 0) low = c.getLowPrice();
                totalVolume += c.getVolume();
            }

            aggregated.add(new Candle(stockId, targetTf, open, high, low, close, totalVolume, bucketTime, true, CandleSource.SYNTHETIC_BACKFILL));
        }

        return aggregated;
    }
}
