<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${stock.symbol}"/> - Stock Details - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .banner { background-color: #fff3cd; color: #856404; padding: 10px; text-align: center; font-weight: bold; margin-bottom: 20px; border-radius: 4px; }
        .nav-bar { display: flex; gap: 15px; margin-bottom: 20px; padding: 10px 0; border-bottom: 1px solid #ddd; }
        .nav-bar a { text-decoration: none; color: #007bff; font-weight: 500; }
        .nav-bar a:hover { text-decoration: underline; }
        .flash-success { background: #d4edda; color: #155724; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .flash-error { background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .container { max-width: 900px; margin: 0 auto; padding: 20px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .stock-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #eee; }
        .price-badge { font-size: 24px; font-weight: bold; }
        .indicative-text { font-size: 13px; color: #6c757d; font-weight: normal; }
        .badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
        .badge-active { background-color: #d4edda; color: #155724; }
        .badge-inactive { background-color: #f8d7da; color: #721c24; }
        .text-green { color: #28a745; }
        .text-red { color: #dc3545; }
        .trade-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 25px; }
        .trade-card { border: 1px solid #dee2e6; border-radius: 8px; padding: 20px; background: #fafafa; }
        .trade-card.buy { border-top: 4px solid #28a745; }
        .trade-card.sell { border-top: 4px solid #dc3545; }
        .trade-card h3 { margin-top: 0; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: 500; }
        .form-control { width: 100%; box-sizing: border-box; padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px; }
        .btn { width: 100%; padding: 10px; border: none; border-radius: 4px; font-size: 16px; font-weight: bold; cursor: pointer; color: white; }
        .btn-buy { background-color: #28a745; }
        .btn-buy:hover { background-color: #218838; }
        .btn-sell { background-color: #dc3545; }
        .btn-sell:hover { background-color: #c82333; }
        .btn:disabled { background-color: #6c757d; cursor: not-allowed; }
        .user-balance-bar { display: flex; gap: 20px; background-color: #e9ecef; padding: 12px 18px; border-radius: 6px; margin-bottom: 20px; }
        .user-balance-bar div { font-size: 14px; }
    </style>
</head>
<body>
<div class="container">
    <div class="banner">
        Paper trading simulator. Virtual money. Mock prices. Not real market data.
    </div>

    <nav class="nav-bar">
        <a href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/app/market">Market</a>
        <a href="${pageContext.request.contextPath}/app/portfolio">Portfolio</a>
        <a href="${pageContext.request.contextPath}/app/history">Trade History</a>
        <a href="${pageContext.request.contextPath}/app/profile">Profile</a>
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

    <div class="stock-header">
        <div>
            <h1 style="margin: 0;"><c:out value="${stock.companyName}"/> (<c:out value="${stock.symbol}"/>)</h1>
            <p style="margin: 5px 0; color: #6c757d;">Sector: <c:out value="${stock.sector != null ? stock.sector : 'General'}"/></p>
            <c:choose>
                <c:when test="${stock.active}">
                    <span class="badge badge-active">Active</span>
                </c:when>
                <c:otherwise>
                    <span class="badge badge-inactive">Inactive / Deactivated</span>
                </c:otherwise>
            </c:choose>
        </div>
        <div style="text-align: right;">
            <div class="price-badge">
                ₹<fmt:formatNumber value="${stock.price}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
            </div>
            <div class="indicative-text">Indicative price (execution at live price)</div>
            <c:set var="chg" value="${stock.changePercent}"/>
            <div>
                <c:choose>
                    <c:when test="${chg > 0}">
                        <span class="text-green font-weight-bold">+<fmt:formatNumber value="${chg}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%</span>
                    </c:when>
                    <c:when test="${chg < 0}">
                        <span class="text-red font-weight-bold"><fmt:formatNumber value="${chg}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%</span>
                    </c:when>
                    <c:otherwise>
                        <span>0.00%</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <c:if test="${not empty stock.description}">
        <p style="color: #495057; line-height: 1.5;"><c:out value="${stock.description}"/></p>
    </c:if>

    <div class="user-balance-bar">
        <div>Available Cash: <strong>₹<fmt:formatNumber value="${cash}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></strong></div>
        <div>Currently Held: <strong><c:out value="${heldQty}"/> shares</strong></div>
    </div>

    <div class="trade-grid">
        <!-- BUY FORM -->
        <div class="trade-card buy">
            <h3>Buy Shares</h3>
            <c:choose>
                <c:when test="${not stock.active}">
                    <p style="color: #dc3545; font-size: 14px;">This stock is inactive and cannot be purchased.</p>
                </c:when>
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/app/buy" method="post">
                        <input type="hidden" name="stockId" value="${stock.id}">
                        <div class="form-group">
                            <label for="buyQty">Quantity (1 - 10,000):</label>
                            <input type="number" id="buyQty" name="quantity" class="form-control" min="1" max="10000" required placeholder="Enter quantity">
                        </div>
                        <button type="submit" class="btn btn-buy">Buy at Indicative ₹<fmt:formatNumber value="${stock.price}" type="number" minFractionDigits="2" maxFractionDigits="2"/></button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- SELL FORM -->
        <div class="trade-card sell">
            <h3>Sell Shares</h3>
            <c:choose>
                <c:when test="${heldQty <= 0}">
                    <p style="color: #6c757d; font-size: 14px;">You currently hold 0 shares of this stock to sell.</p>
                </c:when>
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/app/sell" method="post">
                        <input type="hidden" name="stockId" value="${stock.id}">
                        <div class="form-group">
                            <label for="sellQty">Quantity (Max: <c:out value="${heldQty}"/>):</label>
                            <input type="number" id="sellQty" name="quantity" class="form-control" min="1" max="${heldQty}" required placeholder="Enter quantity">
                        </div>
                        <button type="submit" class="btn btn-sell">Sell at Indicative ₹<fmt:formatNumber value="${stock.price}" type="number" minFractionDigits="2" maxFractionDigits="2"/></button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/app/market" style="color: #007bff; text-decoration: none;">&larr; Back to Market</a>
    </div>
</div>
</body>
</html>
