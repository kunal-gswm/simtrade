package com.project.trading.servlet;

import com.project.trading.exception.StockNotFoundException;
import com.project.trading.exception.ValidationException;
import com.project.trading.listener.AppContextListener;
import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.project.trading.simulator.PriceSimulator;
import com.project.trading.util.FaultInjector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/prices")
public class AdminPricesServlet extends BaseServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Stock> stocks = stockService.listAll();
        req.setAttribute("stocks", stocks);

        PriceSimulator simulator = (PriceSimulator) getServletContext().getAttribute(AppContextListener.SIMULATOR_ATTR);
        boolean simulatorRunning = simulator != null && simulator.isRunning();
        req.setAttribute("simulatorRunning", simulatorRunning);
        req.setAttribute("faultOn", FaultInjector.failMidTrade);

        forward(req, resp, "admin/prices.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            flashError(req, "Action parameter is required.");
            redirect(req, resp, "/admin/prices");
            return;
        }

        try {
            switch (action) {
                case "price": {
                    long id = parseLongParam(req, "id");
                    if (id <= 0) {
                        throw new ValidationException("Invalid stock ID.");
                    }
                    String priceStr = req.getParameter("price");
                    if (priceStr == null || priceStr.trim().isEmpty()) {
                        throw new ValidationException("Price cannot be empty.");
                    }
                    BigDecimal price;
                    try {
                        price = new BigDecimal(priceStr.trim());
                    } catch (NumberFormatException e) {
                        throw new ValidationException("Price must be a valid number.");
                    }

                    stockService.updatePrice(id, price);
                    flashSuccess(req, "Price updated successfully.");
                    break;
                }
                case "simulator": {
                    String toggle = req.getParameter("toggle");
                    PriceSimulator simulator = (PriceSimulator) getServletContext().getAttribute(AppContextListener.SIMULATOR_ATTR);
                    if (simulator != null) {
                        if ("start".equalsIgnoreCase(toggle)) {
                            simulator.start(5);
                            flashSuccess(req, "Price simulator started (ticks every 5 seconds).");
                        } else {
                            simulator.stop();
                            flashSuccess(req, "Price simulator stopped.");
                        }
                    } else {
                        flashError(req, "Price simulator service not available.");
                    }
                    break;
                }
                case "fault": {
                    FaultInjector.failMidTrade = !FaultInjector.failMidTrade;
                    if (FaultInjector.failMidTrade) {
                        flashSuccess(req, "Fault injection ENABLED: Trades will fail mid-transaction to demonstrate ACID rollback.");
                    } else {
                        flashSuccess(req, "Fault injection DISABLED: Trades will execute normally.");
                    }
                    break;
                }
                default:
                    flashError(req, "Unknown action: " + action);
            }
        } catch (ValidationException | StockNotFoundException e) {
            flashError(req, e.getMessage());
        } catch (Exception e) {
            flashError(req, "Failed to perform operation: " + e.getMessage());
        }

        redirect(req, resp, "/admin/prices");
    }
}
