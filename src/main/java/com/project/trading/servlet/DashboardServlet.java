package com.project.trading.servlet;

import com.project.trading.model.Portfolio;
import com.project.trading.model.TradeOrder;
import com.project.trading.service.PortfolioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Serves the user dashboard (T41).
 * Reads the portfolio from session, computes P&L, and forwards to dashboard.jsp.
 * URL: /app/dashboard
 */
@WebServlet("/app/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PortfolioService portfolioService = new PortfolioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userPortfolio") == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); // fall back to main servlet
            return;
        }

        Portfolio portfolio = (Portfolio) session.getAttribute("userPortfolio");
        PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(portfolio);

        @SuppressWarnings("unchecked")
        List<TradeOrder> recentTrades = (List<TradeOrder>) session.getAttribute("tradeFeed");

        req.setAttribute("summary", summary);
        req.setAttribute("recentTrades", recentTrades);
        req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
    }
}
