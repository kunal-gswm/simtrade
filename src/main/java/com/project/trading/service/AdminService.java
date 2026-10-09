package com.project.trading.service;

import com.project.trading.dao.StatsDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.PlatformStats;
import com.project.trading.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminService {

    private final StatsDAO statsDAO;

    public AdminService() {
        this.statsDAO = new StatsDAO();
    }

    public AdminService(StatsDAO statsDAO) {
        this.statsDAO = statsDAO;
    }

    public PlatformStats getPlatformStats() {
        try (Connection c = DBConnection.getConnection()) {
            return statsDAO.load(c);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load platform statistics.", e);
        }
    }
}
