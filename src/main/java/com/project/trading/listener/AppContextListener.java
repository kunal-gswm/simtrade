package com.project.trading.listener;

import com.project.trading.simulator.PriceSimulator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String SIMULATOR_ATTR = "priceSimulator";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PriceSimulator simulator = new PriceSimulator();
        sce.getServletContext().setAttribute(SIMULATOR_ATTR, simulator);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Object attr = sce.getServletContext().getAttribute(SIMULATOR_ATTR);
        if (attr instanceof PriceSimulator) {
            ((PriceSimulator) attr).shutdownNow();
        }
    }
}
