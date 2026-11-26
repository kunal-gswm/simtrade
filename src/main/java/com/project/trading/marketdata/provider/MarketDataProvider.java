package com.project.trading.marketdata.provider;

import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketQuote;

import java.util.List;

/**
 * Common, provider-neutral interface for real-time market data providers.
 */
public interface MarketDataProvider {

    void connect();

    void disconnect();

    void subscribe(List<Long> stockIds);

    void unsubscribe(List<Long> stockIds);

    MarketDataStatus getStatus();

    String getStatusMessage();

    void addListener(MarketDataListener listener);

    void removeListener(MarketDataListener listener);

    MarketQuote getLatestQuote(long stockId);

    boolean isStale(long stockId);

    void shutdown();

    String getProviderName();

    MarketDataSource getSourceType();
}
