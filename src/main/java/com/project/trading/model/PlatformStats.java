package com.project.trading.model;

import java.math.BigDecimal;
import java.util.Map;

public class PlatformStats {
    private int totalUsers;
    private int activeStocks;
    private int totalTrades;
    private BigDecimal totalTradedValue;
    private Map<String, Integer> topStocksByTrades;

    public PlatformStats() {
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getActiveStocks() {
        return activeStocks;
    }

    public void setActiveStocks(int activeStocks) {
        this.activeStocks = activeStocks;
    }

    public int getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(int totalTrades) {
        this.totalTrades = totalTrades;
    }

    public BigDecimal getTotalTradedValue() {
        return totalTradedValue;
    }

    public void setTotalTradedValue(BigDecimal totalTradedValue) {
        this.totalTradedValue = totalTradedValue;
    }

    public Map<String, Integer> getTopStocksByTrades() {
        return topStocksByTrades;
    }

    public void setTopStocksByTrades(Map<String, Integer> topStocksByTrades) {
        this.topStocksByTrades = topStocksByTrades;
    }
}
