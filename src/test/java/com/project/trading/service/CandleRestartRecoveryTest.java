package com.project.trading.service;

import com.project.trading.dao.CandleDAO;
import com.project.trading.model.Candle;
import com.project.trading.model.Candle.CandleSource;
import com.project.trading.model.Timeframe;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CandleRestartRecoveryTest {

    private CandleDAO candleDAO;
    private final long testStockId = 1L;

    @BeforeEach
    public void setUp() throws Exception {
        candleDAO = new CandleDAO();
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
    public void testActiveCandleRecoveryAcrossRestart() throws Exception {
        Instant now = Instant.now();
        Instant candleStart = Timeframe.ONE_MIN.getCandleStart(now);

        // Simulate an unclosed forming candle persisted before server crash/restart
        Candle initialActive = new Candle(testStockId, Timeframe.ONE_MIN,
                new BigDecimal("1500.00"), new BigDecimal("1520.00"),
                new BigDecimal("1490.00"), new BigDecimal("1510.00"),
                1200L, candleStart, false, CandleSource.SIMULATED_STREAM);

        try (Connection c = DBConnection.getConnection()) {
            candleDAO.upsert(c, initialActive);
        }

        // Initialize a new CandleEngine instance (representing a cold server startup)
        CandleEngine restartedEngine = new CandleEngine(candleDAO);
        restartedEngine.recoverActiveCandles();

        // 1. Verify in-memory state is faithfully restored
        Candle recovered = restartedEngine.getActiveCandle(testStockId, Timeframe.ONE_MIN);
        assertNotNull(recovered, "Restarted engine must recover the active unclosed candle");
        assertEquals(candleStart, recovered.getOpenTime());
        assertEquals(new BigDecimal("1500.00"), recovered.getOpenPrice());
        assertEquals(new BigDecimal("1520.00"), recovered.getHighPrice());
        assertEquals(new BigDecimal("1490.00"), recovered.getLowPrice());
        assertEquals(1200L, recovered.getVolume());
        assertFalse(recovered.isClosed());

        // 2. Ingest a new tick within the same interval: price reaches new high of 1535.00
        Instant tickTime = candleStart.plusSeconds(10);
        restartedEngine.onPriceTick(testStockId, new BigDecimal("1535.00"), new BigDecimal("1510.00"), tickTime);

        Candle updated = restartedEngine.getActiveCandle(testStockId, Timeframe.ONE_MIN);
        assertEquals(new BigDecimal("1500.00"), updated.getOpenPrice(), "Open price must remain immutable");
        assertEquals(new BigDecimal("1535.00"), updated.getHighPrice(), "High must update to new peak");
        assertEquals(new BigDecimal("1535.00"), updated.getClosePrice());
        assertTrue(updated.getVolume() > 1200L, "Volume must accumulate without resetting");

        // 3. Flush to DB and check persisted state
        restartedEngine.flushAll();
        try (Connection c = DBConnection.getConnection()) {
            Candle inDb = candleDAO.findLatest(c, testStockId, "1m");
            assertNotNull(inDb);
            assertEquals(new BigDecimal("1500.00"), inDb.getOpenPrice(), "DB open price must not be overwritten");
            assertEquals(new BigDecimal("1535.00"), inDb.getHighPrice(), "DB high price must reflect new peak");
            assertTrue(inDb.getVolume() > 1200L, "DB volume must reflect accumulated volume");
        }
    }

    @Test
    public void testDatabaseUpsertInvariantsProtection() throws Exception {
        Instant t = Instant.parse("2026-11-13T09:15:00Z");

        // Initial insertion: O=100.00, H=110.00, L=95.00, C=105.00, V=500
        Candle c1 = new Candle(testStockId, Timeframe.ONE_MIN,
                new BigDecimal("100.00"), new BigDecimal("110.00"),
                new BigDecimal("95.00"), new BigDecimal("105.00"),
                500L, t, false, CandleSource.SIMULATED_STREAM);

        try (Connection c = DBConnection.getConnection()) {
            candleDAO.upsert(c, c1);

            // Attempt update with altered open price (105.00), lower high (108.00), higher low (98.00), smaller volume (50)
            Candle rogue = new Candle(testStockId, Timeframe.ONE_MIN,
                    new BigDecimal("105.00"), new BigDecimal("108.00"),
                    new BigDecimal("98.00"), new BigDecimal("108.00"),
                    50L, t, false, CandleSource.SIMULATED_STREAM);

            candleDAO.upsert(c, rogue);

            Candle result = candleDAO.findLatest(c, testStockId, "1m");
            assertNotNull(result);

            // Verify Open was NOT changed from 100.00 to 105.00
            assertEquals(new BigDecimal("100.00"), result.getOpenPrice(), "DB open_price must be immutable");

            // Verify High was NOT reduced from 110.00 to 102.00
            assertEquals(new BigDecimal("110.00"), result.getHighPrice(), "High price must only increase");

            // Verify Low was NOT increased from 95.00 to 98.00
            assertEquals(new BigDecimal("95.00"), result.getLowPrice(), "Low price must only decrease");

            // Verify Close tracked latest (108.00)
            assertEquals(new BigDecimal("108.00"), result.getClosePrice(), "Close must track latest tick");

            // Verify Volume did not reset from 500 to 50
            assertEquals(500L, result.getVolume(), "Volume must not regress");
        }
    }
}
