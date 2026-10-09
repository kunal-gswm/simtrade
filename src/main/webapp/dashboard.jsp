<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.project.trading.model.*" %>
<%
    // Resilient initialization ensuring page functions directly or through Servlet
    List<MarketIndex> marketIndices = (List<MarketIndex>) request.getAttribute("marketIndices");
    List<Stock> allStocks = (List<Stock>) request.getAttribute("allStocks");

    if (allStocks == null || marketIndices == null) {
        marketIndices = Arrays.asList(
            new MarketIndex("NIFTY 50", 24825.40, 182.35, 0.74, 24650.00, 24860.20, 24630.15),
            new MarketIndex("SENSEX", 81450.70, 560.10, 0.69, 80920.00, 81580.40, 80890.30)
        );

        allStocks = Arrays.asList(
            new Stock("RELIANCE", "Reliance Industries Ltd.", 2985.50, 2942.00, 4280190, 2998.00, 2935.00, 3024.90, 2220.30),
            new Stock("TCS", "Tata Consultancy Services", 4250.75, 4210.00, 1850320, 4275.00, 4195.00, 4592.25, 3311.00),
            new Stock("HDFCBANK", "HDFC Bank Limited", 1668.20, 1645.00, 8945200, 1675.00, 1640.50, 1794.00, 1363.55),
            new Stock("INFY", "Infosys Limited", 1915.30, 1938.00, 3120400, 1942.00, 1905.00, 1991.45, 1358.35),
            new Stock("ICICIBANK", "ICICI Bank Ltd.", 1235.80, 1215.50, 6420100, 1242.00, 1212.00, 1257.80, 915.00),
            new Stock("TATAMOTORS", "Tata Motors Limited", 1025.40, 995.00, 7510800, 1032.00, 991.00, 1179.05, 600.65),
            new Stock("ITC", "ITC Limited", 508.65, 502.20, 5820300, 512.00, 501.00, 526.55, 399.30),
            new Stock("SBIN", "State Bank of India", 812.10, 798.50, 9102400, 818.00, 795.00, 912.00, 555.25)
        );
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sim Trade — Supercharts</title>
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&family=JetBrains+Mono:wght@500;600;700;800&display=swap" rel="stylesheet">
    
    <!-- TradingView UI Styles -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/simtrade-nse.css">
</head>
<body>

    <!-- 1. Top Navbar -->
    <nav class="tv-top-nav">
        <div class="tv-nav-left">
            <a href="#" class="tv-brand">
                <div class="tv-logo-icon">S</div>
                SimTrade
            </a>
            <div class="tv-symbol-search" id="topSymbolSearch">
                <span style="font-size: 1rem; color: var(--tv-text-heading);">🔍</span>
                <strong id="navActiveSymbol"><%= allStocks.get(0).getSymbol() %></strong>
                <span>⊕</span>
            </div>
            <div class="nav-divider"></div>
            <div style="display: flex; gap: 2px;">
                <button class="tv-icon-btn">1M</button>
                <button class="tv-icon-btn">5M</button>
                <button class="tv-icon-btn">15M</button>
                <button class="tv-icon-btn">1H</button>
                <button class="tv-icon-btn active">D</button>
            </div>
            <div class="nav-divider"></div>
            <button class="tv-icon-btn active" title="Candles">🕯️</button>
            <button class="tv-icon-btn" title="Indicators">fx Indicators</button>
            <button class="tv-icon-btn" title="Indicator Templates">⊞</button>
            <div class="nav-divider"></div>
            <button class="tv-icon-btn" title="Alert">⏰ Alert</button>
            <button class="tv-icon-btn" title="Replay">⏪ Replay</button>
        </div>
        
        <div class="tv-nav-right">
            <button class="tv-icon-btn" title="Undo">↩</button>
            <button class="tv-icon-btn" title="Redo">↪</button>
            <div class="nav-divider"></div>
            <button class="tv-icon-btn" title="Select Layout">◧</button>
            <button class="tv-icon-btn" title="Quick Search">🔍</button>
            <button class="tv-icon-btn" title="Settings">⚙</button>
            <button class="tv-icon-btn" title="Fullscreen">⛶</button>
            <button class="tv-icon-btn" title="Take a snapshot">📷</button>
            <div class="nav-divider"></div>
            <button class="tv-trade-btn">Trade</button>
        </div>
    </nav>

    <!-- 2. Main Flex Container -->
    <div class="tv-main-container">
        
        <!-- Left Drawing Toolbar -->
        <aside class="tv-left-toolbar">
            <button class="tv-tool-icon active" title="Crosshair">✛</button>
            <button class="tv-tool-icon" title="Trend Line">╱</button>
            <button class="tv-tool-icon" title="Gann and Fibonacci">≡</button>
            <button class="tv-tool-icon" title="Geometric Shapes">🖌</button>
            <button class="tv-tool-icon" title="Annotation Tools">T</button>
            <button class="tv-tool-icon" title="Patterns">∿</button>
            <button class="tv-tool-icon" title="Prediction and Measurement">📏</button>
            <button class="tv-tool-icon" title="Zoom In">⊕</button>
            <div style="flex:1;"></div>
            <button class="tv-tool-icon" title="Magnet Mode">🧲</button>
            <button class="tv-tool-icon" title="Stay in Drawing Mode">🔒</button>
            <button class="tv-tool-icon" title="Hide All Drawings">👁</button>
            <button class="tv-tool-icon" title="Remove Drawings">🗑</button>
        </aside>

        <!-- Center Chart Area -->
        <section class="tv-chart-container">
            <div class="chart-watermark">Sim Trade</div>
            
            <div class="chart-legend">
                <div class="chart-legend-title">
                    <span id="chartLegendSymbol" class="ticker"><%= allStocks.get(0).getSymbol() %></span>
                    <span class="exchange">NSE</span>
                    <span class="market-status">● Market open</span>
                </div>
                <div class="chart-legend-ohlc <%= allStocks.get(0).isPositive() ? "bull" : "bear" %>" id="chartLegendOhlc">
                    <div>O <span id="legO"><%= String.format("%.2f", allStocks.get(0).getOpen()) %></span></div>
                    <div>H <span id="legH"><%= String.format("%.2f", allStocks.get(0).getHigh()) %></span></div>
                    <div>L <span id="legL"><%= String.format("%.2f", allStocks.get(0).getLow()) %></span></div>
                    <div>C <span id="legC"><%= String.format("%.2f", allStocks.get(0).getPrice()) %></span></div>
                    <div>
                        <span id="legChg"><%= allStocks.get(0).isPositive() ? "+" : "" %><%= String.format("%.2f", allStocks.get(0).getChange()) %></span>
                        (<span id="legChgP"><%= allStocks.get(0).isPositive() ? "+" : "" %><%= String.format("%.2f", allStocks.get(0).getChangePercent()) %>%</span>)
                    </div>
                </div>
            </div>

            <div class="chart-canvas-wrapper" id="chartWrapper">
                <canvas id="tradingViewCanvas"></canvas>
            </div>

            <div class="chart-bottom-axis">
                <div class="axis-timeframes">
                    <button class="axis-tf-btn">1D</button>
                    <button class="axis-tf-btn">5D</button>
                    <button class="axis-tf-btn">1M</button>
                    <button class="axis-tf-btn">3M</button>
                    <button class="axis-tf-btn">6M</button>
                    <button class="axis-tf-btn">YTD</button>
                    <button class="axis-tf-btn active">1Y</button>
                    <button class="axis-tf-btn">5Y</button>
                    <button class="axis-tf-btn">ALL</button>
                </div>
                <div style="font-size: 0.75rem; color: var(--tv-text-secondary); display: flex; gap: 8px;">
                    <span id="currentTimeDisplay">--:--:--</span>
                    <span>UTC+5:30</span>
                </div>
            </div>
        </section>

        <!-- Right Panel (Watchlist & Details) -->
        <aside class="tv-right-panel">
            <div class="right-panel-header">
                <div class="right-panel-title">Watchlist</div>
                <div class="right-panel-actions">
                    <span style="cursor:pointer;" title="Add Symbol">➕</span>
                    <span style="cursor:pointer;" title="Advanced View">⊞</span>
                    <span style="cursor:pointer;" title="More">⋮</span>
                </div>
            </div>
            
            <div class="watchlist-header">
                <span>Symbol</span>
                <span>Last</span>
                <span>Chg</span>
                <span>Chg%</span>
            </div>
            
            <div class="watchlist-container" id="watchlistContainer">
                <% for (MarketIndex idx : marketIndices) { %>
                    <div class="watchlist-item">
                        <div class="wl-symbol">
                            <span class="badge badge-blue">ID</span>
                            <%= idx.getName().substring(0, Math.min(5, idx.getName().length())) %>
                        </div>
                        <div class="wl-last <%= idx.isPositive() ? "bull-text" : "bear-text" %>">
                            <%= String.format("%.2f", idx.getValue()) %>
                        </div>
                        <div class="wl-chg <%= idx.isPositive() ? "bull-text" : "bear-text" %>">
                            <%= String.format("%.2f", idx.getChange()) %>
                        </div>
                        <div class="wl-chgp <%= idx.isPositive() ? "bull-text" : "bear-text" %>">
                            <%= String.format("%.2f", idx.getChangePercent()) %>%
                        </div>
                    </div>
                <% } %>
                
                <div style="padding: 8px 16px; font-size: 0.65rem; color: var(--tv-text-secondary); font-weight: 700; background: var(--tv-bg-card);">
                    ▼ STOCKS
                </div>
                
                <% for (int i = 0; i < allStocks.size(); i++) { 
                    Stock s = allStocks.get(i);
                %>
                    <div class="watchlist-item <%= i == 0 ? "active" : "" %> stock-row" 
                         data-symbol="<%= s.getSymbol() %>"
                         data-name="<%= s.getName() %>"
                         data-price="<%= s.getPrice() %>"
                         data-change="<%= s.getChange() %>"
                         data-changep="<%= s.getChangePercent() %>"
                         data-high="<%= s.getHigh() %>"
                         data-low="<%= s.getLow() %>"
                         data-open="<%= s.getOpen() %>">
                        <div class="wl-symbol">
                            <span class="badge" style="color:var(--tv-gold)">EQ</span>
                            <%= s.getSymbol() %>
                        </div>
                        <div class="wl-last <%= s.isPositive() ? "bull-text" : "bear-text" %>">
                            <%= String.format("%.2f", s.getPrice()) %>
                        </div>
                        <div class="wl-chg <%= s.isPositive() ? "bull-text" : "bear-text" %>">
                            <%= String.format("%.2f", s.getChange()) %>
                        </div>
                        <div class="wl-chgp <%= s.isPositive() ? "bull-text" : "bear-text" %>">
                            <%= String.format("%.2f", s.getChangePercent()) %>%
                        </div>
                    </div>
                <% } %>
            </div>

            <div class="details-widget">
                <div class="details-header">
                    <div class="details-symbol" id="detSymbol"><%= allStocks.get(0).getSymbol() %></div>
                    <div class="details-name" id="detName"><%= allStocks.get(0).getName() %> • NSE</div>
                </div>
                <div class="details-price-row">
                    <div class="details-price <%= allStocks.get(0).isPositive() ? "bull-text" : "bear-text" %>" id="detPrice">
                        <%= String.format("%.2f", allStocks.get(0).getPrice()) %>
                    </div>
                    <div class="details-change <%= allStocks.get(0).isPositive() ? "bull-text" : "bear-text" %>" id="detChange">
                        <%= allStocks.get(0).isPositive() ? "+" : "" %><%= String.format("%.2f", allStocks.get(0).getChange()) %> 
                        (<%= allStocks.get(0).isPositive() ? "+" : "" %><%= String.format("%.2f", allStocks.get(0).getChangePercent()) %>%)
                    </div>
                </div>
                <div class="details-stats">
                    <div style="font-weight: 600; color: var(--tv-text-heading); margin-bottom: 4px;">Performance</div>
                    <div class="stat-row">
                        <span>Day's Range</span>
                        <span><span id="detLow"><%= String.format("%.2f", allStocks.get(0).getLow()) %></span> - <span id="detHigh"><%= String.format("%.2f", allStocks.get(0).getHigh()) %></span></span>
                    </div>
                    <div class="stat-row">
                        <span>52W Range</span>
                        <span><%= String.format("%.2f", allStocks.get(0).getWeek52Low()) %> - <%= String.format("%.2f", allStocks.get(0).getWeek52High()) %></span>
                    </div>
                    <div class="stat-row">
                        <span>Volume</span>
                        <span><%= String.format("%,d", allStocks.get(0).getVolume()) %></span>
                    </div>
                    <div style="margin-top: 10px; padding: 8px; background: var(--tv-blue-soft); border-radius: 4px; color: var(--tv-text-primary); font-size: 0.75rem;">
                        <span style="color: var(--tv-blue); font-weight: 600;">News</span> • 1 hour ago<br>
                        Top brokerages upgrade <span id="detNewsSymbol"><%= allStocks.get(0).getSymbol() %></span> target price after robust quarterly performance.
                    </div>
                </div>
            </div>
        </aside>

        <!-- Rightmost Nav Strip -->
        <nav class="tv-rightmost-nav">
            <button class="tv-tool-icon active" title="Watchlist and details">📄</button>
            <button class="tv-tool-icon" title="Alerts">⏰</button>
            <button class="tv-tool-icon" title="Data Window">🗄</button>
            <button class="tv-tool-icon" title="Hotlists">🔥</button>
            <button class="tv-tool-icon" title="Calendar">📅</button>
            <div style="flex:1;"></div>
            <button class="tv-tool-icon" title="Object Tree">🌳</button>
            <button class="tv-tool-icon" title="Help">❓</button>
        </nav>
    </div>

    <script src="<%= request.getContextPath() %>/js/simtrade-nse.js"></script>
</body>
</html>
