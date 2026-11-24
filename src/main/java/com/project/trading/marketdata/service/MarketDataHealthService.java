package com.project.trading.marketdata.service;

import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.provider.MarketDataProvider;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitors latency, packet throughput, stale event frequency, and health metrics
 * for active market data providers.
 */
public class MarketDataHealthService {

    private final AtomicLong totalQuotesReceived = new AtomicLong(0);
    private final AtomicLong totalStaleQuotes = new AtomicLong(0);
    private final AtomicLong lastQuoteTimestamp = new AtomicLong(0);
    private volatile long averageLatencyMs = 0;

    public void recordQuote(long exchangeTimestamp, long receivedTimestamp, boolean isStale) {
        totalQuotesReceived.incrementAndGet();
        if (isStale) {
            totalStaleQuotes.incrementAndGet();
        }
        lastQuoteTimestamp.set(receivedTimestamp);
        if (exchangeTimestamp > 0 && receivedTimestamp >= exchangeTimestamp) {
            long latency = receivedTimestamp - exchangeTimestamp;
            // Exponential moving average latency
            averageLatencyMs = (averageLatencyMs * 9 + latency) / 10;
        }
    }

    public long getTotalQuotesReceived() {
        return totalQuotesReceived.get();
    }

    public long getTotalStaleQuotes() {
        return totalStaleQuotes.get();
    }

    public long getLastQuoteTimestamp() {
        return lastQuoteTimestamp.get();
    }

    public long getAverageLatencyMs() {
        return averageLatencyMs;
    }

    public void reset() {
        totalQuotesReceived.set(0);
        totalStaleQuotes.set(0);
        lastQuoteTimestamp.set(0);
        averageLatencyMs = 0;
    }
}
