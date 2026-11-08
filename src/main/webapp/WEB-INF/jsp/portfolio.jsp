<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<%
    com.project.trading.service.PortfolioService.PortfolioSummary s =
        (com.project.trading.service.PortfolioService.PortfolioSummary) request.getAttribute("summary");
%>
<div class="card" style="max-width: 1400px; margin: 20px auto;">
    <h2 style="margin-bottom: 20px;">My Portfolio</h2>

    <%-- Summary Cards --%>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 28px;">
        <div class="stat-card">
            <div class="stat-label">Total Invested</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", s.totalInvested) %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Current Value</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", s.totalCurrentValue) %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Unrealized P&amp;L</div>
            <div class="stat-value <%= s.isPositive() ? "bull-text" : "bear-text" %>">
                <%= s.isPositive() ? "+" : "" %>&#8377;<%= String.format("%,.2f", s.totalUnrealizedPnl) %>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Realized P&amp;L</div>
            <div class="stat-value <%= s.realizedPnl.compareTo(java.math.BigDecimal.ZERO) >= 0 ? "bull-text" : "bear-text" %>">
                <%= s.realizedPnl.compareTo(java.math.BigDecimal.ZERO) >= 0 ? "+" : "" %>&#8377;<%= String.format("%,.2f", s.realizedPnl) %>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Total Net Worth</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", s.netWorth) %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Cash Available</div>
            <div class="stat-value">&#8377;<%= String.format("%,.2f", s.cash) %></div>
        </div>
    </div>

    <%-- Holdings Table --%>
    <h3 style="margin-bottom: 12px;">Holdings</h3>
    <% if (s.rows == null || s.rows.isEmpty()) { %>
        <p style="color: var(--tv-text-secondary);">Your portfolio is empty. <a href="<%= request.getContextPath() %>/app/market">Go to market &rarr;</a></p>
    <% } else { %>
    <table>
        <thead>
            <tr>
                <th>Symbol</th>
                <th>Qty</th>
                <th>Avg Price (&#8377;)</th>
                <th>LTP (&#8377;)</th>
                <th>Invested (&#8377;)</th>
                <th>Cur. Value (&#8377;)</th>
                <th>P&amp;L</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% for (com.project.trading.service.PortfolioService.HoldingRow row : s.rows) { %>
            <tr>
                <td>
                    <strong><%= row.symbol %></strong>
                    <div style="font-size:0.75rem; color:var(--tv-text-secondary);"><%= row.companyName %></div>
                </td>
                <td><%= row.quantity %></td>
                <td><%= String.format("%,.2f", row.avgBuyPrice) %></td>
                <td><%= String.format("%,.2f", row.currentPrice) %></td>
                <td><%= String.format("%,.2f", row.invested) %></td>
                <td><%= String.format("%,.2f", row.currentValue) %></td>
                <td class="<%= row.isPositive() ? "bull-text" : "bear-text" %>">
                    <%= row.isPositive() ? "+" : "" %><%= String.format("%,.2f", row.unrealizedPnl) %><br>
                    <span style="font-size:0.8rem;">(<%= row.isPositive() ? "+" : "" %><%= String.format("%.2f", row.unrealizedPct) %>%)</span>
                </td>
                <td>
                    <a href="<%= request.getContextPath() %>/app/stock?id=<%= row.stockId %>" class="tv-icon-btn" style="padding: 4px 10px; font-size: 0.8rem; background: var(--tv-blue); color: white;">Trade</a>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
