package com.project.trading.model;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioSummary {
    private List<PortfolioRow> rows;
    private BigDecimal cash;
    private BigDecimal totalInvested;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalUnrealizedPnl;
    private BigDecimal realizedPnl;
    private BigDecimal netWorth;
    private BigDecimal overallPnl;

    public PortfolioSummary() {
    }

    public List<PortfolioRow> getRows() {
        return rows;
    }

    public void setRows(List<PortfolioRow> rows) {
        this.rows = rows;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getTotalCurrentValue() {
        return totalCurrentValue;
    }

    public void setTotalCurrentValue(BigDecimal totalCurrentValue) {
        this.totalCurrentValue = totalCurrentValue;
    }

    public BigDecimal getTotalUnrealizedPnl() {
        return totalUnrealizedPnl;
    }

    public void setTotalUnrealizedPnl(BigDecimal totalUnrealizedPnl) {
        this.totalUnrealizedPnl = totalUnrealizedPnl;
    }

    public BigDecimal getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(BigDecimal realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public BigDecimal getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(BigDecimal netWorth) {
        this.netWorth = netWorth;
    }

    public BigDecimal getOverallPnl() {
        return overallPnl;
    }

    public void setOverallPnl(BigDecimal overallPnl) {
        this.overallPnl = overallPnl;
    }
}
