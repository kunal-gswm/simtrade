package com.project.trading.servlet;

import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@WebServlet("/app/market")
public class MarketServlet extends BaseServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        String sort = req.getParameter("sort");
        
        List<Stock> stocks = stockService.search(q);
        
        if (sort != null) {
            switch (sort) {
                case "symbol":
                    stocks.sort(Comparator.comparing(Stock::getSymbol));
                    break;
                case "price":
                    stocks.sort(Comparator.comparing(Stock::getPrice).reversed());
                    break;
                case "change":
                    stocks.sort((s1, s2) -> {
                        BigDecimal chg1 = BigDecimal.ZERO;
                        if (s1.getPrevPrice() != null && s1.getPrevPrice().compareTo(BigDecimal.ZERO) > 0) {
                            chg1 = s1.getPrice().subtract(s1.getPrevPrice()).divide(s1.getPrevPrice(), 4, RoundingMode.HALF_UP);
                        }
                        BigDecimal chg2 = BigDecimal.ZERO;
                        if (s2.getPrevPrice() != null && s2.getPrevPrice().compareTo(BigDecimal.ZERO) > 0) {
                            chg2 = s2.getPrice().subtract(s2.getPrevPrice()).divide(s2.getPrevPrice(), 4, RoundingMode.HALF_UP);
                        }
                        return chg2.compareTo(chg1);
                    });
                    break;
            }
        }
        
        req.setAttribute("stocks", stocks);
        req.setAttribute("q", q != null ? q.trim() : "");
        req.setAttribute("sort", sort != null ? sort : "");
        forward(req, resp, "market.jsp");
    }
}
