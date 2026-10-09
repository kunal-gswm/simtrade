package com.project.trading.servlet;

import com.project.trading.model.TradeOrder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Serves the trade history page (T40).
 * URL: /app/history
 */
@WebServlet("/app/history")
public class HistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        @SuppressWarnings("unchecked")
        List<TradeOrder> trades = (session != null)
                ? (List<TradeOrder>) session.getAttribute("tradeFeed")
                : Collections.emptyList();

        if (trades == null) trades = Collections.emptyList();

        req.setAttribute("trades", trades);
        req.getRequestDispatcher("/WEB-INF/jsp/history.jsp").forward(req, resp);
    }
}
