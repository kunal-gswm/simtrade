<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <main class="container">
        <div class="page-header">
            <div>
                <h1 class="page-title">Administrator Dashboard</h1>
                <p class="page-subtitle">Platform-wide trading metrics, instrument health, and operational status</p>
            </div>
        </div>

        <!-- Metric Stat Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-label">Total Users</div>
                <div class="stat-value"><c:out value="${stats.totalUsers}" /></div>
                <div class="stat-subtext">Registered platform traders</div>
            </div>

            <div class="stat-card">
                <div class="stat-label">Active Stocks</div>
                <div class="stat-value"><c:out value="${stats.activeStocks}" /></div>
                <div class="stat-subtext">Available for trading</div>
            </div>

            <div class="stat-card">
                <div class="stat-label">Total Trades</div>
                <div class="stat-value"><c:out value="${stats.totalTrades}" /></div>
                <div class="stat-subtext">Cumulative executed orders</div>
            </div>

            <div class="stat-card">
                <div class="stat-label">Total Traded Volume</div>
                <div class="stat-value" style="color: #60a5fa;">
                    ₹ <fmt:formatNumber value="${stats.totalTradedValue}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
                </div>
                <div class="stat-subtext">Gross platform transaction value</div>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(350px, 1fr)); gap: 24px;">
            <!-- Top Stocks by Traded Activity -->
            <div class="card">
                <h2 class="card-title">Top 5 Stocks by Trade Activity</h2>
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Stock Symbol</th>
                                <th>Trades Executed</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty stats.topStocksByTrades}">
                                    <c:forEach var="entry" items="${stats.topStocksByTrades}">
                                        <tr>
                                            <td>
                                                <span style="font-weight: 700; color: #93c5fd;">
                                                    <c:out value="${entry.key}" />
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge badge-success">
                                                    <c:out value="${entry.value}" /> trades
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="2" style="text-align: center; color: var(--text-muted);">
                                            No trading activity recorded yet.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Quick Management Shortcuts -->
            <div class="card">
                <h2 class="card-title">Quick Administration Actions</h2>
                <div style="display: flex; flex-direction: column; gap: 12px; margin-top: 10px;">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary" style="justify-content: flex-start;">
                        👥 <strong>Manage Users</strong> &mdash; View and block/unblock accounts
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/stocks" class="btn btn-secondary" style="justify-content: flex-start;">
                        📈 <strong>Manage Stocks</strong> &mdash; Add new stocks and toggle active status
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/prices" class="btn btn-secondary" style="justify-content: flex-start;">
                        ⚡ <strong>Price Simulation &amp; Fault Tool</strong> &mdash; Manual price override &amp; rollback demo
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/trades" class="btn btn-secondary" style="justify-content: flex-start;">
                        📜 <strong>All Trades Monitor</strong> &mdash; View recent 200 platform trades
                    </a>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
