package com.project.trading.order.dao;

import com.project.trading.order.model.ReservedFund;
import com.project.trading.order.model.ReservedHolding;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReservationDAO {

    // ── Reserved Funds (Cash for BUY orders) ──────────────────────────

    public long reserveFunds(Connection c, long orderId, long userId, BigDecimal amount) throws SQLException {
        String sql = "INSERT INTO reserved_funds (order_id, user_id, reserved_amount, status) VALUES (?, ?, ?, 'RESERVED') " +
                "ON DUPLICATE KEY UPDATE reserved_amount = VALUES(reserved_amount), status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderId);
            ps.setLong(2, userId);
            ps.setBigDecimal(3, amount);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return orderId;
    }

    public BigDecimal getTotalReservedFunds(Connection c, long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(reserved_amount), 0.00) FROM reserved_funds WHERE user_id = ? AND status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public ReservedFund findReservedFundByOrder(Connection c, long orderId) throws SQLException {
        String sql = "SELECT * FROM reserved_funds WHERE order_id = ? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReservedFund rf = new ReservedFund();
                    rf.setId(rs.getLong("id"));
                    rf.setOrderId(rs.getLong("order_id"));
                    rf.setUserId(rs.getLong("user_id"));
                    rf.setReservedAmount(rs.getBigDecimal("reserved_amount"));
                    rf.setStatus(ReservedFund.ReservationStatus.valueOf(rs.getString("status")));
                    return rf;
                }
            }
        }
        return null;
    }

    public void commitReservedFunds(Connection c, long orderId) throws SQLException {
        String sql = "UPDATE reserved_funds SET status = 'COMMITTED', updated_at = CURRENT_TIMESTAMP WHERE order_id = ? AND status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        }
    }

    public void releaseReservedFunds(Connection c, long orderId) throws SQLException {
        String sql = "UPDATE reserved_funds SET status = 'RELEASED', updated_at = CURRENT_TIMESTAMP WHERE order_id = ? AND status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        }
    }

    // ── Reserved Holdings (Shares for SELL orders) ─────────────────────

    public long reserveHoldings(Connection c, long orderId, long userId, long stockId, int quantity) throws SQLException {
        String sql = "INSERT INTO reserved_holdings (order_id, user_id, stock_id, reserved_quantity, status) VALUES (?, ?, ?, ?, 'RESERVED') " +
                "ON DUPLICATE KEY UPDATE reserved_quantity = VALUES(reserved_quantity), status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderId);
            ps.setLong(2, userId);
            ps.setLong(3, stockId);
            ps.setInt(4, quantity);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return orderId;
    }

    public int getTotalReservedHoldings(Connection c, long userId, long stockId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(reserved_quantity), 0) FROM reserved_holdings WHERE user_id = ? AND stock_id = ? AND status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public ReservedHolding findReservedHoldingByOrder(Connection c, long orderId) throws SQLException {
        String sql = "SELECT * FROM reserved_holdings WHERE order_id = ? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReservedHolding rh = new ReservedHolding();
                    rh.setId(rs.getLong("id"));
                    rh.setOrderId(rs.getLong("order_id"));
                    rh.setUserId(rs.getLong("user_id"));
                    rh.setStockId(rs.getLong("stock_id"));
                    rh.setReservedQuantity(rs.getInt("reserved_quantity"));
                    rh.setStatus(ReservedHolding.ReservationStatus.valueOf(rs.getString("status")));
                    return rh;
                }
            }
        }
        return null;
    }

    public void commitReservedHoldings(Connection c, long orderId) throws SQLException {
        String sql = "UPDATE reserved_holdings SET status = 'COMMITTED', updated_at = CURRENT_TIMESTAMP WHERE order_id = ? AND status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        }
    }

    public void releaseReservedHoldings(Connection c, long orderId) throws SQLException {
        String sql = "UPDATE reserved_holdings SET status = 'RELEASED', updated_at = CURRENT_TIMESTAMP WHERE order_id = ? AND status = 'RESERVED'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        }
    }
}
