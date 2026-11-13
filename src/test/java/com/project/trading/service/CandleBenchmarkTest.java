package com.project.trading.service;

import com.project.trading.dao.CandleDAO;
import com.project.trading.dao.StockDAO;
import com.project.trading.model.Candle;
import com.project.trading.model.Stock;
import com.project.trading.util.DBConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CandleBenchmarkTest {

    private CandleService candleService;
    private CandleDAO candleDAO;
    private StockDAO stockDAO;
    private List<Stock> activeStocks;
    private int totalCandlesInDb;

    @BeforeAll
    public void setUpDataset() throws Exception {
        candleDAO = new CandleDAO();
        stockDAO = new StockDAO();
        candleService = new CandleService(candleDAO, stockDAO, CandleEngine.getInstance());

        try (Connection c = DBConnection.getConnection()) {
            activeStocks = stockDAO.findAllActive(c);
            assertFalse(activeStocks.isEmpty(), "Active stocks must be present for benchmark");

            // Seed up to 5 stocks with full multi-timeframe history
            int stocksToSeed = Math.min(5, activeStocks.size());
            for (int i = 0; i < stocksToSeed; i++) {
                Stock s = activeStocks.get(i);
                candleService.seedStockHistory(c, s);
            }
            totalCandlesInDb = (int) candleDAO.countTotal(c);
        }
    }

    @Test
    public void runDocumentedQueryBenchmark() {
        int warmupRuns = 10;
        int measuredIterations = 100;
        String[] timeframes = {"1m", "5m", "15m", "1h", "1d"};
        List<Double> latenciesMs = new ArrayList<>(measuredIterations);

        // 1. Warm-up Phase (10 iterations)
        for (int i = 0; i < warmupRuns; i++) {
            Stock s = activeStocks.get(i % Math.min(5, activeStocks.size()));
            String tf = timeframes[i % timeframes.length];
            List<Candle> result = candleService.getCandles(s.getId(), tf, 120);
            assertNotNull(result);
        }

        // 2. Measured Iterations (100 iterations)
        for (int i = 0; i < measuredIterations; i++) {
            Stock s = activeStocks.get(i % Math.min(5, activeStocks.size()));
            String tf = timeframes[i % timeframes.length];

            long startNano = System.nanoTime();
            List<Candle> result = candleService.getCandles(s.getId(), tf, 120);
            long endNano = System.nanoTime();

            assertNotNull(result);
            assertFalse(result.isEmpty(), "Query must return historical candles");

            double durationMs = (endNano - startNano) / 1_000_000.0;
            latenciesMs.add(durationMs);
        }

        // 3. Statistical Calculations
        Collections.sort(latenciesMs);
        double sum = 0.0;
        for (double l : latenciesMs) sum += l;
        double mean = sum / measuredIterations;
        double median = latenciesMs.get(measuredIterations / 2);
        int p95Index = (int) Math.ceil(0.95 * measuredIterations) - 1;
        double p95 = latenciesMs.get(p95Index);
        double min = latenciesMs.get(0);
        double max = latenciesMs.get(latenciesMs.size() - 1);

        // 4. Output Formatted Benchmark Table
        System.out.println("================================================================================");
        System.out.println("  SIMTRADE 2.0 OHLCV HISTORICAL QUERY BENCHMARK REPORT");
        System.out.println("================================================================================");
        System.out.println(String.format("  Dataset Size (Total candles in DB) : %d", totalCandlesInDb));
        System.out.println(String.format("  Number of Distinct Stocks Seeded   : %d", Math.min(5, activeStocks.size())));
        System.out.println(String.format("  Warm-up Runs                       : %d", warmupRuns));
        System.out.println(String.format("  Measured Iterations                : %d", measuredIterations));
        System.out.println(String.format("  Min Latency                        : %.3f ms", min));
        System.out.println(String.format("  Mean Latency                       : %.3f ms", mean));
        System.out.println(String.format("  Median Latency (p50)               : %.3f ms", median));
        System.out.println(String.format("  95th Percentile Latency (p95)      : %.3f ms", p95));
        System.out.println(String.format("  Max Latency                        : %.3f ms", max));
        System.out.println("================================================================================");

        assertTrue(mean < 10.0, "Mean query latency must be under 10ms with composite indexes (actual: " + mean + " ms)");
        assertTrue(p95 < 25.0, "p95 query latency must be under 25ms (actual: " + p95 + " ms)");
    }
}
