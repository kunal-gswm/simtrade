package com.project.trading.service;

import com.project.trading.dao.StatsDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.PlatformStats;
import com.project.trading.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminService {

    private final StatsDAO statsDAO;
    private final com.project.trading.dao.TradeDAO tradeDAO;

    public AdminService() {
        this.statsDAO = new StatsDAO();
        this.tradeDAO = new com.project.trading.dao.TradeDAO();
    }

    public AdminService(StatsDAO statsDAO, com.project.trading.dao.TradeDAO tradeDAO) {
        this.statsDAO = statsDAO;
        this.tradeDAO = tradeDAO;
    }

    public PlatformStats getPlatformStats() {
        try (Connection c = DBConnection.getConnection()) {
            return statsDAO.load(c);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load platform statistics.", e);
        }
    }

    public java.util.List<com.project.trading.model.Trade> getRecentTrades(int limit) {
        try (Connection c = DBConnection.getConnection()) {
            return tradeDAO.findRecent(c, limit);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load platform trade history.", e);
        }
    }
}
