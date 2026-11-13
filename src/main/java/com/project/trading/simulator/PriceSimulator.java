package com.project.trading.simulator;

import com.project.trading.broadcaster.MarketDataBroadcaster;
import com.project.trading.dao.StockDAO;
import com.project.trading.model.Stock;
import com.project.trading.service.CandleEngine;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PriceSimulator {

    private final StockDAO stockDAO;
    private final PriceGenerator priceGenerator;
    private final CandleEngine candleEngine;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler;

    public PriceSimulator() {
        this(new StockDAO(), new RandomWalkPriceGenerator(), CandleEngine.getInstance());
    }

    public PriceSimulator(StockDAO stockDAO, PriceGenerator priceGenerator) {
        this(stockDAO, priceGenerator, CandleEngine.getInstance());
    }

    public PriceSimulator(StockDAO stockDAO, PriceGenerator priceGenerator, CandleEngine candleEngine) {
        this.stockDAO = stockDAO;
        this.priceGenerator = priceGenerator;
        this.candleEngine = candleEngine;
    }

    public synchronized void start(int intervalSec) {
        if (running.compareAndSet(false, true)) {
            if (candleEngine != null && !candleEngine.isRunning()) {
                candleEngine.start(10);
            }
            scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "PriceSimulator-Daemon");
                    t.setDaemon(true);
                    return t;
                }
            });
            int interval = intervalSec <= 0 ? 3 : intervalSec;
            scheduler.scheduleAtFixedRate(this::tick, interval, interval, TimeUnit.SECONDS);
        }
    }

    public synchronized void stop() {
        if (running.compareAndSet(true, false)) {
            if (scheduler != null) {
                scheduler.shutdownNow();
                scheduler = null;
            }
            if (candleEngine != null && candleEngine.isRunning()) {
                candleEngine.stop();
            }
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void shutdownNow() {
        stop();
    }

    private void tick() {
        try {
            Map<Long, BigDecimal> newPrices = new HashMap<>();
            Instant now = Instant.now();
            try (Connection c = DBConnection.getConnection()) {
                List<Stock> activeStocks = stockDAO.findAllActive(c);
                if (activeStocks == null || activeStocks.isEmpty()) {
                    return;
                }
                for (Stock stock : activeStocks) {
                    BigDecimal prevPrice = stock.getPrice();
                    BigDecimal nextPrice = priceGenerator.next(prevPrice);
                    newPrices.put(stock.getId(), nextPrice);
                    if (candleEngine != null) {
                        candleEngine.onPriceTick(stock.getId(), nextPrice, prevPrice, now);
                    }
                }
                stockDAO.updatePricesBatch(c, newPrices);
            }
            // Notify live Vaadin sessions and listeners after batch persistence
            MarketDataBroadcaster.broadcastPrices(newPrices);
        } catch (Throwable t) {
            // Uncaught exception silently cancels all future runs of a scheduled task; catch Throwable explicitly
            System.err.println("[PriceSimulator] Tick error: " + t.getMessage());
        }
    }
}
