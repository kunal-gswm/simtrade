package com.project.trading.dao;

import com.project.trading.model.Holding;
import com.project.trading.model.Stock;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HoldingDAO {

    public Holding find(Connection c, long uid, long sid) throws SQLException {
        String sql = "SELECT user_id, stock_id, quantity, avg_buy_price FROM holdings WHERE user_id = ? AND stock_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            ps.setLong(2, sid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHolding(rs);
                }
                return null;
            }
        }
    }

    public Holding findForUpdate(Connection c, long uid, long sid) throws SQLException {
        String sql = "SELECT user_id, stock_id, quantity, avg_buy_price FROM holdings WHERE user_id = ? AND stock_id = ? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            ps.setLong(2, sid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHolding(rs);
                }
                return null;
            }
        }
    }

    public void insert(Connection c, Holding holding) throws SQLException {
        String sql = "INSERT INTO holdings (user_id, stock_id, quantity, avg_buy_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, holding.getUserId());
            ps.setLong(2, holding.getStockId());
            ps.setInt(3, holding.getQuantity());
            ps.setBigDecimal(4, holding.getAvgBuyPrice());
            ps.executeUpdate();
        }
    }

    public void update(Connection c, long uid, long sid, int qty, BigDecimal avg) throws SQLException {
        String sql = "UPDATE holdings SET quantity = ?, avg_buy_price = ? WHERE user_id = ? AND stock_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setBigDecimal(2, avg);
            ps.setLong(3, uid);
            ps.setLong(4, sid);
            ps.executeUpdate();
        }
    }

    public void delete(Connection c, long uid, long sid) throws SQLException {
        String sql = "DELETE FROM holdings WHERE user_id = ? AND stock_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            ps.setLong(2, sid);
            ps.executeUpdate();
        }
    }

    public List<Holding> findByUserWithStock(Connection c, long uid) throws SQLException {
        String sql = "SELECT h.user_id, h.stock_id, h.quantity, h.avg_buy_price, " +
                     "s.id AS s_id, s.symbol, s.company_name, s.sector, s.description, " +
                     "s.price, s.prev_price, s.is_active, s.updated_at " +
                     "FROM holdings h " +
                     "JOIN stocks s ON h.stock_id = s.id " +
                     "WHERE h.user_id = ?";
        List<Holding> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Holding h = mapHolding(rs);
                    Stock s = new Stock();
                    s.setId(rs.getLong("s_id"));
                    s.setSymbol(rs.getString("symbol"));
                    s.setCompanyName(rs.getString("company_name"));
                    s.setSector(rs.getString("sector"));
                    s.setDescription(rs.getString("description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setPrevPrice(rs.getBigDecimal("prev_price"));
                    s.setActive(rs.getBoolean("is_active"));
                    s.setUpdatedAt(rs.getTimestamp("updated_at"));
                    h.setStock(s);
                    list.add(h);
                }
            }
        }
        return list;
    }

    private Holding mapHolding(ResultSet rs) throws SQLException {
        Holding h = new Holding();
        h.setUserId(rs.getLong("user_id"));
        h.setStockId(rs.getLong("stock_id"));
        h.setQuantity(rs.getInt("quantity"));
        h.setAvgBuyPrice(rs.getBigDecimal("avg_buy_price"));
        return h;
    }
}
