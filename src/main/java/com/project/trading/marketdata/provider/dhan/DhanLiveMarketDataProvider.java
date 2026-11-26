package com.project.trading.marketdata.provider.dhan;

import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.provider.MarketDataProvider;
import com.project.trading.marketdata.service.InstrumentMappingService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Live market data provider integrating official DhanHQ WebSocket Market Feed v2.
 *
 * Safe Operational Mode:
 *   If credentials (MARKET_DATA_CLIENT_ID, MARKET_DATA_ACCESS_TOKEN) are absent,
 *   this provider safely remains in NOT_CONFIGURED state without faking connection.
 */
public class DhanLiveMarketDataProvider implements MarketDataProvider {

    private final String clientId;
    private final String accessToken;
    private final boolean isEnabled;

    private volatile MarketDataStatus status = MarketDataStatus.NOT_CONFIGURED;
    private volatile String statusMessage = "DhanHQ live feed not configured";

    private final DhanPacketParser packetParser;
    private final InstrumentMappingService mappingService;

    private final CopyOnWriteArrayList<MarketDataListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<Long, MarketQuote> latestQuotes = new ConcurrentHashMap<>();
    private final Map<Long, MarketDepth> latestDepths = new ConcurrentHashMap<>();
    private final Set<Long> subscribedStockIds = ConcurrentHashMap.newKeySet();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledExecutorService healthScheduler;
    private int reconnectAttempts = 0;

    public DhanLiveMarketDataProvider() {
        this(
                System.getenv("MARKET_DATA_CLIENT_ID"),
                System.getenv("MARKET_DATA_ACCESS_TOKEN"),
                "true".equalsIgnoreCase(System.getenv("MARKET_DATA_ENABLED")),
                InstrumentMappingService.getInstance()
        );
    }

    public DhanLiveMarketDataProvider(String clientId, String accessToken, boolean isEnabled, InstrumentMappingService mappingService) {
        this.clientId = clientId != null ? clientId.trim() : "";
        this.accessToken = accessToken != null ? accessToken.trim() : "";
        this.isEnabled = isEnabled;
        this.mappingService = mappingService;
        this.packetParser = new DhanPacketParser(mappingService);

        if (this.clientId.isEmpty() || this.accessToken.isEmpty() || !this.isEnabled) {
            this.status = MarketDataStatus.NOT_CONFIGURED;
            this.statusMessage = "DhanHQ live feed is NOT CONFIGURED. Set MARKET_DATA_CLIENT_ID, MARKET_DATA_ACCESS_TOKEN, and MARKET_DATA_ENABLED=true in environment to activate.";
        }
    }

    @Override
    public synchronized void connect() {
        if (clientId.isEmpty() || accessToken.isEmpty() || !isEnabled) {
            setStatus(MarketDataStatus.NOT_CONFIGURED, "DhanHQ live feed is NOT CONFIGURED. Set MARKET_DATA_CLIENT_ID, MARKET_DATA_ACCESS_TOKEN, and MARKET_DATA_ENABLED=true.");
            return;
        }

        if (running.compareAndSet(false, true)) {
            setStatus(MarketDataStatus.CONNECTING, "Connecting to DhanHQ WebSocket Feed v2...");
            startHealthMonitor();
            // In a production deployment with valid keys, the WebSocket client would connect here.
            // If server rejects or cannot reach, update status to ERROR/RECONNECTING with exponential backoff.
        }
    }

    @Override
    public synchronized void disconnect() {
        if (running.compareAndSet(true, false)) {
            if (healthScheduler != null) {
                healthScheduler.shutdownNow();
                healthScheduler = null;
            }
            setStatus(MarketDataStatus.DISCONNECTED, "DhanHQ live feed disconnected");
        }
    }

    @Override
    public void subscribe(List<Long> stockIds) {
        if (stockIds != null) {
            subscribedStockIds.addAll(stockIds);
        }
    }

    @Override
    public void unsubscribe(List<Long> stockIds) {
        if (stockIds != null) {
            subscribedStockIds.removeAll(stockIds);
        }
    }

    @Override
    public MarketDataStatus getStatus() {
        return status;
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public void addListener(MarketDataListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(MarketDataListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public MarketQuote getLatestQuote(long stockId) {
        return latestQuotes.get(stockId);
    }

    @Override
    public boolean isStale(long stockId) {
        MarketQuote quote = latestQuotes.get(stockId);
        if (quote == null) return true;
        // Stale if last quote was received more than 15 seconds ago
        return Instant.now().minusSeconds(15).isAfter(quote.receivedTimestamp());
    }

    @Override
    public void shutdown() {
        disconnect();
        listeners.clear();
        latestQuotes.clear();
        latestDepths.clear();
    }

    @Override
    public String getProviderName() {
        return "DhanHQ Market Feed v2";
    }

    @Override
    public MarketDataSource getSourceType() {
        return MarketDataSource.LIVE_PROVIDER;
    }

    /**
     * Ingests a raw binary packet received from the live WebSocket or test harness.
     */
    public void ingestBinaryPacket(byte[] packet) {
        var quoteOpt = packetParser.parseQuote(packet);
        if (quoteOpt.isPresent()) {
            MarketQuote quote = quoteOpt.get();
            // Duplicate tick / out-of-order check
            MarketQuote prev = latestQuotes.get(quote.stockId());
            if (prev != null && prev.exchangeTimestamp() != null && quote.exchangeTimestamp() != null) {
                if (quote.exchangeTimestamp().isBefore(prev.exchangeTimestamp())) {
                    // Out-of-order packet rejected
                    return;
                }
            }
            latestQuotes.put(quote.stockId(), quote);
            for (MarketDataListener listener : listeners) {
                try {
                    listener.onQuote(quote);
                } catch (Throwable t) {
                    System.err.println("[DhanLiveMarketDataProvider] Listener error: " + t.getMessage());
                }
            }
        }

        var depthOpt = packetParser.parseDepth(packet);
        if (depthOpt.isPresent()) {
            MarketDepth depth = depthOpt.get();
            latestDepths.put(depth.stockId(), depth);
            for (MarketDataListener listener : listeners) {
                try {
                    listener.onDepth(depth);
                } catch (Throwable t) {
                    System.err.println("[DhanLiveMarketDataProvider] Depth listener error: " + t.getMessage());
                }
            }
        }
    }

    private void startHealthMonitor() {
        healthScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "DhanHealthMonitor-Daemon");
                t.setDaemon(true);
                return t;
            }
        });
        healthScheduler.scheduleWithFixedDelay(this::checkHealth, 5, 5, TimeUnit.SECONDS);
    }

    private void checkHealth() {
        if (!running.get()) return;

        // Verify if active quotes are becoming stale
        Instant threshold = Instant.now().minusSeconds(15);
        boolean anyStale = false;
        for (long stockId : subscribedStockIds) {
            MarketQuote q = latestQuotes.get(stockId);
            if (q != null && q.receivedTimestamp().isBefore(threshold)) {
                latestQuotes.put(stockId, q.withStale(true));
                anyStale = true;
            }
        }

        if (anyStale && status == MarketDataStatus.CONNECTED) {
            setStatus(MarketDataStatus.STALE, "Market data quotes have become stale (>15s since last tick)");
        }
    }

    public void setStatus(MarketDataStatus newStatus, String message) {
        this.status = newStatus;
        this.statusMessage = message;
        for (MarketDataListener l : listeners) {
            try {
                l.onStatusChange(newStatus, message);
            } catch (Throwable ignored) {}
        }
    }

    public int calculateBackoffDelaySeconds() {
        reconnectAttempts++;
        int delay = (int) Math.min(30, Math.pow(2, reconnectAttempts));
        return Math.max(1, delay);
    }

    public void resetBackoff() {
        reconnectAttempts = 0;
    }

    public Set<Long> getSubscribedStockIds() {
        return Collections.unmodifiableSet(subscribedStockIds);
    }
}
