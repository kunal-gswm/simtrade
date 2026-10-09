<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="margin: 20px auto;">
    <h2>Dashboard</h2>
    
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px;">
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Cash Balance</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);">₹<fmt:formatNumber value="${summary.cash}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></div>
        </div>
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Net Worth</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);">₹<fmt:formatNumber value="${summary.netWorth}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></div>
        </div>
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Overall P&L</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);" class="${summary.overallPnl >= 0 ? 'bull-text' : 'bear-text'}">
                <c:if test="${summary.overallPnl > 0}">+</c:if>₹<fmt:formatNumber value="${summary.overallPnl}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
            </div>
        </div>
    </div>

    <h3>Recent Trades</h3>
    <c:choose>
        <c:when test="${empty recentTrades}">
            <p style="color: var(--tv-text-secondary);">No recent trades. <a href="${pageContext.request.contextPath}/app/market">Go to market</a>.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Type</th>
                        <th>Symbol</th>
                        <th>Qty</th>
                        <th>Price</th>
                        <th>Total</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="trade" items="${recentTrades}">
                        <tr>
                            <td><c:out value="${trade.executedAt}"/></td>
                            <td class="${trade.type == 'BUY' ? 'bull-text' : 'bear-text'}">
                                <span class="badge ${trade.type == 'BUY' ? 'badge-blue' : ''}" style="${trade.type == 'SELL' ? 'color: var(--tv-bear); border-color: var(--tv-bear);' : ''}">
                                    <c:out value="${trade.type}"/>
                                </span>
                            </td>
                            <td><a href="${pageContext.request.contextPath}/app/stock?id=${trade.stockId}"><c:out value="${trade.stockSymbol}"/></a></td>
                            <td><c:out value="${trade.quantity}"/></td>
                            <td>₹<fmt:formatNumber value="${trade.price}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td>₹<fmt:formatNumber value="${trade.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div style="margin-top: 16px;">
                <a href="${pageContext.request.contextPath}/app/history">View all history →</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
