package com.project.trading.dao;

import com.project.trading.model.Candle;
import com.project.trading.model.Timeframe;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CandleDAOTest {

    private CandleDAO candleDAO;
    private final long testStockId = 1L; // Existing seeded stock RELIANCE

    @BeforeEach
    public void setUp() throws Exception {
        candleDAO = new CandleDAO();
        cleanTestCandles();
    }

    @AfterEach
    public void tearDown() throws Exception {
        cleanTestCandles();
    }

    private void cleanTestCandles() throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM candles WHERE stock_id = ? AND timeframe = '1m'")) {
            ps.setLong(1, testStockId);
            ps.executeUpdate();
        }
    }

    @Test
    public void testUpsertBatchAndChronologicalAscOrdering() throws Exception {
        Instant baseTime = Instant.parse("2026-11-13T09:00:00Z");
        List<Candle> batch = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Instant t = baseTime.plusSeconds(i * 60L);
            BigDecimal p = BigDecimal.valueOf(100 + i).setScale(2);
            batch.add(new Candle(testStockId, Timeframe.ONE_MIN, p, p.add(BigDecimal.ONE), p.subtract(BigDecimal.ONE), p, 1000L + i * 100, t, true));
        }

        try (Connection c = DBConnection.getConnection()) {
            candleDAO.upsertBatch(c, batch);

            List<Candle> fetched = candleDAO.findByStockAndTimeframe(c, testStockId, "1m", 10);
            assertEquals(5, fetched.size());

            // Validate ASC chronological ordering
            for (int i = 0; i < fetched.size(); i++) {
                assertEquals(baseTime.plusSeconds(i * 60L), fetched.get(i).getOpenTime(),
                        "Candles must be ordered chronologically ascending");
            }

            // Test duplicate key upsert (mutating close and volume)
            Candle updatedLast = new Candle(testStockId, Timeframe.ONE_MIN, 
                    new BigDecimal("104.00"), new BigDecimal("115.00"), new BigDecimal("98.00"), 
                    new BigDecimal("112.50"), 9999L, baseTime.plusSeconds(4 * 60L), true);
            candleDAO.upsert(c, updatedLast);

            List<Candle> refreshed = candleDAO.findByStockAndTimeframe(c, testStockId, "1m", 10);
            assertEquals(5, refreshed.size(), "Duplicate open_time must not create duplicate row");
            Candle lastRow = refreshed.get(4);
            assertEquals(new BigDecimal("112.50"), lastRow.getClosePrice());
            assertEquals(new BigDecimal("115.00"), lastRow.getHighPrice());
            assertEquals(9999L, lastRow.getVolume());
        }
    }

    @Test
    public void testFindByRange() throws Exception {
        Instant t0 = Instant.parse("2026-11-13T08:00:00Z");
        List<Candle> batch = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Instant t = t0.plusSeconds(i * 60L);
            BigDecimal p = new BigDecimal("150.00");
            batch.add(new Candle(testStockId, Timeframe.ONE_MIN, p, p, p, p, 500L, t, true));
        }

        try (Connection c = DBConnection.getConnection()) {
            candleDAO.upsertBatch(c, batch);

            Instant rangeStart = t0.plusSeconds(2 * 60L);
            Instant rangeEnd = t0.plusSeconds(6 * 60L);
            List<Candle> rangeResult = candleDAO.findByRange(c, testStockId, "1m", rangeStart, rangeEnd, 20);

            assertEquals(5, rangeResult.size());
            assertEquals(rangeStart, rangeResult.get(0).getOpenTime());
            assertEquals(rangeEnd, rangeResult.get(rangeResult.size() - 1).getOpenTime());
        }
    }
}
