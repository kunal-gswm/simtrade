package com.project.trading.order.dao;

import com.project.trading.model.Stock;
import com.project.trading.order.model.BracketRole;
import com.project.trading.order.model.Order;
import com.project.trading.order.model.OrderEvent;
import com.project.trading.order.model.OrderSide;
import com.project.trading.order.model.OrderStatus;
import com.project.trading.order.model.OrderType;
import com.project.trading.order.model.OrderValidity;
import com.project.trading.order.service.OrderStateMachine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public long insert(Connection c, Order order) throws SQLException {
        String sql = "INSERT INTO orders (" +
                "user_id, stock_id, side, order_type, quantity, filled_quantity, remaining_quantity, " +
                "limit_price, trigger_price, target_price, stop_loss_price, average_fill_price, " +
                "status, validity, parent_order_id, bracket_role, idempotency_key, rejection_reason, expiry_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getUserId());
            ps.setLong(2, order.getStockId());
            ps.setString(3, order.getSide().name());
            ps.setString(4, order.getOrderType().name());
            ps.setInt(5, order.getQuantity());
            ps.setInt(6, order.getFilledQuantity());
            ps.setInt(7, order.getRemainingQuantity());
            ps.setBigDecimal(8, order.getLimitPrice());
            ps.setBigDecimal(9, order.getTriggerPrice());
            ps.setBigDecimal(10, order.getTargetPrice());
            ps.setBigDecimal(11, order.getStopLossPrice());
            ps.setBigDecimal(12, order.getAverageFillPrice());
            ps.setString(13, order.getStatus().name());
            ps.setString(14, order.getValidity().name());

            if (order.getParentOrderId() != null) {
                ps.setLong(15, order.getParentOrderId());
            } else {
                ps.setNull(15, java.sql.Types.BIGINT);
            }

            if (order.getBracketRole() != null) {
                ps.setString(16, order.getBracketRole().name());
            } else {
                ps.setNull(16, java.sql.Types.VARCHAR);
            }

            ps.setString(17, order.getIdempotencyKey());
            ps.setString(18, order.getRejectionReason());

            if (order.getExpiryTime() != null) {
                ps.setTimestamp(19, Timestamp.from(order.getExpiryTime()));
            } else {
                ps.setNull(19, java.sql.Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    order.setId(id);
                    recordEvent(c, id, null, order.getStatus().name(), "Order created");
                    return id;
                }
            }
        }
        throw new SQLException("Failed to retrieve generated key for order");
    }

    public Order findById(Connection c, long orderId) throws SQLException {
        String sql = "SELECT o.*, s.symbol, s.company_name, s.price AS stock_price FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id WHERE o.id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        }
        return null;
    }

    public Order findByIdForUpdate(Connection c, long orderId) throws SQLException {
        String sql = "SELECT o.*, s.symbol, s.company_name, s.price AS stock_price FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id WHERE o.id = ? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        }
        return null;
    }

    public List<Order> findByUser(Connection c, long userId, OrderStatus statusFilter, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT o.*, s.symbol, s.company_name, s.price AS stock_price FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id WHERE o.user_id = ? "
        );
        if (statusFilter != null) {
            sql.append("AND o.status = ? ");
        }
        sql.append("ORDER BY o.created_at DESC LIMIT ?");

        List<Order> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
            ps.setLong(1, userId);
            int idx = 2;
            if (statusFilter != null) {
                ps.setString(idx++, statusFilter.name());
            }
            ps.setInt(idx, limit > 0 ? limit : 100);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        }
        return list;
    }

    public List<Order> findActiveOrders(Connection c) throws SQLException {
        String sql = "SELECT o.*, s.symbol, s.company_name, s.price AS stock_price FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id " +
                "WHERE o.status IN ('OPEN', 'TRIGGER_PENDING', 'PARTIALLY_FILLED') " +
                "ORDER BY o.id ASC";
        List<Order> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }

    public List<Order> findActiveByStock(Connection c, long stockId) throws SQLException {
        String sql = "SELECT o.*, s.symbol, s.company_name, s.price AS stock_price FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id " +
                "WHERE o.stock_id = ? AND o.status IN ('OPEN', 'TRIGGER_PENDING', 'PARTIALLY_FILLED') " +
                "ORDER BY o.id ASC";
        List<Order> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        }
        return list;
    }

    public List<Order> findChildOrders(Connection c, long parentOrderId) throws SQLException {
        String sql = "SELECT o.*, s.symbol, s.company_name, s.price AS stock_price FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id " +
                "WHERE o.parent_order_id = ? " +
                "ORDER BY o.id ASC";
        List<Order> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, parentOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        }
        return list;
    }

    public void updateStatus(Connection c, long orderId, OrderStatus newStatus, String reason) throws SQLException {
        Order current = findByIdForUpdate(c, orderId);
        if (current == null) {
            throw new SQLException("Order not found: " + orderId);
        }
        try {
            OrderStateMachine.validateTransition(current.getStatus(), newStatus, orderId);
        } catch (Exception e) {
            throw new SQLException(e.getMessage(), e);
        }

        String sql = "UPDATE orders SET status = ?, rejection_reason = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setString(2, reason != null ? reason : current.getRejectionReason());
            ps.setLong(3, orderId);
            ps.executeUpdate();
            recordEvent(c, orderId, current.getStatus().name(), newStatus.name(), reason);
        }
    }

    public void updateFillProgress(Connection c, long orderId, int filledQty, int remQty, BigDecimal avgPrice, OrderStatus status) throws SQLException {
        String sql = "UPDATE orders SET filled_quantity = ?, remaining_quantity = ?, average_fill_price = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, filledQty);
            ps.setInt(2, remQty);
            ps.setBigDecimal(3, avgPrice);
            ps.setString(4, status.name());
            ps.setLong(5, orderId);
            ps.executeUpdate();
            recordEvent(c, orderId, null, status.name(), "Filled qty: " + filledQty + " @ " + avgPrice);
        }
    }

    public void recordEvent(Connection c, long orderId, String fromStatus, String toStatus, String reason) throws SQLException {
        String sql = "INSERT INTO order_events (order_id, from_status, to_status, reason) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setString(2, fromStatus);
            ps.setString(3, toStatus);
            ps.setString(4, reason);
            ps.executeUpdate();
        }
    }

    public List<OrderEvent> findEventsByOrder(Connection c, long orderId) throws SQLException {
        String sql = "SELECT * FROM order_events WHERE order_id = ? ORDER BY event_time ASC";
        List<OrderEvent> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderEvent(
                            rs.getLong("id"),
                            rs.getLong("order_id"),
                            rs.getString("from_status"),
                            rs.getString("to_status"),
                            rs.getString("reason"),
                            rs.getTimestamp("event_time").toInstant()
                    ));
                }
            }
        }
        return list;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setUserId(rs.getLong("user_id"));
        o.setStockId(rs.getLong("stock_id"));
        o.setSide(OrderSide.valueOf(rs.getString("side")));
        o.setOrderType(OrderType.valueOf(rs.getString("order_type")));
        o.setQuantity(rs.getInt("quantity"));
        o.setFilledQuantity(rs.getInt("filled_quantity"));
        o.setRemainingQuantity(rs.getInt("remaining_quantity"));
        o.setLimitPrice(rs.getBigDecimal("limit_price"));
        o.setTriggerPrice(rs.getBigDecimal("trigger_price"));
        o.setTargetPrice(rs.getBigDecimal("target_price"));
        o.setStopLossPrice(rs.getBigDecimal("stop_loss_price"));
        o.setAverageFillPrice(rs.getBigDecimal("average_fill_price"));
        o.setStatus(OrderStatus.valueOf(rs.getString("status")));
        o.setValidity(OrderValidity.valueOf(rs.getString("validity")));

        long parent = rs.getLong("parent_order_id");
        if (!rs.wasNull()) {
            o.setParentOrderId(parent);
        }

        String role = rs.getString("bracket_role");
        if (role != null) {
            o.setBracketRole(BracketRole.valueOf(role));
        }

        o.setIdempotencyKey(rs.getString("idempotency_key"));
        o.setRejectionReason(rs.getString("rejection_reason"));
        o.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        o.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());

        Timestamp expiry = rs.getTimestamp("expiry_time");
        if (expiry != null) {
            o.setExpiryTime(expiry.toInstant());
        }

        Stock s = new Stock();
        s.setId(o.getStockId());
        s.setSymbol(rs.getString("symbol"));
        s.setCompanyName(rs.getString("company_name"));
        s.setPrice(rs.getBigDecimal("stock_price"));
        o.setStock(s);

        return o;
    }
}
