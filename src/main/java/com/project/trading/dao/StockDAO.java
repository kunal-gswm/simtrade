package com.project.trading.dao;

import com.project.trading.model.Stock;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockDAO {

    public Stock findById(Connection c, long id) throws SQLException {
        String sql = "SELECT id, symbol, company_name, sector, description, price, prev_price, is_active, updated_at " +
                     "FROM stocks WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStock(rs);
                }
            }
        }
        return null;
    }

    public Stock findBySymbol(Connection c, String symbol) throws SQLException {
        String sql = "SELECT id, symbol, company_name, sector, description, price, prev_price, is_active, updated_at " +
                     "FROM stocks WHERE symbol = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStock(rs);
                }
            }
        }
        return null;
    }

    public List<Stock> findAllActive(Connection c) throws SQLException {
        String sql = "SELECT id, symbol, company_name, sector, description, price, prev_price, is_active, updated_at " +
                     "FROM stocks WHERE is_active = 1 ORDER BY symbol ASC";
        List<Stock> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapStock(rs));
            }
        }
        return list;
    }

    public List<Stock> search(Connection c, String q) throws SQLException {
        String queryPattern = "%" + (q == null ? "" : q.trim()) + "%";
        String sql = "SELECT id, symbol, company_name, sector, description, price, prev_price, is_active, updated_at " +
                     "FROM stocks WHERE is_active = 1 AND (symbol LIKE ? OR company_name LIKE ?) " +
                     "ORDER BY symbol ASC";
        List<Stock> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, queryPattern);
            ps.setString(2, queryPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStock(rs));
                }
            }
        }
        return list;
    }

    public List<Stock> findAll(Connection c) throws SQLException {
        String sql = "SELECT id, symbol, company_name, sector, description, price, prev_price, is_active, updated_at " +
                     "FROM stocks ORDER BY symbol ASC";
        List<Stock> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapStock(rs));
            }
        }
        return list;
    }

    public long insert(Connection c, Stock stock) throws SQLException {
        String sql = "INSERT INTO stocks (symbol, company_name, sector, description, price, prev_price, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, stock.getSymbol());
            ps.setString(2, stock.getCompanyName());
            ps.setString(3, stock.getSector());
            ps.setString(4, stock.getDescription());
            ps.setBigDecimal(5, stock.getPrice());
            ps.setBigDecimal(6, stock.getPrevPrice() != null ? stock.getPrevPrice() : stock.getPrice());
            ps.setInt(7, stock.isActive() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to retrieve generated key for stock insert.");
    }

    public void update(Connection c, Stock stock) throws SQLException {
        String sql = "UPDATE stocks SET company_name = ?, sector = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, stock.getCompanyName());
            ps.setString(2, stock.getSector());
            ps.setString(3, stock.getDescription());
            ps.setLong(4, stock.getId());
            ps.executeUpdate();
        }
    }

    public void setActive(Connection c, long id, boolean active) throws SQLException {
        String sql = "UPDATE stocks SET is_active = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void updatePrice(Connection c, long id, BigDecimal price) throws SQLException {
        // In MySQL, SET assignments evaluate left to right.
        // SET prev_price = price, price = ? preserves the current price into prev_price.
        String sql = "UPDATE stocks SET prev_price = price, price = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, price);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void updatePricesBatch(Connection c, Map<Long, BigDecimal> newPrices) throws SQLException {
        if (newPrices == null || newPrices.isEmpty()) {
            return;
        }
        String sql = "UPDATE stocks SET prev_price = price, price = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Map.Entry<Long, BigDecimal> entry : newPrices.entrySet()) {
                ps.setBigDecimal(1, entry.getValue());
                ps.setLong(2, entry.getKey());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Stock mapStock(ResultSet rs) throws SQLException {
        Stock s = new Stock();
        s.setId(rs.getLong("id"));
        s.setSymbol(rs.getString("symbol"));
        s.setCompanyName(rs.getString("company_name"));
        s.setSector(rs.getString("sector"));
        s.setDescription(rs.getString("description"));
        s.setPrice(rs.getBigDecimal("price"));
        s.setPrevPrice(rs.getBigDecimal("prev_price"));
        s.setActive(rs.getInt("is_active") == 1);
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
