package com.project.trading.servlet;

import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/app/market")
public class MarketServlet extends BaseServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        List<Stock> stocks = stockService.search(q);
        req.setAttribute("stocks", stocks);
        req.setAttribute("q", q != null ? q.trim() : "");
        forward(req, resp, "market.jsp");
    }
}
