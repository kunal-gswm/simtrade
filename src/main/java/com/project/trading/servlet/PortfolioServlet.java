package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.service.PortfolioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Serves the portfolio page (T39).
 * Loads real database-backed portfolio and holding metrics.
 * URL: /app/portfolio
 */
@WebServlet("/app/portfolio")
public class PortfolioServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final PortfolioService portfolioService = new PortfolioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AuthUser user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/login");
            return;
        }

        PortfolioService.PortfolioSummary summary = portfolioService.getSummaryForUser(user.getId());

        req.setAttribute("summary", summary);
        forward(req, resp, "portfolio.jsp");
    }
}
