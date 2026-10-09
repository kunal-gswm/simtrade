package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.model.PortfolioSummary;
import com.project.trading.model.Trade;
import com.project.trading.service.PortfolioService;
import com.project.trading.service.TradingService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@WebServlet("/app/dashboard")
public class DashboardServlet extends BaseServlet {
    
    private final PortfolioService portfolioService = new PortfolioService();
    private final TradingService tradingService = new TradingService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser user = currentUser(req);
        
        PortfolioSummary summary = portfolioService.getSummary(user.getId());
        List<Trade> recentTrades = tradingService.getHistory(user.getId(), 5);
        
        req.setAttribute("summary", summary);
        req.setAttribute("recentTrades", recentTrades);
        
        forward(req, resp, "/WEB-INF/jsp/dashboard.jsp");
    }
}
