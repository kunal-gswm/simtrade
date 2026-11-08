package com.project.trading.web;

import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Role;
import com.project.trading.model.Stock;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.StockService;
import com.project.trading.service.TradingService;
import com.project.trading.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller Servlet for /dashboard and /trade legacy routes.
 * Redirects UI requests to modern database-backed /app routes
 * and processes simulated trade orders using TradingService.
 */
@WebServlet(name = "LegacyDashboardServlet", urlPatterns = {"/dashboard", "/trade"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TradingService tradingService = new TradingService();
    private final PortfolioService portfolioService = new PortfolioService();
    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        AuthUser user = SessionUtil.getAuthUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (user.getRole() == Role.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        String servletPath = req.getServletPath();
        if ("/trade".equals(servletPath)) {
            resp.sendRedirect(req.getContextPath() + "/app/market");
        } else {
            resp.sendRedirect(req.getContextPath() + "/app/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        AuthUser user = SessionUtil.getAuthUser(req);
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || "json".equalsIgnoreCase(req.getParameter("format"));

        if (user == null) {
            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\":false,\"message\":\"Session expired. Please log in.\"}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }

        String symbol = req.getParameter("symbol");
        String side = req.getParameter("side");
        String quantityStr = req.getParameter("quantity");

        boolean isSuccess = false;
        String statusMessage = "";
        PortfolioService.PortfolioSummary summary = null;

        try {
            if (symbol == null || symbol.trim().isEmpty()) {
                throw new IllegalArgumentException("Stock symbol is required.");
            }
            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Quantity is required.");
            }

            int quantity = Integer.parseInt(quantityStr.trim());
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }

            Stock stock = stockService.getBySymbol(symbol.trim().toUpperCase());
            Trade trade;

            if ("BUY".equalsIgnoreCase(side)) {
                trade = tradingService.buy(user.getId(), stock.getId(), quantity);
                statusMessage = "Order executed successfully! Bought " + quantity + " shares of " + stock.getSymbol() + " at ₹" + trade.getPrice();
                isSuccess = true;
            } else if ("SELL".equalsIgnoreCase(side)) {
                trade = tradingService.sell(user.getId(), stock.getId(), quantity);
                statusMessage = "Order executed successfully! Sold " + quantity + " shares of " + stock.getSymbol() + " at ₹" + trade.getPrice();
                isSuccess = true;
            } else {
                throw new IllegalArgumentException("Invalid order side. Must be BUY or SELL.");
            }

            summary = portfolioService.getSummaryForUser(user.getId());

        } catch (AppException | IllegalArgumentException e) {
            statusMessage = e.getMessage();
            isSuccess = false;
        } catch (DataAccessException e) {
            statusMessage = "Trade processing failed due to database error.";
            isSuccess = false;
        }

        if (isAjax) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            if (isSuccess && summary != null) {
                out.print("{\"success\":true" +
                        ",\"message\":\"" + statusMessage.replace("\"", "\\\"") + "\"" +
                        ",\"cashBalance\":" + summary.cash +
                        ",\"totalValue\":" + summary.totalCurrentValue +
                        ",\"netWorth\":" + summary.netWorth +
                        "}");
            } else {
                out.print("{\"success\":false,\"message\":\"" + statusMessage.replace("\"", "\\\"") + "\"}");
            }
            out.flush();
        } else {
            if (isSuccess) {
                SessionUtil.flashSuccess(req, statusMessage);
                resp.sendRedirect(req.getContextPath() + "/app/portfolio");
            } else {
                SessionUtil.flashError(req, statusMessage);
                resp.sendRedirect(req.getContextPath() + "/app/market");
            }
        }
    }
}
