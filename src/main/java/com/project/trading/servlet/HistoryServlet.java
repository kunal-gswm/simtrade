package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.TradingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Serves the trade history page (T40).
 * Loads real executed trades from the database via TradingService.
 * URL: /app/history
 */
@WebServlet("/app/history")
public class HistoryServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final TradingService tradingService = new TradingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AuthUser user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/login");
            return;
        }

        List<Trade> trades = tradingService.getHistory(user.getId(), 100);

        req.setAttribute("trades", trades);
        forward(req, resp, "history.jsp");
    }
}
