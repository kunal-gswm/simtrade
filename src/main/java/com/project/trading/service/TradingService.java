package com.project.trading.service;

import com.project.trading.dao.TradeDAO;
import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.model.Trade;
import com.project.trading.service.order.BuyOrderExecutor;
import com.project.trading.service.order.OrderExecutor;
import com.project.trading.service.order.SellOrderExecutor;
import com.project.trading.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TradingService {
    private final TradeDAO tradeDAO = new TradeDAO();

    public Trade buy(long userId, long stockId, int qty) throws AppException {
        return placeOrder(new BuyOrderExecutor(), userId, stockId, qty);
    }

    public Trade sell(long userId, long stockId, int qty) throws AppException {
        return placeOrder(new SellOrderExecutor(), userId, stockId, qty);
    }

    private Trade placeOrder(OrderExecutor executor, long userId, long stockId, int qty) throws AppException {
        validateQuantity(qty);
        try (Connection c = DBConnection.getConnection()) {
            try {
                c.setAutoCommit(false);
                Trade trade = executor.execute(c, userId, stockId, qty);
                c.commit();
                return trade;
            } catch (AppException e) {
                rollbackQuietly(c);
                throw e;
            } catch (RuntimeException e) {
                rollbackQuietly(c);
                throw new DataAccessException("Trade failed due to unexpected error.", e);
            } catch (SQLException e) {
                rollbackQuietly(c);
                throw new DataAccessException("Trade failed; no changes were saved.", e);
            } finally {
                try {
                    c.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to acquire connection.", e);
        }
    }

    private void validateQuantity(int qty) throws InvalidOrderException {
        if (qty < 1 || qty > 10000) {
            throw new InvalidOrderException("Quantity must be between 1 and 10000");
        }
    }

    private void rollbackQuietly(Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (SQLException e) {
                System.err.println("Rollback failed: " + e.getMessage());
            }
        }
    }

    public List<Trade> getHistory(long userId, int limit) {
        try (Connection c = DBConnection.getConnection()) {
            return tradeDAO.findByUser(c, userId, limit);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch history.", e);
        }
    }

    public List<Trade> getRecentTrades(int limit) {
        try (Connection c = DBConnection.getConnection()) {
            return tradeDAO.findRecent(c, limit);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch recent trades.", e);
        }
    }
}
