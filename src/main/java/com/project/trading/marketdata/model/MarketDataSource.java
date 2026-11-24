package com.project.trading.marketdata.model;

/**
 * Explicit origin source of financial market quotes and candle data.
 */
public enum MarketDataSource {
    LIVE_PROVIDER("Live Exchange Feed"),
    SIMULATED_STREAM("Simulated Paper Stream"),
    SYNTHETIC_BACKFILL("Synthetic Historical Backfill"),
    EXCHANGE_IMPORT("Exchange Historical Import");

    private final String displayName;

    MarketDataSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
