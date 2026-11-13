package com.project.trading.dao;

import com.project.trading.model.Candle;
import com.project.trading.model.Timeframe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Data access object for persisting and querying OHLCV financial candles.
 * Handles high-throughput batch upserts with duplicate prevention.
 */
public class CandleDAO {

    private static final String UPSERT_SQL =
            "INSERT INTO candles (stock_id, timeframe, open_price, high_price, low_price, close_price, volume, open_time, is_closed) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "open_price = VALUES(open_price), " +
            "high_price = VALUES(high_price), " +
            "low_price = VALUES(low_price), " +
            "close_price = VALUES(close_price), " +
            "volume = VALUES(volume), " +
            "is_closed = VALUES(is_closed), " +
            "updated_at = CURRENT_TIMESTAMP";

    public void upsertBatch(Connection c, Collection<Candle> candles) throws SQLException {
        if (candles == null || candles.isEmpty()) {
            return;
        }

        try (PreparedStatement ps = c.prepareStatement(UPSERT_SQL)) {
            for (Candle candle : candles) {
                if (candle == null || candle.getStockId() == null || candle.getTimeframe() == null || candle.getOpenTime() == null) {
                    continue;
                }
                ps.setLong(1, candle.getStockId());
                ps.setString(2, candle.getTimeframe().getCode());
                ps.setBigDecimal(3, candle.getOpenPrice());
                ps.setBigDecimal(4, candle.getHighPrice());
                ps.setBigDecimal(5, candle.getLowPrice());
                ps.setBigDecimal(6, candle.getClosePrice());
                ps.setLong(7, candle.getVolume());
                ps.setTimestamp(8, Timestamp.from(candle.getOpenTime()));
                ps.setBoolean(9, candle.isClosed());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void upsert(Connection c, Candle candle) throws SQLException {
        if (candle == null) return;
        upsertBatch(c, Collections.singletonList(candle));
    }

    /**
     * Retrieves historical candles ordered chronologically (ASC: oldest to newest) for charting.
     * Takes the most recent `limit` candles from the database.
     */
    public List<Candle> findByStockAndTimeframe(Connection c, long stockId, String timeframeCode, int limit) throws SQLException {
        int clampedLimit = Math.max(1, Math.min(limit, 2000));
        String sql = "SELECT id, stock_id, timeframe, open_price, high_price, low_price, close_price, " +
                     "volume, open_time, is_closed, created_at, updated_at " +
                     "FROM candles " +
                     "WHERE stock_id = ? AND timeframe = ? " +
                     "ORDER BY open_time DESC " +
                     "LIMIT ?";

        List<Candle> result = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, stockId);
            ps.setString(2, timeframeCode);
            ps.setInt(3, clampedLimit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapCandle(rs));
                }
            }
        }

        // Reverse so candles are in ascending chronological order (required by charts)
        Collections.reverse(result);
        return result;
    }

    /**
     * Retrieves candles within a specific historical timestamp range in chronological order (ASC).
     */
    public List<Candle> findByRange(Connection c, long stockId, String timeframeCode, Instant start, Instant end, int limit) throws SQLException {
        int clampedLimit = Math.max(1, Math.min(limit, 2000));
        String sql = "SELECT id, stock_id, timeframe, open_price, high_price, low_price, close_price, " +
                     "volume, open_time, is_closed, created_at, updated_at " +
                     "FROM candles " +
                     "WHERE stock_id = ? AND timeframe = ? AND open_time >= ? AND open_time <= ? " +
                     "ORDER BY open_time ASC " +
                     "LIMIT ?";

        List<Candle> result = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, stockId);
            ps.setString(2, timeframeCode);
            ps.setTimestamp(3, Timestamp.from(start));
            ps.setTimestamp(4, Timestamp.from(end));
            ps.setInt(5, clampedLimit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapCandle(rs));
                }
            }
        }
        return result;
    }

    public Candle findLatest(Connection c, long stockId, String timeframeCode) throws SQLException {
        String sql = "SELECT id, stock_id, timeframe, open_price, high_price, low_price, close_price, " +
                     "volume, open_time, is_closed, created_at, updated_at " +
                     "FROM candles " +
                     "WHERE stock_id = ? AND timeframe = ? " +
                     "ORDER BY open_time DESC " +
                     "LIMIT 1";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, stockId);
            ps.setString(2, timeframeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCandle(rs);
                }
            }
        }
        return null;
    }

    public long countByStockAndTimeframe(Connection c, long stockId, String timeframeCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candles WHERE stock_id = ? AND timeframe = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, stockId);
            ps.setString(2, timeframeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0L;
    }

    public long countTotal(Connection c) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candles";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0L;
    }

    private Candle mapCandle(ResultSet rs) throws SQLException {
        Candle candle = new Candle(
                rs.getLong("id"),
                rs.getLong("stock_id"),
                Timeframe.fromCodeSafe(rs.getString("timeframe")),
                rs.getBigDecimal("open_price"),
                rs.getBigDecimal("high_price"),
                rs.getBigDecimal("low_price"),
                rs.getBigDecimal("close_price"),
                rs.getLong("volume"),
                rs.getTimestamp("open_time").toInstant(),
                rs.getBoolean("is_closed"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toInstant() : null
        );
        return candle;
    }
}
