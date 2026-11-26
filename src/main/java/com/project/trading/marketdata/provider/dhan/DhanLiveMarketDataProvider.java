package com.project.trading.marketdata.provider.dhan;

import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.provider.MarketDataProvider;
import com.project.trading.marketdata.service.InstrumentMappingService;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
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
 * Operational & Security Architecture:
 * - Uses standard Java 17 java.net.http.WebSocket client.
 * - Authenticates against wss://api-feed.dhan.co via secure query handshake.
 * - Dispatches JSON subscription requests (RequestCode 15/16).
 * - Ingests and normalizes live binary packets (Codes 2, 4, 8) into MarketQuote and MarketDepth.
 * - Enforces exponential backoff reconnects and staleness monitoring.
 * - Never fakes live data or masks connection state when credentials are absent or invalid.
 */
public class DhanLiveMarketDataProvider implements MarketDataProvider {

    public static final String DHAN_FEED_URL = "wss://api-feed.dhan.co";

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
    private HttpClient httpClient;
    private volatile WebSocket webSocket;

    private ScheduledExecutorService healthScheduler;
    private ScheduledExecutorService reconnectScheduler;
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
            startSchedulers();
            initiateWebSocketConnection();
        }
    }

    private synchronized void initiateWebSocketConnection() {
        if (!running.get()) return;

        setStatus(MarketDataStatus.CONNECTING, "Connecting to DhanHQ WebSocket Feed v2 at " + DHAN_FEED_URL + "...");
        try {
            String url = String.format("%s?version=2&token=%s&clientId=%s&authType=2",
                    DHAN_FEED_URL,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8));

            if (httpClient == null) {
                httpClient = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
            }

            httpClient.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .buildAsync(URI.create(url), new DhanWebSocketListener())
                    .whenComplete((ws, throwable) -> {
                        if (throwable != null) {
                            handleConnectionFailure("WebSocket connection error: " + throwable.getMessage(), throwable);
                        } else {
                            this.webSocket = ws;
                            handleConnectionSuccess();
                        }
                    });

        } catch (Throwable t) {
            handleConnectionFailure("Failed to initiate WebSocket connection: " + t.getMessage(), t);
        }
    }

    private void handleConnectionSuccess() {
        resetBackoff();
        setStatus(MarketDataStatus.CONNECTED, "Connected to DhanHQ WebSocket Feed v2");
        System.out.println("[DhanLiveMarketDataProvider] Successfully connected to live market feed.");

        // Restore active subscriptions on reconnect
        if (!subscribedStockIds.isEmpty()) {
            sendSubscriptionMessage(new ArrayList<>(subscribedStockIds), 15);
        }
    }

    private void handleConnectionFailure(String reason, Throwable t) {
        System.err.println("[DhanLiveMarketDataProvider] " + reason);
        if (t != null && t.getCause() != null) {
            System.err.println("[DhanLiveMarketDataProvider] Cause: " + t.getCause().getMessage());
        }

        if (!running.get()) return;

        int delay = calculateBackoffDelaySeconds();
        setStatus(MarketDataStatus.RECONNECTING, reason + ". Reconnecting in " + delay + "s (attempt #" + reconnectAttempts + ")...");

        if (reconnectScheduler != null && !reconnectScheduler.isShutdown()) {
            reconnectScheduler.schedule(this::initiateWebSocketConnection, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public synchronized void disconnect() {
        running.set(false);
        stopSchedulers();
        if (webSocket != null) {
            try {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Normal disconnect").join();
            } catch (Throwable ignored) {
                webSocket.abort();
            }
            webSocket = null;
        }
        setStatus(MarketDataStatus.DISCONNECTED, "DhanHQ live feed disconnected");
    }

    @Override
    public void subscribe(List<Long> stockIds) {
        if (stockIds != null && !stockIds.isEmpty()) {
            subscribedStockIds.addAll(stockIds);
            if (webSocket != null && status == MarketDataStatus.CONNECTED) {
                sendSubscriptionMessage(stockIds, 15);
            }
        }
    }

    @Override
    public void unsubscribe(List<Long> stockIds) {
        if (stockIds != null && !stockIds.isEmpty()) {
            subscribedStockIds.removeAll(stockIds);
            if (webSocket != null && status == MarketDataStatus.CONNECTED) {
                sendSubscriptionMessage(stockIds, 16);
            }
        }
    }

    /**
     * Builds official DhanHQ v2 JSON subscription payload.
     * RequestCode 15: Subscribe
     * RequestCode 16: Unsubscribe
     */
    public String buildSubscriptionJson(List<Long> stockIds, int requestCode) {
        if (stockIds == null || stockIds.isEmpty()) {
            return null;
        }

        List<InstrumentMappingService.InstrumentMapping> instruments = new ArrayList<>();
        for (long id : stockIds) {
            mappingService.getByStockId(id).ifPresent(instruments::add);
        }

        if (instruments.isEmpty()) {
            return null;
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"RequestCode\":").append(requestCode)
            .append(",\"InstrumentCount\":").append(instruments.size())
            .append(",\"InstrumentList\":[");

        for (int i = 0; i < instruments.size(); i++) {
            var inst = instruments.get(i);
            if (i > 0) json.append(",");
            json.append("{\"ExchangeSegment\":\"").append(inst.segment()).append("\",\"SecurityId\":\"").append(inst.providerSecurityId()).append("\"}");
        }
        json.append("]}");
        return json.toString();
    }

    /**
     * Sends official DhanHQ v2 JSON subscription payload over active WebSocket.
     * RequestCode 15: Subscribe
     * RequestCode 16: Unsubscribe
     */
    public boolean sendSubscriptionMessage(List<Long> stockIds, int requestCode) {
        if (stockIds == null || stockIds.isEmpty() || webSocket == null) {
            return false;
        }

        String json = buildSubscriptionJson(stockIds, requestCode);
        if (json == null) {
            return false;
        }

        try {
            webSocket.sendText(json, true);
            return true;
        } catch (Throwable t) {
            System.err.println("[DhanLiveMarketDataProvider] Failed to send subscription payload: " + t.getMessage());
            return false;
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

    private void startSchedulers() {
        healthScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "DhanHealthMonitor-Daemon");
                t.setDaemon(true);
                return t;
            }
        });
        healthScheduler.scheduleWithFixedDelay(this::checkHealth, 5, 5, TimeUnit.SECONDS);

        reconnectScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "DhanReconnect-Daemon");
                t.setDaemon(true);
                return t;
            }
        });
    }

    private void stopSchedulers() {
        if (healthScheduler != null) {
            healthScheduler.shutdownNow();
            healthScheduler = null;
        }
        if (reconnectScheduler != null) {
            reconnectScheduler.shutdownNow();
            reconnectScheduler = null;
        }
    }

    private void checkHealth() {
        if (!running.get()) return;

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

    public WebSocket getWebSocket() {
        return webSocket;
    }

    // Package-private hook for testing
    void setWebSocketForTest(WebSocket ws) {
        this.webSocket = ws;
    }

    /**
     * WebSocket listener handling lifecycle events and binary market frames.
     */
    private class DhanWebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket ws) {
            ws.request(1);
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket ws, ByteBuffer data, boolean last) {
            try {
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                ingestBinaryPacket(bytes);
            } catch (Throwable t) {
                System.err.println("[DhanWebSocketListener] Binary ingest error: " + t.getMessage());
            } finally {
                ws.request(1);
            }
            return null;
        }

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            ws.request(1);
            return null;
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            handleConnectionFailure("WebSocket stream error: " + error.getMessage(), error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            if (running.get()) {
                handleConnectionFailure("WebSocket closed by server (status " + statusCode + ": " + reason + ")", null);
            }
            return null;
        }
    }
}
