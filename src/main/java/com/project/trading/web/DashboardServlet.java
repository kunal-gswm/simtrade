package com.project.trading.web;

import com.project.trading.model.Holding;
import com.project.trading.model.MarketIndex;
import com.project.trading.model.Portfolio;
import com.project.trading.model.Stock;
import com.project.trading.model.TradeOrder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controller Servlet for Sim Trade Dashboard.
 * Serves the NSE India-inspired trading dashboard and processes simulated trade orders.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/trade"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Thread-safe in-memory stock exchange catalog
    private final Map<String, Stock> stockCatalog = new ConcurrentHashMap<>();
    private final List<MarketIndex> marketIndices = new CopyOnWriteArrayList<>();
    private final List<TradeOrder> exchangeTradeFeed = new CopyOnWriteArrayList<>();

    @Override
    public void init() throws ServletException {
        super.init();
        initExchangeData();
    }

    private void initExchangeData() {
        // Indian Market Indices (NSE/BSE)
        marketIndices.clear();
        marketIndices.add(new MarketIndex("NIFTY 50", 24825.40, 182.35, 0.74, 24650.00, 24860.20, 24630.15));
        marketIndices.add(new MarketIndex("SENSEX", 81450.70, 560.10, 0.69, 80920.00, 81580.40, 80890.30));
        marketIndices.add(new MarketIndex("NIFTY BANK", 52140.25, 410.80, 0.79, 51750.10, 52280.00, 51690.40));
        marketIndices.add(new MarketIndex("NIFTY IT", 41890.60, -125.45, -0.30, 42010.00, 42100.50, 41750.20));

        // High-liquidity Indian equities listed on Sim Trade
        stockCatalog.clear();
        addStock(new Stock("RELIANCE", "Reliance Industries Ltd.", 2985.50, 2942.00, 4280190, 2998.00, 2935.00, 3024.90, 2220.30));
        addStock(new Stock("TCS", "Tata Consultancy Services", 4250.75, 4210.00, 1850320, 4275.00, 4195.00, 4592.25, 3311.00));
        addStock(new Stock("HDFCBANK", "HDFC Bank Limited", 1668.20, 1645.00, 8945200, 1675.00, 1640.50, 1794.00, 1363.55));
        addStock(new Stock("INFY", "Infosys Limited", 1915.30, 1938.00, 3120400, 1942.00, 1905.00, 1991.45, 1358.35));
        addStock(new Stock("ICICIBANK", "ICICI Bank Ltd.", 1235.80, 1215.50, 6420100, 1242.00, 1212.00, 1257.80, 915.00));
        addStock(new Stock("TATAMOTORS", "Tata Motors Limited", 1025.40, 995.00, 7510800, 1032.00, 991.00, 1179.05, 600.65));
        addStock(new Stock("ITC", "ITC Limited", 508.65, 502.20, 5820300, 512.00, 501.00, 526.55, 399.30));
        addStock(new Stock("SBIN", "State Bank of India", 812.10, 798.50, 9102400, 818.00, 795.00, 912.00, 555.25));
        addStock(new Stock("BHARTIARTL", "Bharti Airtel Ltd.", 1585.00, 1560.00, 2450100, 1595.00, 1555.00, 1622.00, 892.00));
        addStock(new Stock("LT", "Larsen & Toubro Ltd.", 3640.20, 3680.00, 1340900, 3690.00, 3625.00, 3919.90, 2860.00));
        addStock(new Stock("BAJFINANCE", "Bajaj Finance Ltd.", 7350.00, 7220.00, 920400, 7390.00, 7210.00, 8192.00, 6160.00));
        addStock(new Stock("WIPRO", "Wipro Limited", 542.80, 551.20, 2810500, 554.00, 539.00, 564.00, 375.00));

        // Initial executed simulated trades stream
        exchangeTradeFeed.clear();
        exchangeTradeFeed.add(new TradeOrder("TRD-1001", "RELIANCE", "BUY", "MARKET", 50, 2984.00, "EXECUTED"));
        exchangeTradeFeed.add(new TradeOrder("TRD-1002", "TATAMOTORS", "BUY", "LIMIT", 100, 1024.50, "EXECUTED"));
        exchangeTradeFeed.add(new TradeOrder("TRD-1003", "HDFCBANK", "SELL", "MARKET", 40, 1667.50, "EXECUTED"));
        exchangeTradeFeed.add(new TradeOrder("TRD-1004", "TCS", "BUY", "MARKET", 15, 4250.00, "EXECUTED"));
    }

    private void addStock(Stock s) {
        stockCatalog.put(s.getSymbol(), s);
    }

    private Portfolio getOrCreateUserPortfolio(HttpSession session) {
        Portfolio portfolio = (Portfolio) session.getAttribute("userPortfolio");
        if (portfolio == null) {
            portfolio = new Portfolio(101, new ArrayList<>());
            portfolio.setCashBalance(500000.00); // 5 Lakhs INR initial simulated capital

            // Seed initial realistic holdings
            Stock rel = stockCatalog.get("RELIANCE");
            Stock tcs = stockCatalog.get("TCS");
            Stock hdfc = stockCatalog.get("HDFCBANK");
            Stock infy = stockCatalog.get("INFY");

            if (rel != null) portfolio.addOrUpdateHolding("RELIANCE", rel.getName(), 25, 2850.00);
            if (tcs != null) portfolio.addOrUpdateHolding("TCS", tcs.getName(), 15, 4120.00);
            if (hdfc != null) portfolio.addOrUpdateHolding("HDFCBANK", hdfc.getName(), 50, 1610.00);
            if (infy != null) portfolio.addOrUpdateHolding("INFY", infy.getName(), 30, 1880.00);

            session.setAttribute("userPortfolio", portfolio);
        }

        // Keep current prices in sync with latest catalog prices
        for (Holding h : portfolio.getHoldings()) {
            Stock s = stockCatalog.get(h.getStockSymbol());
            if (s != null) {
                h.setCurrentPrice(s.getPriceAsDouble());
            }
        }

        return portfolio;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        Portfolio portfolio = getOrCreateUserPortfolio(session);

        // Prepare categorized stock lists
        List<Stock> allStocks = new ArrayList<>(stockCatalog.values());
        
        // Sort for Top Gainers
        List<Stock> topGainers = new ArrayList<>(allStocks);
        topGainers.sort(Comparator.comparingDouble(Stock::getChangePercentAsDouble).reversed());

        // Sort for Top Losers
        List<Stock> topLosers = new ArrayList<>(allStocks);
        topLosers.sort(Comparator.comparingDouble(Stock::getChangePercentAsDouble));

        // Sort for Most Active by Volume
        List<Stock> mostActive = new ArrayList<>(allStocks);
        mostActive.sort(Comparator.comparingLong(Stock::getVolume).reversed());

        // Compute Market Breadth (Advances vs Declines)
        int advances = 0;
        int declines = 0;
        int unchanged = 0;
        for (Stock s : allStocks) {
            if (s.getChange() > 0) advances++;
            else if (s.getChange() < 0) declines++;
            else unchanged++;
        }

        // Set request attributes for JSP rendering
        request.setAttribute("marketIndices", marketIndices);
        request.setAttribute("allStocks", allStocks);
        request.setAttribute("topGainers", topGainers.subList(0, Math.min(5, topGainers.size())));
        request.setAttribute("topLosers", topLosers.subList(0, Math.min(5, topLosers.size())));
        request.setAttribute("mostActive", mostActive.subList(0, Math.min(5, mostActive.size())));
        request.setAttribute("advances", advances);
        request.setAttribute("declines", declines);
        request.setAttribute("unchanged", unchanged);
        request.setAttribute("portfolio", portfolio);
        request.setAttribute("exchangeTradeFeed", exchangeTradeFeed);

        // Forward to the NSE-styled JSP dashboard view
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        Portfolio portfolio = getOrCreateUserPortfolio(session);

        String action = request.getParameter("action");
        String symbol = request.getParameter("symbol");
        String side = request.getParameter("side"); // BUY or SELL
        String orderType = request.getParameter("orderType"); // MARKET or LIMIT
        String quantityStr = request.getParameter("quantity");
        String priceStr = request.getParameter("price");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "json".equalsIgnoreCase(request.getParameter("format"));

        String statusMessage = "";
        boolean isSuccess = false;

        try {
            if (symbol == null || symbol.trim().isEmpty() || !stockCatalog.containsKey(symbol.toUpperCase())) {
                throw new IllegalArgumentException("Invalid or unknown stock symbol: " + symbol);
            }

            Stock stock = stockCatalog.get(symbol.toUpperCase());
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be a positive integer.");
            }

            double executionPrice = stock.getPriceAsDouble();
            if ("LIMIT".equalsIgnoreCase(orderType) && priceStr != null && !priceStr.trim().isEmpty()) {
                executionPrice = Double.parseDouble(priceStr);
                if (executionPrice <= 0) {
                    throw new IllegalArgumentException("Limit price must be greater than zero.");
                }
            }

            double totalOrderCost = Math.round((quantity * executionPrice) * 100.0) / 100.0;

            if ("BUY".equalsIgnoreCase(side)) {
                // Check cash balance
                if (portfolio.getCashBalance() < totalOrderCost) {
                    throw new IllegalStateException("Insufficient funds! Required: ₹" + totalOrderCost + 
                            ", Available: ₹" + portfolio.getCashBalance());
                }

                // Debit cash and credit holding atomically
                synchronized (portfolio) {
                    portfolio.debit(totalOrderCost);
                    portfolio.addOrUpdateHolding(stock.getSymbol(), stock.getName(), quantity, executionPrice);
                }

                String orderId = "ORD-" + System.currentTimeMillis() % 100000;
                TradeOrder order = new TradeOrder(orderId, stock.getSymbol(), "BUY", orderType, quantity, executionPrice, "EXECUTED");
                exchangeTradeFeed.add(0, order);

                statusMessage = "Order executed successfully! Bought " + quantity + " shares of " + stock.getSymbol() + " at ₹" + executionPrice;
                isSuccess = true;

            } else if ("SELL".equalsIgnoreCase(side)) {
                // Check holdings
                Holding holding = portfolio.getHolding(stock.getSymbol());
                if (holding == null || holding.getQuantity() < quantity) {
                    int availableQty = holding != null ? holding.getQuantity() : 0;
                    throw new IllegalStateException("Insufficient shares! Available: " + availableQty + ", Requested to sell: " + quantity);
                }

                // Credit cash and debit holding atomically
                synchronized (portfolio) {
                    portfolio.credit(totalOrderCost);
                    portfolio.removeOrReduceHolding(stock.getSymbol(), quantity);
                }

                String orderId = "ORD-" + System.currentTimeMillis() % 100000;
                TradeOrder order = new TradeOrder(orderId, stock.getSymbol(), "SELL", orderType, quantity, executionPrice, "EXECUTED");
                exchangeTradeFeed.add(0, order);

                statusMessage = "Order executed successfully! Sold " + quantity + " shares of " + stock.getSymbol() + " at ₹" + executionPrice;
                isSuccess = true;

            } else {
                throw new IllegalArgumentException("Invalid order side. Must be BUY or SELL.");
            }

        } catch (Exception e) {
            statusMessage = e.getMessage();
            isSuccess = false;
        }

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\":" + isSuccess + 
                     ",\"message\":\"" + statusMessage.replace("\"", "\\\"") + "\"" +
                     ",\"cashBalance\":" + portfolio.getCashBalance() + 
                     ",\"totalValue\":" + portfolio.getTotalHoldingsValue() + 
                     ",\"netWorth\":" + portfolio.getTotalNetWorth() + 
                     "}");
            out.flush();
        } else {
            if (isSuccess) {
                session.setAttribute("orderSuccess", statusMessage);
            } else {
                session.setAttribute("orderError", statusMessage);
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
