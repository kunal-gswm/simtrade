package com.project.trading.listener;

import com.project.trading.service.CandleEngine;
import com.project.trading.service.CandleService;
import com.project.trading.simulator.PriceSimulator;
import com.project.trading.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String SIMULATOR_ATTR = "priceSimulator";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new CandleService().seedInitialDataIfEmpty();
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
        CandleEngine.getInstance().stop();
        DBConnection.closePool();
    }
}
