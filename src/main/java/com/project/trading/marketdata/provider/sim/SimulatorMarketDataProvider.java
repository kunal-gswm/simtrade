package com.project.trading.marketdata.provider.sim;

import com.project.trading.dao.StockDAO;
import com.project.trading.marketdata.model.MarketDataSource;
import com.project.trading.marketdata.model.MarketDataStatus;
import com.project.trading.marketdata.model.MarketDepth;
import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.provider.MarketDataProvider;
import com.project.trading.model.Stock;
import com.project.trading.simulator.PriceGenerator;
import com.project.trading.simulator.RandomWalkPriceGenerator;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
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
 * Simulator market data provider that generates random-walk price quotes and
 * synthetic order book depth for offline paper testing and simulated trading.
 * Visibly marked as SIMULATED_STREAM.
 */
public class SimulatorMarketDataProvider implements MarketDataProvider {

    private final StockDAO stockDAO;
    private final PriceGenerator priceGenerator;
    private final CopyOnWriteArrayList<MarketDataListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<Long, MarketQuote> latestQuotes = new ConcurrentHashMap<>();
    private final Set<Long> subscribedStockIds = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler;

    private volatile MarketDataStatus status = MarketDataStatus.DISCONNECTED;
    private volatile String statusMessage = "Simulator stopped";

    public SimulatorMarketDataProvider() {
        this(new StockDAO(), new RandomWalkPriceGenerator());
    }

    public SimulatorMarketDataProvider(StockDAO stockDAO, PriceGenerator priceGenerator) {
        this.stockDAO = stockDAO;
        this.priceGenerator = priceGenerator;
    }

    @Override
    public synchronized void connect() {
        if (running.compareAndSet(false, true)) {
            status = MarketDataStatus.CONNECTED;
            statusMessage = "Simulated stream active (Random-walk simulator)";
            scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "SimulatorMarketDataProvider-Daemon");
                    t.setDaemon(true);
                    return t;
                }
            });
            scheduler.scheduleAtFixedRate(this::tick, 1, 3, TimeUnit.SECONDS);
            notifyStatusChange();
        }
    }

    @Override
    public synchronized void disconnect() {
        if (running.compareAndSet(true, false)) {
            if (scheduler != null) {
                scheduler.shutdownNow();
                scheduler = null;
            }
            status = MarketDataStatus.DISCONNECTED;
            statusMessage = "Simulator stopped";
            notifyStatusChange();
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
        return false;
    }

    @Override
    public void shutdown() {
        disconnect();
        listeners.clear();
        latestQuotes.clear();
    }

    @Override
    public String getProviderName() {
        return "Internal Simulator Stream";
    }

    @Override
    public MarketDataSource getSourceType() {
        return MarketDataSource.SIMULATED_STREAM;
    }

    private void tick() {
        try {
            Instant now = Instant.now();
            try (Connection c = DBConnection.getConnection()) {
                List<Stock> activeStocks = stockDAO.findAllActive(c);
                if (activeStocks == null || activeStocks.isEmpty()) {
                    return;
                }
                for (Stock stock : activeStocks) {
                    BigDecimal prevPrice = stock.getPrice();
                    BigDecimal nextPrice = priceGenerator.next(prevPrice);

                    BigDecimal spread = new BigDecimal("0.05");
                    BigDecimal bid = nextPrice.subtract(spread).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal ask = nextPrice.add(spread).setScale(2, RoundingMode.HALF_UP);

                    MarketQuote quote = new MarketQuote(
                            stock.getId(),
                            stock.getSymbol(),
                            "NSE",
                            String.valueOf(stock.getId()),
                            MarketDataSource.SIMULATED_STREAM,
                            now,
                            now,
                            nextPrice,
                            stock.getPrevPrice(),
                            stock.getPrice(),
                            nextPrice.max(stock.getPrice()),
                            nextPrice.min(stock.getPrice()),
                            1000L,
                            bid, 100,
                            ask, 100,
                            false,
                            "VALID"
                    );

                    latestQuotes.put(stock.getId(), quote);

                    // Notify quote listeners
                    for (MarketDataListener listener : listeners) {
                        try {
                            listener.onQuote(quote);
                        } catch (Throwable ignored) {}
                    }
                }
            }
        } catch (Throwable t) {
            System.err.println("[SimulatorMarketDataProvider] Tick error: " + t.getMessage());
        }
    }

    private void notifyStatusChange() {
        for (MarketDataListener listener : listeners) {
            try {
                listener.onStatusChange(status, statusMessage);
            } catch (Throwable ignored) {}
        }
    }
}
