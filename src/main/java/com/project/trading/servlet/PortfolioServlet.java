package com.project.trading.servlet;

import com.project.trading.model.Portfolio;
import com.project.trading.service.PortfolioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Serves the portfolio page (T39).
 * URL: /app/portfolio
 */
@WebServlet("/app/portfolio")
public class PortfolioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PortfolioService portfolioService = new PortfolioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userPortfolio") == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        Portfolio portfolio = (Portfolio) session.getAttribute("userPortfolio");
        PortfolioService.PortfolioSummary summary = portfolioService.buildSummary(portfolio);

        req.setAttribute("summary", summary);
        req.getRequestDispatcher("/WEB-INF/jsp/portfolio.jsp").forward(req, resp);
    }
}
