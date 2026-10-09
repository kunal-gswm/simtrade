<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="margin: 20px auto; max-width: 1400px;">
    <h2>Portfolio</h2>
    
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px;">
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Total Invested</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);">₹<fmt:formatNumber value="${summary.totalInvested}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></div>
        </div>
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Current Value</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);">₹<fmt:formatNumber value="${summary.totalCurrentValue}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></div>
        </div>
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Unrealized P&L</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);" class="${summary.totalUnrealizedPnl >= 0 ? 'bull-text' : 'bear-text'}">
                <c:if test="${summary.totalUnrealizedPnl > 0}">+</c:if>₹<fmt:formatNumber value="${summary.totalUnrealizedPnl}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
            </div>
        </div>
        <div style="background: var(--tv-bg-card); padding: 16px; border-radius: 8px; border: 1px solid var(--tv-border);">
            <div style="color: var(--tv-text-secondary); font-size: 0.85rem; margin-bottom: 8px;">Realized P&L</div>
            <div style="font-size: 1.5rem; font-weight: bold; font-family: var(--tv-font-mono);" class="${summary.realizedPnl >= 0 ? 'bull-text' : 'bear-text'}">
                <c:if test="${summary.realizedPnl > 0}">+</c:if>₹<fmt:formatNumber value="${summary.realizedPnl}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
            </div>
        </div>
    </div>

    <h3>Holdings</h3>
    <c:choose>
        <c:when test="${empty summary.rows}">
            <p style="color: var(--tv-text-secondary);">empty portfolio message <a href="${pageContext.request.contextPath}/app/market">Go to market</a>.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Symbol</th>
                        <th>Qty</th>
                        <th>Avg Price</th>
                        <th>LTP</th>
                        <th>Invested</th>
                        <th>Current Value</th>
                        <th>P&L</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${summary.rows}">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/app/stock?id=${row.stockId}" style="font-weight: 600;"><c:out value="${row.symbol}"/></a>
                                <div style="font-size: 0.75rem; color: var(--tv-text-secondary);"><c:out value="${row.companyName}"/></div>
                            </td>
                            <td><c:out value="${row.quantity}"/></td>
                            <td>₹<fmt:formatNumber value="${row.avgBuyPrice}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td>₹<fmt:formatNumber value="${row.currentPrice}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td>₹<fmt:formatNumber value="${row.invested}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td>₹<fmt:formatNumber value="${row.currentValue}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td class="${row.unrealizedPnl >= 0 ? 'bull-text' : 'bear-text'}">
                                <c:if test="${row.unrealizedPnl > 0}">+</c:if>₹<fmt:formatNumber value="${row.unrealizedPnl}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
                                <br>
                                <span style="font-size: 0.8rem;">(<c:if test="${row.unrealizedPct > 0}">+</c:if><fmt:formatNumber value="${row.unrealizedPct}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%)</span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/app/stock?id=${row.stockId}" class="tv-trade-btn" style="padding: 4px 12px; font-size: 0.75rem; background: var(--tv-bear);">Sell</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
