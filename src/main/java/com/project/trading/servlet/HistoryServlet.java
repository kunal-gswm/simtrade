package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.TradingService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@WebServlet("/app/history")
public class HistoryServlet extends BaseServlet {
    
    private final TradingService tradingService = new TradingService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser user = currentUser(req);
        
        List<Trade> trades = tradingService.getHistory(user.getId(), 100);
        req.setAttribute("trades", trades);
        
        forward(req, resp, "/WEB-INF/jsp/history.jsp");
    }
}
