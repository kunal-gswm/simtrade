<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="max-width: 1200px; margin: 20px auto;">
    <h2 style="margin-bottom: 20px;">Dashboard</h2>

    <%-- Summary Cards --%>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 28px;">

        <div class="stat-card">
            <div class="stat-label">Cash Balance</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", ((com.project.trading.service.PortfolioService.PortfolioSummary) request.getAttribute("summary")).cash) %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Net Worth</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", ((com.project.trading.service.PortfolioService.PortfolioSummary) request.getAttribute("summary")).netWorth) %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Holdings Value</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", ((com.project.trading.service.PortfolioService.PortfolioSummary) request.getAttribute("summary")).totalCurrentValue) %></div>
        </div>
        <%
            com.project.trading.service.PortfolioService.PortfolioSummary s =
                (com.project.trading.service.PortfolioService.PortfolioSummary) request.getAttribute("summary");
            String pnlClass = s.overallPnl >= 0 ? "bull-text" : "bear-text";
            String pnlSign  = s.overallPnl >= 0 ? "+" : "";
        %>
        <div class="stat-card">
            <div class="stat-label">Overall P&amp;L</div>
            <div class="stat-value <%= pnlClass %>"><%= pnlSign %>&#8377;<%= String.format("%,.2f", s.overallPnl) %></div>
        </div>
    </div>

    <%-- Recent Trades --%>
    <h3 style="margin-bottom: 12px;">Recent Trades</h3>
    <%
        java.util.List<com.project.trading.model.TradeOrder> trades =
            (java.util.List<com.project.trading.model.TradeOrder>) request.getAttribute("recentTrades");
        if (trades == null || trades.isEmpty()) {
    %>
        <p style="color: var(--tv-text-secondary);">No trades yet. <a href="<%= request.getContextPath() %>/dashboard">Go trade</a>.</p>
    <%
        } else {
            int limit = Math.min(5, trades.size());
    %>
    <table>
        <thead>
            <tr><th>Time</th><th>Type</th><th>Symbol</th><th>Qty</th><th>Price (&#8377;)</th><th>Total (&#8377;)</th></tr>
        </thead>
        <tbody>
        <%  for (int i = 0; i < limit; i++) {
                com.project.trading.model.TradeOrder t = trades.get(i);
                String rowClass = "BUY".equals(t.getSide()) ? "bull-text" : "bear-text";
        %>
            <tr>
                <td><%= t.getTimestamp() %></td>
                <td class="<%= rowClass %>"><%= t.getSide() %></td>
                <td><strong><%= t.getSymbol() %></strong></td>
                <td><%= t.getQuantity() %></td>
                <td><%= String.format("%,.2f", t.getPrice()) %></td>
                <td><%= String.format("%,.2f", t.getTotalAmount()) %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <div style="margin-top: 14px;"><a href="<%= request.getContextPath() %>/app/history">View full history &rarr;</a></div>
    <% } %>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
