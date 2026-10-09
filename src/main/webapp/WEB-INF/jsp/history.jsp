<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="margin: 20px auto; max-width: 1200px;">
    <h2>Trade History</h2>
    
    <c:choose>
        <c:when test="${empty trades}">
            <p style="color: var(--tv-text-secondary);">empty message</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Date & Time</th>
                        <th>Type</th>
                        <th>Symbol</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Total Amount</th>
                        <th>Realized P&L</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="trade" items="${trades}">
                        <tr>
                            <td><c:out value="${trade.executedAt}"/></td>
                            <td class="${trade.type == 'BUY' ? 'bull-text' : 'bear-text'}">
                                <span class="badge ${trade.type == 'BUY' ? 'badge-blue' : ''}" style="${trade.type == 'SELL' ? 'color: var(--tv-bear); border-color: var(--tv-bear);' : ''}">
                                    <c:out value="${trade.type}"/>
                                </span>
                            </td>
                            <td><a href="${pageContext.request.contextPath}/app/stock?id=${trade.stockId}" style="font-weight:600;"><c:out value="${trade.stockSymbol}"/></a></td>
                            <td><c:out value="${trade.quantity}"/></td>
                            <td>₹<fmt:formatNumber value="${trade.price}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td>₹<fmt:formatNumber value="${trade.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${trade.type == 'SELL'}">
                                        <span class="${trade.realizedPnl >= 0 ? 'bull-text' : 'bear-text'}">
                                            <c:if test="${trade.realizedPnl > 0}">+</c:if>₹<fmt:formatNumber value="${trade.realizedPnl}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: var(--tv-text-secondary);">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
