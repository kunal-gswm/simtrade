package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.model.PortfolioSummary;
import com.project.trading.service.PortfolioService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/app/portfolio")
public class PortfolioServlet extends BaseServlet {
    
    private final PortfolioService portfolioService = new PortfolioService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser user = currentUser(req);
        
        PortfolioSummary summary = portfolioService.getSummary(user.getId());
        req.setAttribute("summary", summary);
        
        forward(req, resp, "/WEB-INF/jsp/portfolio.jsp");
    }
}
