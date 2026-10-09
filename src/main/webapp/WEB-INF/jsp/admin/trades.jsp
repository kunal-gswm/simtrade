<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Global Trade Ledger - SimTrade Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <main class="container">
        <div class="page-header">
            <div>
                <h1 class="page-title">Global Trade Monitor</h1>
                <p class="page-subtitle">Audited transaction ledger of the latest 200 executed trades across all users</p>
            </div>
        </div>

        <div class="card">
            <h2 class="card-title">Recent Trades Ledger</h2>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Trade ID</th>
                            <th>Time</th>
                            <th>User</th>
                            <th>Symbol</th>
                            <th>Type</th>
                            <th>Quantity</th>
                            <th>Execution Price</th>
                            <th>Total Amount</th>
                            <th>Realized P&amp;L</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty trades}">
                                <c:forEach var="t" items="${trades}">
                                    <tr>
                                        <td>#<c:out value="${t.id}" /></td>
                                        <td><fmt:formatDate value="${t.executedAt}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
                                        <td><strong><c:out value="${t.username}" /></strong></td>
                                        <td><span style="font-weight: 700; color: #93c5fd;"><c:out value="${t.stockSymbol}" /></span></td>
                                        <td>
                                            <span class="badge ${t.type == 'BUY' ? 'badge-buy' : 'badge-sell'}">
                                                <c:out value="${t.type}" />
                                            </span>
                                        </td>
                                        <td><fmt:formatNumber value="${t.quantity}" type="number" /></td>
                                        <td>₹ <fmt:formatNumber value="${t.price}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                                        <td>₹ <fmt:formatNumber value="${t.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.type == 'SELL'}">
                                                    <span style="font-weight: 600; color: ${t.realizedPnl >= 0 ? 'var(--success)' : 'var(--danger)'};">
                                                        <c:if test="${t.realizedPnl >= 0}">+</c:if>₹ <fmt:formatNumber value="${t.realizedPnl}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--text-muted);">&mdash;</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="9" style="text-align: center; color: var(--text-muted); padding: 30px;">
                                        No trades have been executed on the platform yet.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
