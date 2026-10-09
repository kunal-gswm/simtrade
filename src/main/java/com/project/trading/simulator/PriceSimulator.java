package com.project.trading.simulator;

import com.project.trading.dao.StockDAO;
import com.project.trading.model.Stock;
import com.project.trading.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
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
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler;

    public PriceSimulator() {
        this(new StockDAO(), new RandomWalkPriceGenerator());
    }

    public PriceSimulator(StockDAO stockDAO, PriceGenerator priceGenerator) {
        this.stockDAO = stockDAO;
        this.priceGenerator = priceGenerator;
    }

    public synchronized void start(int intervalSec) {
        if (running.compareAndSet(false, true)) {
            scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "PriceSimulator-Daemon");
                    t.setDaemon(true);
                    return t;
                }
            });
            int interval = intervalSec <= 0 ? 5 : intervalSec;
            scheduler.scheduleAtFixedRate(this::tick, interval, interval, TimeUnit.SECONDS);
        }
    }

    public synchronized void stop() {
        if (running.compareAndSet(true, false)) {
            if (scheduler != null) {
                scheduler.shutdownNow();
                scheduler = null;
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
            try (Connection c = DBConnection.getConnection()) {
                List<Stock> activeStocks = stockDAO.findAllActive(c);
                if (activeStocks == null || activeStocks.isEmpty()) {
                    return;
                }
                Map<Long, BigDecimal> newPrices = new HashMap<>();
                for (Stock stock : activeStocks) {
                    BigDecimal nextPrice = priceGenerator.next(stock.getPrice());
                    newPrices.put(stock.getId(), nextPrice);
                }
                stockDAO.updatePricesBatch(c, newPrices);
            }
        } catch (Throwable t) {
            // Uncaught exception silently cancels all future runs of a scheduled task; catch Throwable explicitly
            System.err.println("[PriceSimulator] Tick error: " + t.getMessage());
        }
    }
}
