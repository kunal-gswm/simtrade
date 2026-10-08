package com.project.trading.dao;

import com.project.trading.model.Trade;
import com.project.trading.model.TradeType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TradeDAO {

    public long insert(Connection c, Trade trade) throws SQLException {
        String sql = "INSERT INTO trades (user_id, stock_id, trade_type, quantity, price, total_amount, realized_pnl) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, trade.getUserId());
            ps.setLong(2, trade.getStockId());
            ps.setString(3, trade.getType().name());
            ps.setInt(4, trade.getQuantity());
            ps.setBigDecimal(5, trade.getPrice());
            ps.setBigDecimal(6, trade.getTotalAmount());
            ps.setBigDecimal(7, trade.getRealizedPnl() == null ? BigDecimal.ZERO : trade.getRealizedPnl());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to retrieve generated key for trade insert.");
    }

    public List<Trade> findByUser(Connection c, long uid, int limit) throws SQLException {
        int safeLimit = limit <= 0 ? 100 : limit;
        String sql = "SELECT t.id, t.user_id, t.stock_id, t.trade_type, t.quantity, t.price, t.total_amount, " +
                     "t.realized_pnl, t.executed_at, u.username, s.symbol " +
                     "FROM trades t " +
                     "JOIN users u ON t.user_id = u.id " +
                     "JOIN stocks s ON t.stock_id = s.id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY t.executed_at DESC, t.id DESC " +
                     "LIMIT ?";
        List<Trade> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            ps.setInt(2, safeLimit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTrade(rs));
                }
            }
        }
        return list;
    }

    public List<Trade> findRecent(Connection c, int limit) throws SQLException {
        int safeLimit = limit <= 0 ? 100 : limit;
        String sql = "SELECT t.id, t.user_id, t.stock_id, t.trade_type, t.quantity, t.price, t.total_amount, " +
                     "t.realized_pnl, t.executed_at, u.username, s.symbol " +
                     "FROM trades t " +
                     "JOIN users u ON t.user_id = u.id " +
                     "JOIN stocks s ON t.stock_id = s.id " +
                     "ORDER BY t.executed_at DESC, t.id DESC " +
                     "LIMIT ?";
        List<Trade> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, safeLimit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTrade(rs));
                }
            }
        }
        return list;
    }

    public BigDecimal sumRealizedPnl(Connection c, long uid) throws SQLException {
        String sql = "SELECT COALESCE(SUM(realized_pnl), 0) FROM trades WHERE user_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private Trade mapTrade(ResultSet rs) throws SQLException {
        Trade t = new Trade();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getLong("user_id"));
        t.setStockId(rs.getLong("stock_id"));
        t.setType(TradeType.valueOf(rs.getString("trade_type")));
        t.setQuantity(rs.getInt("quantity"));
        t.setPrice(rs.getBigDecimal("price"));
        t.setTotalAmount(rs.getBigDecimal("total_amount"));
        t.setRealizedPnl(rs.getBigDecimal("realized_pnl"));
        t.setExecutedAt(rs.getTimestamp("executed_at"));
        t.setUsername(rs.getString("username"));
        t.setStockSymbol(rs.getString("symbol"));
        return t;
    }
}
