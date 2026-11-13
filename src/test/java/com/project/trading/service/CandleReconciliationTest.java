package com.project.trading.service;

import com.project.trading.dao.CandleDAO;
import com.project.trading.dao.StockDAO;
import com.project.trading.model.Candle;
import com.project.trading.model.Candle.CandleSource;
import com.project.trading.model.Stock;
import com.project.trading.model.Timeframe;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CandleReconciliationTest {

    private CandleService candleService;
    private CandleDAO candleDAO;
    private StockDAO stockDAO;
    private final long testStockId = 1L; // Seeded stock (e.g. RELIANCE)

    @BeforeEach
    public void setUp() throws Exception {
        candleDAO = new CandleDAO();
        stockDAO = new StockDAO();
        candleService = new CandleService(candleDAO, stockDAO, CandleEngine.getInstance());
        cleanCandles();
    }

    @AfterEach
    public void tearDown() throws Exception {
        cleanCandles();
    }

    private void cleanCandles() throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM candles WHERE stock_id = ?")) {
            ps.setLong(1, testStockId);
            ps.executeUpdate();
        }
    }

    @Test
    public void testFullMultiTimeframeReconciliation() throws Exception {
        try (Connection c = DBConnection.getConnection()) {
            Stock stock = stockDAO.findById(c, testStockId);
            assertNotNull(stock, "Test stock 1 must exist in seed database");

            // Seed complete session history
            candleService.seedStockHistory(c, stock);

            // Fetch 1m, 5m, 15m, 1h, 1d candles
            List<Candle> m1Candles = candleDAO.findByStockAndTimeframe(c, testStockId, "1m", 2000);
            List<Candle> m5Candles = candleDAO.findByStockAndTimeframe(c, testStockId, "5m", 1000);
            List<Candle> m15Candles = candleDAO.findByStockAndTimeframe(c, testStockId, "15m", 500);
            List<Candle> h1Candles = candleDAO.findByStockAndTimeframe(c, testStockId, "1h", 200);

            assertFalse(m1Candles.isEmpty(), "1m candles must be seeded");
            assertFalse(m5Candles.isEmpty(), "5m candles must be seeded");
            assertFalse(m15Candles.isEmpty(), "15m candles must be seeded");
            assertFalse(h1Candles.isEmpty(), "1h candles must be seeded");

            // 1. Reconcile 15m candles against underlying 1m candles
            int reconciled15mCount = 0;
            for (Candle c15 : m15Candles) {
                Instant start = c15.getOpenTime();
                Instant end = start.plusSeconds(900L); // 15 minutes window

                List<Candle> sub1m = m1Candles.stream()
                        .filter(m -> !m.getOpenTime().isBefore(start) && m.getOpenTime().isBefore(end))
                        .toList();

                if (sub1m.size() == 15) {
                    reconciled15mCount++;
                    // Invariant 1: Open must match first 1m open
                    assertEquals(sub1m.get(0).getOpenPrice(), c15.getOpenPrice(),
                            "15m Open must equal first 1m Open at " + start);

                    // Invariant 2: Close must match last 1m close
                    assertEquals(sub1m.get(14).getClosePrice(), c15.getClosePrice(),
                            "15m Close must equal last 1m Close at " + start);

                    // Invariant 3: High must equal max of 1m highs
                    BigDecimal expectedHigh = sub1m.stream().map(Candle::getHighPrice).max(BigDecimal::compareTo).orElseThrow();
                    assertEquals(expectedHigh, c15.getHighPrice(),
                            "15m High must equal max of 1m Highs at " + start);

                    // Invariant 4: Low must equal min of 1m lows
                    BigDecimal expectedLow = sub1m.stream().map(Candle::getLowPrice).min(BigDecimal::compareTo).orElseThrow();
                    assertEquals(expectedLow, c15.getLowPrice(),
                            "15m Low must equal min of 1m Lows at " + start);

                    // Invariant 5: Volume must equal sum of 1m volumes
                    long expectedVol = sub1m.stream().mapToLong(Candle::getVolume).sum();
                    assertEquals(expectedVol, c15.getVolume(),
                            "15m Volume must equal sum of 1m Volumes at " + start);
                }
            }
            assertTrue(reconciled15mCount >= 25, "At least 25 15m candles must be fully reconciled");

            // 2. Reconcile 5m candles against underlying 1m candles
            int reconciled5mCount = 0;
            for (Candle c5 : m5Candles) {
                Instant start = c5.getOpenTime();
                Instant end = start.plusSeconds(300L);

                List<Candle> sub1m = m1Candles.stream()
                        .filter(m -> !m.getOpenTime().isBefore(start) && m.getOpenTime().isBefore(end))
                        .toList();

                if (sub1m.size() == 5) {
                    reconciled5mCount++;
                    assertEquals(sub1m.get(0).getOpenPrice(), c5.getOpenPrice());
                    assertEquals(sub1m.get(4).getClosePrice(), c5.getClosePrice());
                    assertEquals(sub1m.stream().map(Candle::getHighPrice).max(BigDecimal::compareTo).orElseThrow(), c5.getHighPrice());
                    assertEquals(sub1m.stream().map(Candle::getLowPrice).min(BigDecimal::compareTo).orElseThrow(), c5.getLowPrice());
                    assertEquals(sub1m.stream().mapToLong(Candle::getVolume).sum(), c5.getVolume());
                }
            }
            assertTrue(reconciled5mCount >= 75, "At least 75 5m candles must be fully reconciled");

            // 3. Verify database financial precision and mathematical invariants for every candle
            String auditSql = "SELECT open_price, high_price, low_price, close_price, volume, source FROM candles WHERE stock_id = ?";
            try (PreparedStatement ps = c.prepareStatement(auditSql)) {
                ps.setLong(1, testStockId);
                try (ResultSet rs = ps.executeQuery()) {
                    int checkedCount = 0;
                    while (rs.next()) {
                        checkedCount++;
                        BigDecimal o = rs.getBigDecimal("open_price");
                        BigDecimal h = rs.getBigDecimal("high_price");
                        BigDecimal l = rs.getBigDecimal("low_price");
                        BigDecimal close = rs.getBigDecimal("close_price");
                        long vol = rs.getLong("volume");
                        String source = rs.getString("source");

                        assertEquals(2, o.scale(), "Open price must have scale 2");
                        assertEquals(2, h.scale(), "High price must have scale 2");
                        assertEquals(2, l.scale(), "Low price must have scale 2");
                        assertEquals(2, close.scale(), "Close price must have scale 2");

                        assertTrue(o.compareTo(BigDecimal.ZERO) > 0, "Open must be positive");
                        assertTrue(h.compareTo(l) >= 0, "High >= Low");
                        assertTrue(h.compareTo(o) >= 0, "High >= Open");
                        assertTrue(h.compareTo(close) >= 0, "High >= Close");
                        assertTrue(l.compareTo(o) <= 0, "Low <= Open");
                        assertTrue(l.compareTo(close) <= 0, "Low <= Close");
                        assertTrue(vol >= 0, "Volume >= 0");
                        assertEquals(CandleSource.SYNTHETIC_BACKFILL.name(), source, "Seeded candle must have SYNTHETIC_BACKFILL source");
                    }
                    assertTrue(checkedCount > 1000, "Must have audited over 1000 seeded candles");
                }
            }
        }
    }
}
