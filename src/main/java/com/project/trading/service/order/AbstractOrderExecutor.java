package com.project.trading.service.order;

import com.project.trading.dao.HoldingDAO;
import com.project.trading.dao.StockDAO;
import com.project.trading.dao.TradeDAO;
import com.project.trading.dao.UserDAO;
import com.project.trading.exception.AppException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.exception.StockNotFoundException;
import com.project.trading.exception.UserNotFoundException;
import com.project.trading.model.Role;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.User;
import com.project.trading.model.UserStatus;
import com.project.trading.util.FaultInjector;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractOrderExecutor implements OrderExecutor {
    protected final UserDAO userDAO = new UserDAO();
    protected final StockDAO stockDAO = new StockDAO();
    protected final HoldingDAO holdingDAO = new HoldingDAO();
    protected final TradeDAO tradeDAO = new TradeDAO();

    protected User loadTradableUser(Connection c, long userId) throws SQLException, AppException {
        User user = userDAO.findByIdForUpdate(c, userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (user.getStatus() == UserStatus.BLOCKED || user.getRole() == Role.ADMIN) {
            throw new InvalidOrderException("Account cannot trade");
        }
        return user;
    }

    protected Stock loadStock(Connection c, long stockId) throws SQLException, AppException {
        Stock stock = stockDAO.findById(c, stockId);
        if (stock == null) {
            throw new StockNotFoundException("Stock not found");
        }
        return stock;
    }

    protected Trade recordTrade(Connection c, Trade trade) throws SQLException {
        FaultInjector.checkpoint("after-holding-update");
        long id = tradeDAO.insert(c, trade);
        trade.setId(id);
        return trade;
    }
}
