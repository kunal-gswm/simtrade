package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Serves the user dashboard (T41).
 * Loads real database-backed portfolio metrics and trade history.
 * URL: /app/dashboard
 */
@WebServlet("/app/dashboard")
public class DashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final PortfolioService portfolioService = new PortfolioService();
    private final TradingService tradingService = new TradingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AuthUser user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/login");
            return;
        }

        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(user.getId());
        List<Trade> recentTrades = tradingService.getHistory(user.getId(), 5);

        req.setAttribute("summary", summary);
        req.setAttribute("recentTrades", recentTrades);
        forward(req, resp, "dashboard.jsp");
    }
}
