<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prices & Simulator - Admin - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .banner { background-color: #fff3cd; color: #856404; padding: 10px; text-align: center; font-weight: bold; margin-bottom: 20px; border-radius: 4px; }
        .nav-bar { display: flex; gap: 15px; margin-bottom: 20px; padding: 10px 0; border-bottom: 1px solid #ddd; }
        .nav-bar a { text-decoration: none; color: #007bff; font-weight: 500; }
        .nav-bar a:hover { text-decoration: underline; }
        .flash-success { background: #d4edda; color: #155724; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .flash-error { background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .container { max-width: 1100px; margin: 0 auto; padding: 20px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .control-panel { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 25px; }
        .card { border: 1px solid #dee2e6; border-radius: 6px; padding: 20px; background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        .badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 13px; font-weight: bold; }
        .badge-running { background-color: #d4edda; color: #155724; }
        .badge-stopped { background-color: #f8d7da; color: #721c24; }
        .badge-fault-on { background-color: #f8d7da; color: #721c24; border: 1px solid #dc3545; }
        .badge-fault-off { background-color: #e2e3e5; color: #383d41; }
        .btn { padding: 6px 14px; border: none; border-radius: 4px; cursor: pointer; color: white; font-weight: 500; text-decoration: none; font-size: 14px; }
        .btn-success { background-color: #28a745; }
        .btn-success:hover { background-color: #218838; }
        .btn-danger { background-color: #dc3545; }
        .btn-danger:hover { background-color: #c82333; }
        .btn-warning { background-color: #ffc107; color: #212529; }
        .btn-warning:hover { background-color: #e0a800; }
        .btn-primary { background-color: #007bff; }
        .btn-primary:hover { background-color: #0056b3; }
        .table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        .table th, .table td { border: 1px solid #dee2e6; padding: 10px 12px; text-align: left; vertical-align: middle; }
        .table th { background-color: #f8f9fa; font-weight: 600; }
        .price-input { width: 120px; padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; }
    </style>
</head>
<body>
<div class="container">
    <div class="banner">
        Paper trading simulator. Virtual money. Mock prices. Not real market data.
    </div>

    <nav class="nav-bar">
        <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/admin/users">Users</a>
        <a href="${pageContext.request.contextPath}/admin/stocks">Stocks</a>
        <a href="${pageContext.request.contextPath}/admin/prices" style="font-weight: bold;">Prices & Simulator</a>
        <a href="${pageContext.request.contextPath}/admin/trades">Trades Monitor</a>
        <a href="${pageContext.request.contextPath}/logout" style="margin-left: auto; color: #dc3545;">Logout</a>
    </nav>

    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="flash-success"><c:out value="${sessionScope.flashSuccess}"/></div>
        <c:remove var="flashSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="flash-error"><c:out value="${sessionScope.flashError}"/></div>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <!-- ADMIN CONTROLS: SIMULATOR & FAULT INJECTION -->
    <div class="control-panel">
        <!-- PRICE SIMULATOR CARD -->
        <div class="card">
            <h3>Background Price Simulator</h3>
            <p style="color: #6c757d; font-size: 14px;">
                Simulates random-walk market price movements (±1.5%) on a background daemon thread every 5 seconds.
            </p>
            <div style="margin-bottom: 15px;">
                Status:
                <c:choose>
                    <c:when test="${simulatorRunning}">
                        <span class="badge badge-running">RUNNING</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-stopped">STOPPED</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <form action="${pageContext.request.contextPath}/admin/prices" method="post">
                <input type="hidden" name="action" value="simulator">
                <c:choose>
                    <c:when test="${simulatorRunning}">
                        <input type="hidden" name="toggle" value="stop">
                        <button type="submit" class="btn btn-danger">Stop Simulator</button>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="toggle" value="start">
                        <button type="submit" class="btn btn-success">Start Simulator</button>
                    </c:otherwise>
                </c:choose>
            </form>
        </div>

        <!-- DEMO TOOL: FAULT INJECTION CARD -->
        <div class="card" style="border-left: 4px solid #ffc107;">
            <h3>DEMO TOOL: Transaction Fault Injection</h3>
            <p style="color: #6c757d; font-size: 14px;">
                Forces an intentional runtime exception mid-trade (after balance/holding updates but before trade recording) to demonstrate ACID JDBC transaction rollback.
            </p>
            <div style="margin-bottom: 15px;">
                Status:
                <c:choose>
                    <c:when test="${faultOn}">
                        <span class="badge badge-fault-on">FAULT ON (TRADES WILL ROLLBACK)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-fault-off">FAULT OFF (NORMAL TRADING)</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <form action="${pageContext.request.contextPath}/admin/prices" method="post">
                <input type="hidden" name="action" value="fault">
                <c:choose>
                    <c:when test="${faultOn}">
                        <button type="submit" class="btn btn-secondary">Disable Fault Injection</button>
                    </c:when>
                    <c:otherwise>
                        <button type="submit" class="btn btn-warning">Enable Fault Injection</button>
                    </c:otherwise>
                </c:choose>
            </form>
        </div>
    </div>

    <!-- MANUAL PRICE UPDATE TABLE -->
    <div class="card">
        <h3>Manual Price Management</h3>
        <p style="color: #6c757d; font-size: 14px; margin-bottom: 15px;">
            Set exact stock prices manually for testing or live demonstrations. In MySQL, setting the new price automatically shifts the current price to previous price.
        </p>

        <table class="table">
            <thead>
                <tr>
                    <th>Symbol</th>
                    <th>Company Name</th>
                    <th>Previous Price</th>
                    <th>Current Price</th>
                    <th>New Price (₹)</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="stock" items="${stocks}">
                    <tr>
                        <td><strong><c:out value="${stock.symbol}"/></strong></td>
                        <td><c:out value="${stock.companyName}"/></td>
                        <td>₹<fmt:formatNumber value="${stock.prevPrice}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                        <td>₹<fmt:formatNumber value="${stock.price}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                        <td colspan="2">
                            <form action="${pageContext.request.contextPath}/admin/prices" method="post" style="display: flex; gap: 10px; align-items: center; margin: 0;">
                                <input type="hidden" name="action" value="price">
                                <input type="hidden" name="id" value="${stock.id}">
                                <input type="number" name="price" class="price-input" step="0.01" min="0.01" max="9999999.99" value="<fmt:formatNumber value='${stock.price}' type='number' minFractionDigits='2' maxFractionDigits='2' groupingUsed='false'/>" required>
                                <button type="submit" class="btn btn-primary">Update Price</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
