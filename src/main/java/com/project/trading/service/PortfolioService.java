package com.project.trading.service;

import com.project.trading.dao.HoldingDAO;
import com.project.trading.dao.TradeDAO;
import com.project.trading.dao.UserDAO;
import com.project.trading.model.Holding;
import com.project.trading.model.PortfolioRow;
import com.project.trading.model.PortfolioSummary;
import com.project.trading.model.User;
import com.project.trading.util.DBConnection;
import com.project.trading.exception.DataAccessException;
import com.project.trading.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PortfolioService {
    
    private final UserDAO userDAO = new UserDAO();
    private final HoldingDAO holdingDAO = new HoldingDAO();
    private final TradeDAO tradeDAO = new TradeDAO();
    
    public PortfolioSummary getSummary(long userId) {
        try (Connection c = DBConnection.getConnection()) {
            User user = userDAO.findById(c, userId);
            if (user == null) {
                throw new UserNotFoundException("User not found");
            }
            
            BigDecimal cash = user.getCashBalance();
            if (cash == null) cash = BigDecimal.ZERO;

            List<Holding> holdings = holdingDAO.findByUserWithStock(c, userId);
            BigDecimal totalRealizedPnl = tradeDAO.sumRealizedPnl(c, userId);
            
            if (totalRealizedPnl == null) {
                totalRealizedPnl = BigDecimal.ZERO;
            }

            BigDecimal totalInvested = BigDecimal.ZERO;
            BigDecimal totalCurrentValue = BigDecimal.ZERO;
            BigDecimal totalUnrealizedPnl = BigDecimal.ZERO;
            
            List<PortfolioRow> rows = new ArrayList<>();
            
            for (Holding h : holdings) {
                PortfolioRow row = new PortfolioRow();
                row.setStockId(h.getStockId());
                row.setSymbol(h.getStock().getSymbol());
                row.setCompanyName(h.getStock().getCompanyName());
                row.setQuantity(h.getQuantity());
                row.setAvgBuyPrice(h.getAvgBuyPrice());
                
                BigDecimal currentPrice = h.getStock().getPrice();
                row.setCurrentPrice(currentPrice);
                
                BigDecimal invested = PnLCalculator.calculateInvested(h.getQuantity(), h.getAvgBuyPrice());
                BigDecimal currentValue = PnLCalculator.calculateCurrentValue(h.getQuantity(), currentPrice);
                BigDecimal unrealizedPnl = PnLCalculator.calculateUnrealizedPnl(currentValue, invested);
                BigDecimal unrealizedPct = PnLCalculator.calculateUnrealizedPct(unrealizedPnl, invested);
                
                row.setInvested(invested);
                row.setCurrentValue(currentValue);
                row.setUnrealizedPnl(unrealizedPnl);
                row.setUnrealizedPct(unrealizedPct);
                
                totalInvested = totalInvested.add(invested);
                totalCurrentValue = totalCurrentValue.add(currentValue);
                totalUnrealizedPnl = totalUnrealizedPnl.add(unrealizedPnl);
                
                rows.add(row);
            }
            
            BigDecimal netWorth = PnLCalculator.calculateNetWorth(cash, totalCurrentValue);
            BigDecimal overallPnl = PnLCalculator.calculateOverallPnl(netWorth);
            
            PortfolioSummary summary = new PortfolioSummary();
            summary.setRows(rows);
            summary.setCash(cash);
            summary.setTotalInvested(totalInvested);
            summary.setTotalCurrentValue(totalCurrentValue);
            summary.setTotalUnrealizedPnl(totalUnrealizedPnl);
            summary.setRealizedPnl(totalRealizedPnl);
            summary.setNetWorth(netWorth);
            summary.setOverallPnl(overallPnl);
            
            return summary;
            
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load portfolio", e);
        }
    }
}
