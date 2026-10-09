package com.project.trading.service.order;

import com.project.trading.exception.AppException;
import com.project.trading.exception.InsufficientHoldingsException;
import com.project.trading.model.Holding;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.model.TradeType;
import com.project.trading.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;

public class SellOrderExecutor extends AbstractOrderExecutor {

    @Override
    public Trade execute(Connection c, long userId, long stockId, int qty) throws AppException, SQLException {
        User user = loadTradableUser(c, userId);
        Stock stock = loadStock(c, stockId);

        Holding h = holdingDAO.findForUpdate(c, userId, stockId);

        if (h == null || h.getQuantity() < qty) {
            int held = h == null ? 0 : h.getQuantity();
            throw new InsufficientHoldingsException(held, qty);
        }

        BigDecimal total = stock.getPrice().multiply(new BigDecimal(qty)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal realized = stock.getPrice().subtract(h.getAvgBuyPrice())
                .multiply(new BigDecimal(qty))
                .setScale(2, RoundingMode.HALF_UP);

        int newQty = h.getQuantity() - qty;
        if (newQty == 0) {
            holdingDAO.delete(c, userId, stockId);
        } else {
            holdingDAO.update(c, userId, stockId, newQty, h.getAvgBuyPrice());
        }

        userDAO.adjustCash(c, userId, total);

        Trade trade = new Trade();
        trade.setUserId(userId);
        trade.setStockId(stockId);
        trade.setType(TradeType.SELL);
        trade.setQuantity(qty);
        trade.setPrice(stock.getPrice());
        trade.setTotalAmount(total);
        trade.setRealizedPnl(realized);

        return recordTrade(c, trade);
    }
}
