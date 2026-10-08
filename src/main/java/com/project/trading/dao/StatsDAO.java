package com.project.trading.dao;

import com.project.trading.model.PlatformStats;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatsDAO {

    public PlatformStats load(Connection c) throws SQLException {
        PlatformStats stats = new PlatformStats();

        // 1. Total users
        String sqlUsers = "SELECT COUNT(*) FROM users";
        try (PreparedStatement ps = c.prepareStatement(sqlUsers);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setTotalUsers(rs.getInt(1));
            }
        }

        // 2. Active stocks
        String sqlStocks = "SELECT COUNT(*) FROM stocks WHERE is_active = 1";
        try (PreparedStatement ps = c.prepareStatement(sqlStocks);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setActiveStocks(rs.getInt(1));
            }
        }

        // 3. Total trades and total traded value
        String sqlTrades = "SELECT COUNT(*), COALESCE(SUM(total_amount), 0) FROM trades";
        try (PreparedStatement ps = c.prepareStatement(sqlTrades);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setTotalTrades(rs.getInt(1));
                stats.setTotalTradedValue(rs.getBigDecimal(2));
            } else {
                stats.setTotalTrades(0);
                stats.setTotalTradedValue(BigDecimal.ZERO);
            }
        }

        // 4. Top 5 stocks by trade count
        String sqlTopStocks = "SELECT s.symbol, COUNT(t.id) AS trade_count " +
                              "FROM trades t " +
                              "JOIN stocks s ON t.stock_id = s.id " +
                              "GROUP BY s.id, s.symbol " +
                              "ORDER BY trade_count DESC, s.symbol ASC " +
                              "LIMIT 5";
        Map<String, Integer> topStocks = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sqlTopStocks);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                topStocks.put(rs.getString("symbol"), rs.getInt("trade_count"));
            }
        }
        stats.setTopStocksByTrades(topStocks);

        return stats;
    }
}
