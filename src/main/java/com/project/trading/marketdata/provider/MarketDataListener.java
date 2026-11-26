package com.project.trading.marketdata.provider;

import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;

/**
 * Listener interface for receiving real-time market data updates and connection status.
 */
public interface MarketDataListener {
    default void onQuote(MarketQuote quote) {}
    default void onDepth(MarketDepth depth) {}
    default void onStatusChange(MarketDataStatus status, String message) {}
    default void onError(String error, Throwable t) {}
}
