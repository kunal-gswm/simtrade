<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="max-width: 1200px; margin: 20px auto;">
    <h2 style="margin-bottom: 20px;">Trade History</h2>
    <%
        java.util.List<com.project.trading.model.TradeOrder> trades =
            (java.util.List<com.project.trading.model.TradeOrder>) request.getAttribute("trades");
        if (trades == null || trades.isEmpty()) {
    %>
        <p style="color: var(--tv-text-secondary);">No trades have been executed yet. <a href="<%= request.getContextPath() %>/dashboard">Go trade &rarr;</a></p>
    <%  } else { %>
    <table>
        <thead>
            <tr>
                <th>Time</th>
                <th>Order ID</th>
                <th>Type</th>
                <th>Symbol</th>
                <th>Order Type</th>
                <th>Qty</th>
                <th>Price (&#8377;)</th>
                <th>Total (&#8377;)</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
        <% for (com.project.trading.model.TradeOrder t : trades) { %>
            <tr>
                <td><%= t.getTimestamp() %></td>
                <td style="font-size:0.75rem; color:var(--tv-text-secondary);"><%= t.getOrderId() %></td>
                <td class="<%= "BUY".equals(t.getSide()) ? "bull-text" : "bear-text" %>">
                    <span class="badge" style="color:<%= "BUY".equals(t.getSide()) ? "var(--tv-blue)" : "var(--tv-bear)" %>; border-color:currentColor;">
                        <%= t.getSide() %>
                    </span>
                </td>
                <td><strong><%= t.getSymbol() %></strong></td>
                <td style="color:var(--tv-text-secondary);"><%= t.getOrderType() %></td>
                <td><%= t.getQuantity() %></td>
                <td><%= String.format("%,.2f", t.getPrice()) %></td>
                <td><%= String.format("%,.2f", t.getTotalAmount()) %></td>
                <td style="color:var(--tv-bull-light);"><%= t.getStatus() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
