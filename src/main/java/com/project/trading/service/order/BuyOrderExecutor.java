package com.project.trading.service.order;

import com.project.trading.exception.AppException;
import com.project.trading.exception.InsufficientBalanceException;
import com.project.trading.exception.InvalidOrderException;
import com.project.trading.model.Holding;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.TradeType;
import com.project.trading.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;

public class BuyOrderExecutor extends AbstractOrderExecutor {

    @Override
    public Trade execute(Connection c, long userId, long stockId, int qty) throws AppException, SQLException {
        User user = loadTradableUser(c, userId);
        
        Stock stock = loadStock(c, stockId);
        if (!stock.isActive()) {
            throw new InvalidOrderException("Stock not available for trading");
        }
        
        BigDecimal total = stock.getPrice().multiply(new BigDecimal(qty)).setScale(2, RoundingMode.HALF_UP);
        
        if (user.getCashBalance().compareTo(total) < 0) {
            throw new InsufficientBalanceException(user.getCashBalance(), total);
        }
        
        userDAO.adjustCash(c, userId, total.negate());
        
        Holding h = holdingDAO.findForUpdate(c, userId, stockId);
        
        if (h == null) {
            Holding newHolding = new Holding();
            newHolding.setUserId(userId);
            newHolding.setStockId(stockId);
            newHolding.setQuantity(qty);
            newHolding.setAvgBuyPrice(stock.getPrice());
            holdingDAO.insert(c, newHolding);
        } else {
            int newQty = h.getQuantity() + qty;
            BigDecimal oldCost = h.getAvgBuyPrice().multiply(new BigDecimal(h.getQuantity()));
            BigDecimal newCost = stock.getPrice().multiply(new BigDecimal(qty));
            BigDecimal newAvg = oldCost.add(newCost).divide(new BigDecimal(newQty), 4, RoundingMode.HALF_UP);
            holdingDAO.update(c, userId, stockId, newQty, newAvg);
        }
        
        Trade trade = new Trade();
        trade.setUserId(userId);
        trade.setStockId(stockId);
        trade.setType(TradeType.BUY);
        trade.setQuantity(qty);
        trade.setPrice(stock.getPrice());
        trade.setTotalAmount(total);
        trade.setRealizedPnl(BigDecimal.ZERO);
        trade.setStockSymbol(stock.getSymbol());
        
        return recordTrade(c, trade);
    }
}
