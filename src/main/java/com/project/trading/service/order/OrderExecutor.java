package com.project.trading.service.order;

import com.project.trading.exception.AppException;
import com.project.trading.model.Trade;

import java.sql.Connection;
import java.sql.SQLException;

public interface OrderExecutor {
    Trade execute(Connection c, long userId, long stockId, int qty) throws AppException, SQLException;
}
