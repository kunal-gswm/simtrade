package com.project.trading.service;

import com.project.trading.dao.HoldingDAO;
import com.project.trading.dao.TradeDAO;
import com.project.trading.dao.UserDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.Holding;
import com.project.trading.model.Portfolio;
import com.project.trading.model.User;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates portfolio data and computes end-to-end financial summaries
 * using high-precision BigDecimal arithmetic via PnLCalculator.
 */
public class PortfolioService {

    private final HoldingDAO holdingDAO = new HoldingDAO();
    private final UserDAO userDAO = new UserDAO();
    private final TradeDAO tradeDAO = new TradeDAO();

    /**
     * Loads the real database-backed portfolio for the given user ID.
     */
    public Portfolio getPortfolioForUser(long userId) {
        try (Connection c = DBConnection.getConnection()) {
            User user = userDAO.findById(c, userId);
            if (user == null) {
                return new Portfolio((int) userId, BigDecimal.ZERO, Collections.emptyList());
            }
            List<Holding> holdings = holdingDAO.findByUserWithStock(c, userId);

            for (Holding h : holdings) {
                if (h.getStock() != null) {
                    h.setCurrentPriceBigDecimal(h.getStock().getPrice());
                    h.setStockName(h.getStock().getCompanyName());
                    h.setStockSymbol(h.getStock().getSymbol());
                }
            }

            return new Portfolio((int) userId, user.getCashBalance(), holdings);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load portfolio from database.", e);
        }
    }

    /**
     * Loads portfolio and trades atomically in a single connection,
     * building the fully calculated PortfolioSummary with real realized P&L.
     */
    public PortfolioSummary getSummaryForUser(long userId) {
        try (Connection c = DBConnection.getConnection()) {
            User user = userDAO.findById(c, userId);
            if (user == null) {
                return buildSummary(new Portfolio((int) userId, BigDecimal.ZERO, Collections.emptyList()), BigDecimal.ZERO);
            }
            List<Holding> holdings = holdingDAO.findByUserWithStock(c, userId);
            for (Holding h : holdings) {
                if (h.getStock() != null) {
                    h.setCurrentPriceBigDecimal(h.getStock().getPrice());
                    h.setStockName(h.getStock().getCompanyName());
                    h.setStockSymbol(h.getStock().getSymbol());
                }
            }
            BigDecimal realizedPnl = tradeDAO.sumRealizedPnl(c, userId);
            Portfolio portfolio = new Portfolio((int) userId, user.getCashBalance(), holdings);
            return buildSummary(portfolio, realizedPnl);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load portfolio summary from database.", e);
        }
    }

    /**
     * Builds a fully computed PortfolioSummary from a Portfolio object.
     * Automatically queries TradeDAO for realized P&L if userId > 0.
     */
    public PortfolioSummary buildSummary(Portfolio portfolio) {
        BigDecimal realizedPnl = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (portfolio.getUserId() > 0) {
            try (Connection c = DBConnection.getConnection()) {
                realizedPnl = tradeDAO.sumRealizedPnl(c, portfolio.getUserId());
            } catch (Exception ignored) {
                // In mock test mode or non-DB context, default to ZERO
            }
        }
        return buildSummary(portfolio, realizedPnl);
    }

    /**
     * Builds a fully computed PortfolioSummary with explicitly supplied realized P&L.
     */
    public PortfolioSummary buildSummary(Portfolio portfolio, BigDecimal realizedPnl) {
        BigDecimal cash = portfolio.getCash();
        if (cash == null) {
            cash = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal safeRealizedPnl = realizedPnl != null ? realizedPnl.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalInvested = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalCurrentValue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        List<HoldingRow> rows = new ArrayList<>();

        for (Holding h : portfolio.getHoldings()) {
            int qty = h.getQuantity();
            BigDecimal avgBuyPrice = h.getAvgBuyPrice() != null ? h.getAvgBuyPrice() : BigDecimal.ZERO;
            BigDecimal currentPrice = h.getCurrentPriceBigDecimal() != null ? h.getCurrentPriceBigDecimal() : avgBuyPrice;

            BigDecimal invested = PnLCalculator.calculateInvested(qty, avgBuyPrice);
            BigDecimal currentValue = PnLCalculator.calculateCurrentValue(qty, currentPrice);
            BigDecimal unrealizedPnl = PnLCalculator.calculateUnrealizedPnl(currentValue, invested);
            BigDecimal unrealizedPct = PnLCalculator.calculateUnrealizedPct(unrealizedPnl, invested);

            rows.add(new HoldingRow(
                    h.getStockId(),
                    h.getStockSymbol(),
                    h.getStockName(),
                    h.getQuantity(),
                    avgBuyPrice,
                    currentPrice,
                    invested,
                    currentValue,
                    unrealizedPnl,
                    unrealizedPct
            ));

            totalInvested = totalInvested.add(invested).setScale(2, RoundingMode.HALF_UP);
            totalCurrentValue = totalCurrentValue.add(currentValue).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalUnrealizedPnl = PnLCalculator.calculateUnrealizedPnl(totalCurrentValue, totalInvested);
        BigDecimal netWorth = PnLCalculator.calculateNetWorth(cash, totalCurrentValue);
        BigDecimal overallPnl = safeRealizedPnl.add(totalUnrealizedPnl).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalReturnPct = PnLCalculator.calculateUnrealizedPct(totalUnrealizedPnl, totalInvested);

        return new PortfolioSummary(
                rows,
                cash,
                totalInvested,
                totalCurrentValue,
                totalUnrealizedPnl,
                safeRealizedPnl,
                netWorth,
                overallPnl,
                totalReturnPct
        );
    }

    // ── Inner value-object classes ────────────────────────────────────────────

    /** One row in the holdings table */
    public static class HoldingRow {
        public final long stockId;
        public final String symbol;
        public final String companyName;
        public final int quantity;
        public final BigDecimal avgBuyPrice;
        public final BigDecimal currentPrice;
        public final BigDecimal invested;
        public final BigDecimal currentValue;
        public final BigDecimal unrealizedPnl;
        public final BigDecimal unrealizedPct;

        public HoldingRow(long stockId, String symbol, String companyName, int quantity,
                          BigDecimal avgBuyPrice, BigDecimal currentPrice,
                          BigDecimal invested, BigDecimal currentValue,
                          BigDecimal unrealizedPnl, BigDecimal unrealizedPct) {
            this.stockId       = stockId;
            this.symbol        = symbol;
            this.companyName   = companyName;
            this.quantity      = quantity;
            this.avgBuyPrice   = avgBuyPrice != null ? avgBuyPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.currentPrice  = currentPrice != null ? currentPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.invested      = invested != null ? invested.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.currentValue  = currentValue != null ? currentValue.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.unrealizedPnl = unrealizedPnl != null ? unrealizedPnl.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.unrealizedPct = unrealizedPct != null ? unrealizedPct.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        public HoldingRow(long stockId, String symbol, String companyName, int quantity,
                          double avgBuyPrice, double currentPrice,
                          double invested, double currentValue,
                          double unrealizedPnl, double unrealizedPct) {
            this(stockId, symbol, companyName, quantity,
                 BigDecimal.valueOf(avgBuyPrice), BigDecimal.valueOf(currentPrice),
                 BigDecimal.valueOf(invested), BigDecimal.valueOf(currentValue),
                 BigDecimal.valueOf(unrealizedPnl), BigDecimal.valueOf(unrealizedPct));
        }

        public boolean isPositive() {
            return unrealizedPnl != null && unrealizedPnl.compareTo(BigDecimal.ZERO) >= 0;
        }

        public double getAvgBuyPrice() { return avgBuyPrice.doubleValue(); }
        public double getCurrentPrice() { return currentPrice.doubleValue(); }
        public double getInvested() { return invested.doubleValue(); }
        public double getCurrentValue() { return currentValue.doubleValue(); }
        public double getUnrealizedPnl() { return unrealizedPnl.doubleValue(); }
        public double getUnrealizedPct() { return unrealizedPct.doubleValue(); }
    }

    /** Aggregated summary for UI views */
    public static class PortfolioSummary {
        public final List<HoldingRow> rows;
        public final BigDecimal cash;
        public final BigDecimal totalInvested;
        public final BigDecimal totalCurrentValue;
        public final BigDecimal totalUnrealizedPnl;
        public final BigDecimal realizedPnl;
        public final BigDecimal netWorth;
        public final BigDecimal overallPnl;
        public final BigDecimal totalReturnPct;

        public PortfolioSummary(List<HoldingRow> rows,
                                BigDecimal cash, BigDecimal totalInvested,
                                BigDecimal totalCurrentValue, BigDecimal totalUnrealizedPnl,
                                BigDecimal realizedPnl, BigDecimal netWorth, BigDecimal overallPnl,
                                BigDecimal totalReturnPct) {
            this.rows               = rows != null ? rows : Collections.emptyList();
            this.cash               = cash != null ? cash.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.totalInvested      = totalInvested != null ? totalInvested.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.totalCurrentValue  = totalCurrentValue != null ? totalCurrentValue.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.totalUnrealizedPnl = totalUnrealizedPnl != null ? totalUnrealizedPnl.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.realizedPnl        = realizedPnl != null ? realizedPnl.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.netWorth           = netWorth != null ? netWorth.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.overallPnl         = overallPnl != null ? overallPnl.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            this.totalReturnPct     = totalReturnPct != null ? totalReturnPct.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        public PortfolioSummary(List<HoldingRow> rows,
                                double cash, double totalInvested,
                                double totalCurrentValue, double totalUnrealizedPnl,
                                double realizedPnl, double netWorth, double overallPnl) {
            this(rows,
                 BigDecimal.valueOf(cash), BigDecimal.valueOf(totalInvested),
                 BigDecimal.valueOf(totalCurrentValue), BigDecimal.valueOf(totalUnrealizedPnl),
                 BigDecimal.valueOf(realizedPnl), BigDecimal.valueOf(netWorth), BigDecimal.valueOf(overallPnl),
                 totalInvested > 0 ? BigDecimal.valueOf(totalUnrealizedPnl / totalInvested * 100.0) : BigDecimal.ZERO);
        }

        public boolean isPositive() {
            return totalUnrealizedPnl != null && totalUnrealizedPnl.compareTo(BigDecimal.ZERO) >= 0;
        }

        public boolean isOverallPositive() {
            return overallPnl != null && overallPnl.compareTo(BigDecimal.ZERO) >= 0;
        }

        public double getCash() { return cash.doubleValue(); }
        public double getTotalInvested() { return totalInvested.doubleValue(); }
        public double getTotalCurrentValue() { return totalCurrentValue.doubleValue(); }
        public double getTotalUnrealizedPnl() { return totalUnrealizedPnl.doubleValue(); }
        public double getRealizedPnl() { return realizedPnl.doubleValue(); }
        public double getNetWorth() { return netWorth.doubleValue(); }
        public double getOverallPnl() { return overallPnl.doubleValue(); }
        public double getTotalReturnPct() { return totalReturnPct.doubleValue(); }
    }
}
