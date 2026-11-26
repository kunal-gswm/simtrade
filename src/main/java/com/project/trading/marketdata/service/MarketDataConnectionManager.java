package com.project.trading.marketdata.service;

import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.provider.MarketDataProvider;
import com.project.trading.marketdata.provider.dhan.DhanLiveMarketDataProvider;
import com.project.trading.marketdata.provider.sim.SimulatorMarketDataProvider;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Centralized Connection Manager for Market Data Providers.
 * Coordinates active provider selection, lifecycle, and global event dispatching.
 */
public class MarketDataConnectionManager {

    private static final MarketDataConnectionManager INSTANCE = new MarketDataConnectionManager();

    private volatile MarketDataProvider activeProvider;
    private final MarketDataHealthService healthService = new MarketDataHealthService();
    private final CopyOnWriteArrayList<MarketDataListener> globalListeners = new CopyOnWriteArrayList<>();

    private final MarketDataListener providerBridgeListener = new MarketDataListener() {
        @Override
        public void onQuote(MarketQuote quote) {
            if (quote != null) {
                long exTime = quote.exchangeTimestamp() != null ? quote.exchangeTimestamp().toEpochMilli() : 0;
                long recTime = quote.receivedTimestamp() != null ? quote.receivedTimestamp().toEpochMilli() : System.currentTimeMillis();
                healthService.recordQuote(exTime, recTime, quote.isStale());
            }
            for (MarketDataListener l : globalListeners) {
                try {
                    l.onQuote(quote);
                } catch (Throwable t) {
                    System.err.println("[MarketDataConnectionManager] Listener error onQuote: " + t.getMessage());
                }
            }
        }

        @Override
        public void onDepth(MarketDepth depth) {
            for (MarketDataListener l : globalListeners) {
                try {
                    l.onDepth(depth);
                } catch (Throwable t) {
                    System.err.println("[MarketDataConnectionManager] Listener error onDepth: " + t.getMessage());
                }
            }
        }

        @Override
        public void onStatusChange(MarketDataStatus status, String message) {
            for (MarketDataListener l : globalListeners) {
                try {
                    l.onStatusChange(status, message);
                } catch (Throwable t) {
                    System.err.println("[MarketDataConnectionManager] Listener error onStatusChange: " + t.getMessage());
                }
            }
        }

        @Override
        public void onError(String error, Throwable t) {
            for (MarketDataListener l : globalListeners) {
                try {
                    l.onError(error, t);
                } catch (Throwable ignored) {}
            }
        }
    };

    public static MarketDataConnectionManager getInstance() {
        return INSTANCE;
    }

    public MarketDataConnectionManager() {
        initializeDefaultProvider();
    }

    public synchronized void initializeDefaultProvider() {
        String providerEnv = System.getenv("MARKET_DATA_PROVIDER");
        if (providerEnv != null && providerEnv.trim().equalsIgnoreCase("DHAN")) {
            setProvider(new DhanLiveMarketDataProvider());
        } else {
            setProvider(new SimulatorMarketDataProvider());
        }
    }

    public synchronized void setProvider(MarketDataProvider provider) {
        if (this.activeProvider != null) {
            this.activeProvider.removeListener(providerBridgeListener);
            this.activeProvider.shutdown();
        }
        this.activeProvider = provider;
        if (this.activeProvider != null) {
            this.activeProvider.addListener(providerBridgeListener);
            this.activeProvider.connect();
        }
    }

    public MarketDataProvider getActiveProvider() {
        return activeProvider;
    }

    public MarketDataHealthService getHealthService() {
        return healthService;
    }

    public void addGlobalListener(MarketDataListener listener) {
        if (listener != null) {
            globalListeners.add(listener);
        }
    }

    public void removeGlobalListener(MarketDataListener listener) {
        if (listener != null) {
            globalListeners.remove(listener);
        }
    }

    public MarketQuote getLatestQuote(long stockId) {
        return activeProvider != null ? activeProvider.getLatestQuote(stockId) : null;
    }

    public boolean isStale(long stockId) {
        return activeProvider != null && activeProvider.isStale(stockId);
    }

    public MarketDataStatus getStatus() {
        return activeProvider != null ? activeProvider.getStatus() : MarketDataStatus.DISCONNECTED;
    }

    public String getStatusMessage() {
        return activeProvider != null ? activeProvider.getStatusMessage() : "No active provider";
    }

    public MarketDataSource getSourceType() {
        return activeProvider != null ? activeProvider.getSourceType() : MarketDataSource.SIMULATED_STREAM;
    }

    public void subscribe(List<Long> stockIds) {
        if (activeProvider != null) {
            activeProvider.subscribe(stockIds);
        }
    }

    public void shutdown() {
        if (activeProvider != null) {
            activeProvider.shutdown();
        }
        globalListeners.clear();
        healthService.reset();
    }
}
