package com.project.trading.order.dao;

import com.project.trading.order.model.Fill;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FillDAO {

    public long insert(Connection c, Fill fill) throws SQLException {
        String sql = "INSERT INTO fills (" +
                "order_id, user_id, stock_id, fill_quantity, fill_price, total_amount, " +
                "slippage, simulated_brokerage, simulated_taxes, fill_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, fill.getOrderId());
            ps.setLong(2, fill.getUserId());
            ps.setLong(3, fill.getStockId());
            ps.setInt(4, fill.getFillQuantity());
            ps.setBigDecimal(5, fill.getFillPrice());
            ps.setBigDecimal(6, fill.getTotalAmount());
            ps.setBigDecimal(7, fill.getSlippage());
            ps.setBigDecimal(8, fill.getSimulatedBrokerage());
            ps.setBigDecimal(9, fill.getSimulatedTaxes());
            ps.setTimestamp(10, fill.getFillTime() != null ? Timestamp.from(fill.getFillTime()) : new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    fill.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Failed to retrieve generated fill ID");
    }

    public List<Fill> findByOrder(Connection c, long orderId) throws SQLException {
        String sql = "SELECT * FROM fills WHERE order_id = ? ORDER BY fill_time ASC";
        List<Fill> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFill(rs));
                }
            }
        }
        return list;
    }

    public List<Fill> findByUser(Connection c, long userId, int limit) throws SQLException {
        String sql = "SELECT * FROM fills WHERE user_id = ? ORDER BY fill_time DESC LIMIT ?";
        List<Fill> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit > 0 ? limit : 100);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFill(rs));
                }
            }
        }
        return list;
    }

    public BigDecimal sumFeesByUser(Connection c, long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(simulated_brokerage + simulated_taxes), 0.00) FROM fills WHERE user_id = ?";
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

    private Fill mapFill(ResultSet rs) throws SQLException {
        Fill f = new Fill();
        f.setId(rs.getLong("id"));
        f.setOrderId(rs.getLong("order_id"));
        f.setUserId(rs.getLong("user_id"));
        f.setStockId(rs.getLong("stock_id"));
        f.setFillQuantity(rs.getInt("fill_quantity"));
        f.setFillPrice(rs.getBigDecimal("fill_price"));
        f.setTotalAmount(rs.getBigDecimal("total_amount"));
        f.setSlippage(rs.getBigDecimal("slippage"));
        f.setSimulatedBrokerage(rs.getBigDecimal("simulated_brokerage"));
        f.setSimulatedTaxes(rs.getBigDecimal("simulated_taxes"));
        f.setFillTime(rs.getTimestamp("fill_time").toInstant());
        return f;
    }
}
