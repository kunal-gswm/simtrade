package com.project.trading.marketdata.model;

/**
 * Normalized market data event holding either a quote or depth update.
 */
public record MarketDataEvent(
        EventType type,
        MarketQuote quote,
        MarketDepth depth,
        long eventTimestamp
) {
    public enum EventType {
        QUOTE, DEPTH, STATUS_CHANGE
    }

    public static MarketDataEvent quote(MarketQuote quote) {
        return new MarketDataEvent(EventType.QUOTE, quote, null, System.currentTimeMillis());
    }

    public static MarketDataEvent depth(MarketDepth depth) {
        return new MarketDataEvent(EventType.DEPTH, null, depth, System.currentTimeMillis());
    }
}
