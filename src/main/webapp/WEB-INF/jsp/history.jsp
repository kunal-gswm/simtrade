<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="max-width: 1200px; margin: 20px auto;">
    <h2 style="margin-bottom: 20px;">Trade History</h2>
    <%
        java.util.List<com.project.trading.model.Trade> trades =
            (java.util.List<com.project.trading.model.Trade>) request.getAttribute("trades");
        if (trades == null || trades.isEmpty()) {
    %>
        <p style="color: var(--tv-text-secondary);">No trades have been executed yet. <a href="<%= request.getContextPath() %>/app/market">Go trade &rarr;</a></p>
    <%  } else { %>
    <table>
        <thead>
            <tr>
                <th>Time</th>
                <th>Trade ID</th>
                <th>Type</th>
                <th>Symbol</th>
                <th>Qty</th>
                <th>Price (&#8377;)</th>
                <th>Total (&#8377;)</th>
                <th>Realized P&amp;L (&#8377;)</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
        <% for (com.project.trading.model.Trade t : trades) {
            boolean isBuy = t.getType() == com.project.trading.model.TradeType.BUY;
            String sideClass = isBuy ? "bull-text" : "bear-text";
        %>
            <tr>
                <td><%= t.getExecutedAt() != null ? t.getExecutedAt().toString() : "-" %></td>
                <td style="font-size:0.75rem; color:var(--tv-text-secondary);">#<%= t.getId() %></td>
                <td class="<%= sideClass %>">
                    <span class="badge" style="color:<%= isBuy ? "var(--tv-blue)" : "var(--tv-bear)" %>; border-color:currentColor;">
                        <%= t.getType() %>
                    </span>
                </td>
                <td><strong><%= t.getStockSymbol() %></strong></td>
                <td><%= t.getQuantity() %></td>
                <td><%= String.format("%,.2f", t.getPrice()) %></td>
                <td><%= String.format("%,.2f", t.getTotalAmount()) %></td>
                <td>
                    <% if (!isBuy && t.getRealizedPnl() != null) {
                        boolean pnlPos = t.getRealizedPnl().compareTo(java.math.BigDecimal.ZERO) >= 0;
                    %>
                        <span class="<%= pnlPos ? "bull-text" : "bear-text" %>">
                            <%= pnlPos ? "+" : "" %><%= String.format("%,.2f", t.getRealizedPnl()) %>
                        </span>
                    <% } else { %>
                        <span style="color: var(--tv-text-secondary);">-</span>
                    <% } %>
                </td>
                <td style="color:var(--tv-bull-light);">EXECUTED</td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
