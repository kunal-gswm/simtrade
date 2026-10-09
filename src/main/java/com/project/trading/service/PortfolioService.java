package com.project.trading.service;

import com.project.trading.model.Holding;
import com.project.trading.model.Portfolio;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates portfolio data and calls PnLCalculator to produce
 * the numbers that the JSP layer needs.
 *
 * Dev 4 — T38.
 *
 * NOTE: This branch stores portfolios in HTTP session (in-memory).
 * When the DB layer (feature/database branch) is merged, this service
 * should be updated to load from PortfolioDAO instead of accepting
 * the Portfolio object directly.
 */
public class PortfolioService {

    /**
     * Builds a fully computed PortfolioSummary from the session-held Portfolio object.
     *
     * @param portfolio the Portfolio loaded from session (never null)
     * @return a PortfolioSummary ready to be set as a request attribute
     */
    public PortfolioSummary buildSummary(Portfolio portfolio) {
        double cash = portfolio.getCashBalance();

        double totalInvested = 0;
        double totalCurrentValue = 0;

        List<HoldingRow> rows = new ArrayList<>();

        for (Holding h : portfolio.getHoldings()) {
            double invested = PnLCalculator.calculateInvested(h.getQuantity(), h.getAveragePrice());
            double currentValue = PnLCalculator.calculateCurrentValue(h.getQuantity(), h.getCurrentPrice());
            double unrealizedPnl = PnLCalculator.calculateUnrealizedPnl(currentValue, invested);
            double unrealizedPct = PnLCalculator.calculateUnrealizedPct(unrealizedPnl, invested);

            rows.add(new HoldingRow(
                    h.getStockSymbol(),
                    h.getStockName(),
                    h.getQuantity(),
                    h.getAveragePrice(),
                    h.getCurrentPrice(),
                    invested,
                    currentValue,
                    unrealizedPnl,
                    unrealizedPct
            ));

            totalInvested     += invested;
            totalCurrentValue += currentValue;
        }

        double totalUnrealizedPnl = PnLCalculator.calculateUnrealizedPnl(totalCurrentValue, totalInvested);
        double netWorth           = PnLCalculator.calculateNetWorth(cash, totalCurrentValue);
        double overallPnl         = PnLCalculator.calculateOverallPnl(netWorth);

        return new PortfolioSummary(
                rows,
                cash,
                PnLCalculator.round2(totalInvested),
                PnLCalculator.round2(totalCurrentValue),
                PnLCalculator.round2(totalUnrealizedPnl),
                0.0,   // realizedPnl — tracked per-trade; not yet persisted on this branch
                netWorth,
                overallPnl
        );
    }

    // ── Inner value-object classes ────────────────────────────────────────────

    /** One row in the holdings table on portfolio.jsp */
    public static class HoldingRow {
        public final String symbol;
        public final String companyName;
        public final int    quantity;
        public final double avgBuyPrice;
        public final double currentPrice;
        public final double invested;
        public final double currentValue;
        public final double unrealizedPnl;
        public final double unrealizedPct;

        public HoldingRow(String symbol, String companyName, int quantity,
                          double avgBuyPrice, double currentPrice,
                          double invested, double currentValue,
                          double unrealizedPnl, double unrealizedPct) {
            this.symbol        = symbol;
            this.companyName   = companyName;
            this.quantity      = quantity;
            this.avgBuyPrice   = avgBuyPrice;
            this.currentPrice  = currentPrice;
            this.invested      = invested;
            this.currentValue  = currentValue;
            this.unrealizedPnl = unrealizedPnl;
            this.unrealizedPct = unrealizedPct;
        }

        public boolean isPositive() { return unrealizedPnl >= 0; }
    }

    /** Aggregated summary for dashboard.jsp and portfolio.jsp */
    public static class PortfolioSummary {
        public final List<HoldingRow> rows;
        public final double cash;
        public final double totalInvested;
        public final double totalCurrentValue;
        public final double totalUnrealizedPnl;
        public final double realizedPnl;
        public final double netWorth;
        public final double overallPnl;

        public PortfolioSummary(List<HoldingRow> rows,
                                double cash, double totalInvested,
                                double totalCurrentValue, double totalUnrealizedPnl,
                                double realizedPnl, double netWorth, double overallPnl) {
            this.rows               = rows;
            this.cash               = cash;
            this.totalInvested      = totalInvested;
            this.totalCurrentValue  = totalCurrentValue;
            this.totalUnrealizedPnl = totalUnrealizedPnl;
            this.realizedPnl        = realizedPnl;
            this.netWorth           = netWorth;
            this.overallPnl         = overallPnl;
        }
    }
}
