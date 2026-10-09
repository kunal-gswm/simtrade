<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Market - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .banner { background-color: #fff3cd; color: #856404; padding: 10px; text-align: center; font-weight: bold; margin-bottom: 20px; border-radius: 4px; }
        .nav-bar { display: flex; gap: 15px; margin-bottom: 20px; padding: 10px 0; border-bottom: 1px solid #ddd; }
        .nav-bar a { text-decoration: none; color: #007bff; font-weight: 500; }
        .nav-bar a:hover { text-decoration: underline; }
        .flash-success { background: #d4edda; color: #155724; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .flash-error { background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .search-form { display: flex; gap: 10px; margin-bottom: 20px; }
        .search-input { padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px; width: 300px; }
        .btn { padding: 8px 16px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; }
        .btn:hover { background-color: #0056b3; }
        .btn-secondary { background-color: #6c757d; }
        .btn-secondary:hover { background-color: #5a6268; }
        .market-table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        .market-table th, .market-table td { border: 1px solid #dee2e6; padding: 12px; text-align: left; }
        .market-table th { background-color: #f8f9fa; font-weight: 600; }
        .market-table tr:hover { background-color: #f1f3f5; }
        .text-green { color: #28a745; font-weight: bold; }
        .text-red { color: #dc3545; font-weight: bold; }
        .container { max-width: 1000px; margin: 0 auto; padding: 20px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
    </style>
</head>
<body>
<div class="container">
    <div class="banner">
        Paper trading simulator. Virtual money. Mock prices. Not real market data.
    </div>

    <nav class="nav-bar">
        <a href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/app/market" style="font-weight: bold;">Market</a>
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

    <h2>Active Stocks Market</h2>

    <form class="search-form" action="${pageContext.request.contextPath}/app/market" method="get">
        <input type="text" name="q" class="search-input" placeholder="Search by symbol or company..." value="<c:out value='${q}'/>" maxlength="50">
        <button type="submit" class="btn">Search</button>
        <c:if test="${not empty q}">
            <a href="${pageContext.request.contextPath}/app/market" class="btn btn-secondary">Clear</a>
        </c:if>
    </form>

    <c:choose>
        <c:when test="${empty stocks}">
            <div style="padding: 20px; background: #f8f9fa; text-align: center; border-radius: 4px; color: #6c757d;">
                No stocks match
            </div>
        </c:when>
        <c:otherwise>
            <table class="market-table">
                <thead>
                    <tr>
                        <th>Symbol</th>
                        <th>Company Name</th>
                        <th>Sector</th>
                        <th>Price</th>
                        <th>Day Change</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="stock" items="${stocks}">
                        <tr>
                            <td><strong><c:out value="${stock.symbol}"/></strong></td>
                            <td><c:out value="${stock.companyName}"/></td>
                            <td><c:out value="${stock.sector != null ? stock.sector : '-'}"/></td>
                            <td>
                                ₹<fmt:formatNumber value="${stock.price}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
                            </td>
                            <td>
                                <c:set var="chg" value="${stock.changePercent}"/>
                                <c:choose>
                                    <c:when test="${chg > 0}">
                                        <span class="text-green">+<fmt:formatNumber value="${chg}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%</span>
                                    </c:when>
                                    <c:when test="${chg < 0}">
                                        <span class="text-red"><fmt:formatNumber value="${chg}" type="number" minFractionDigits="2" maxFractionDigits="2"/>%</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span>0.00%</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/app/stock?id=${stock.id}" class="btn" style="padding: 4px 12px; font-size: 14px;">Trade</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
