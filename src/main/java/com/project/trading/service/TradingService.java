package com.project.trading.service;

import com.project.trading.exception.AppException;
import com.project.trading.model.Trade;
import com.project.trading.service.order.BuyOrderExecutor;
import com.project.trading.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class TradingService {

    private final BuyOrderExecutor buyExecutor = new BuyOrderExecutor();

    public Trade buy(long userId, long stockId, int qty) throws AppException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false);
            
            Trade trade = buyExecutor.execute(c, userId, stockId, qty);
            
            c.commit();
            return trade;
        } catch (AppException | SQLException e) {
            if (c != null) {
                try {
                    c.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (e instanceof AppException) {
                throw (AppException) e;
            }
            throw new AppException("Database error during buy: " + e.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
