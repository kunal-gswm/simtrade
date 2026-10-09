package com.project.trading.dao;

import com.project.trading.model.Role;
import com.project.trading.model.User;
import com.project.trading.model.UserStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findById(Connection c, long id) throws SQLException {
        String sql = "SELECT id, username, email, full_name, password_hash, role, status, cash_balance, created_at " +
                     "FROM users WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findByIdForUpdate(Connection c, long id) throws SQLException {
        String sql = "SELECT id, username, email, full_name, password_hash, role, status, cash_balance, created_at " +
                     "FROM users WHERE id = ? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findByUsername(Connection c, String username) throws SQLException {
        String sql = "SELECT id, username, email, full_name, password_hash, role, status, cash_balance, created_at " +
                     "FROM users WHERE username = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public boolean existsByUsername(Connection c, String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByEmail(Connection c, String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public long insert(Connection c, User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, full_name, password_hash, role, status, cash_balance) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole() != null ? user.getRole().name() : Role.USER.name());
            ps.setString(6, user.getStatus() != null ? user.getStatus().name() : UserStatus.ACTIVE.name());
            ps.setBigDecimal(7, user.getCashBalance() != null ? user.getCashBalance() : new BigDecimal("100000.00"));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    user.setId(generatedId);
                    return generatedId;
                }
            }
        }
        throw new SQLException("Failed to retrieve generated key for user insert.");
    }

    public void updateProfile(Connection c, long id, String fullName, String email) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    public void updatePasswordHash(Connection c, long id, String hash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void updateStatus(Connection c, long id, UserStatus status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void adjustCash(Connection c, long id, BigDecimal delta) throws SQLException {
        String sql = "UPDATE users SET cash_balance = cash_balance + ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, delta);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public List<User> findAll(Connection c) throws SQLException {
        String sql = "SELECT id, username, email, full_name, password_hash, role, status, cash_balance, created_at " +
                     "FROM users ORDER BY id ASC";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        }
        return list;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setCashBalance(rs.getBigDecimal("cash_balance"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
