package com.project.trading.listener;

import com.project.trading.marketdata.model.MarketQuote;
import com.project.trading.marketdata.provider.MarketDataListener;
import com.project.trading.marketdata.service.MarketDataConnectionManager;
import com.project.trading.order.engine.PaperExecutionEngine;
import com.project.trading.service.CandleEngine;
import com.project.trading.service.CandleService;
import com.project.trading.simulator.PriceSimulator;
import com.project.trading.util.DBConnection;
import com.project.trading.util.DatabaseMigrator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String SIMULATOR_ATTR = "priceSimulator";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseMigrator.migrate();
        new CandleService().seedInitialDataIfEmpty();
        
        // Connect CandleEngine to MarketDataConnectionManager
        MarketDataConnectionManager.getInstance().addGlobalListener(new MarketDataListener() {
            @Override
            public void onQuote(MarketQuote quote) {
                CandleEngine.getInstance().onMarketQuote(quote);
            }
        });

        // Start Simulated Execution Engine for Paper Orders
        PaperExecutionEngine.getInstance().start();

        PriceSimulator simulator = new PriceSimulator();
        simulator.start(3); // Controlled 3-second simulation interval
        sce.getServletContext().setAttribute(SIMULATOR_ATTR, simulator);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Object attr = sce.getServletContext().getAttribute(SIMULATOR_ATTR);
        if (attr instanceof PriceSimulator) {
            ((PriceSimulator) attr).shutdownNow();
        }
        PaperExecutionEngine.getInstance().stop();
        MarketDataConnectionManager.getInstance().shutdown();
        CandleEngine.getInstance().stop();
        DBConnection.closePool();
    }
}
