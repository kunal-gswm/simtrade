<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Stocks - Admin - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .banner { background-color: #fff3cd; color: #856404; padding: 10px; text-align: center; font-weight: bold; margin-bottom: 20px; border-radius: 4px; }
        .nav-bar { display: flex; gap: 15px; margin-bottom: 20px; padding: 10px 0; border-bottom: 1px solid #ddd; }
        .nav-bar a { text-decoration: none; color: #007bff; font-weight: 500; }
        .nav-bar a:hover { text-decoration: underline; }
        .flash-success { background: #d4edda; color: #155724; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .flash-error { background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .container { max-width: 1100px; margin: 0 auto; padding: 20px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .card { border: 1px solid #dee2e6; border-radius: 6px; padding: 20px; margin-bottom: 25px; background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        .form-row { display: flex; gap: 15px; margin-bottom: 15px; flex-wrap: wrap; }
        .form-group { flex: 1; min-width: 200px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: 500; }
        .form-control { width: 100%; box-sizing: border-box; padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px; }
        .btn { padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; color: white; font-weight: 500; text-decoration: none; display: inline-block; font-size: 14px; }
        .btn-primary { background-color: #007bff; }
        .btn-primary:hover { background-color: #0056b3; }
        .btn-secondary { background-color: #6c757d; }
        .btn-secondary:hover { background-color: #5a6268; }
        .btn-success { background-color: #28a745; }
        .btn-success:hover { background-color: #218838; }
        .btn-danger { background-color: #dc3545; }
        .btn-danger:hover { background-color: #c82333; }
        .table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        .table th, .table td { border: 1px solid #dee2e6; padding: 10px 12px; text-align: left; vertical-align: middle; }
        .table th { background-color: #f8f9fa; font-weight: 600; }
        .badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
        .badge-active { background-color: #d4edda; color: #155724; }
        .badge-inactive { background-color: #f8d7da; color: #721c24; }
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
        <a href="${pageContext.request.contextPath}/admin/stocks" style="font-weight: bold;">Stocks</a>
        <a href="${pageContext.request.contextPath}/admin/prices">Prices & Simulator</a>
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

    <!-- ADD OR EDIT STOCK FORM -->
    <div class="card">
        <c:choose>
            <c:when test="${not empty editStock}">
                <h3>Edit Stock: <c:out value="${editStock.symbol}"/></h3>
                <form action="${pageContext.request.contextPath}/admin/stocks" method="post">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${editStock.id}">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Symbol</label>
                            <input type="text" class="form-control" value="<c:out value='${editStock.symbol}'/>" disabled>
                        </div>
                        <div class="form-group">
                            <label for="editName">Company Name *</label>
                            <input type="text" id="editName" name="companyName" class="form-control" required maxlength="100" value="<c:out value='${editStock.companyName}'/>">
                        </div>
                        <div class="form-group">
                            <label for="editSector">Sector</label>
                            <input type="text" id="editSector" name="sector" class="form-control" maxlength="50" value="<c:out value='${editStock.sector}'/>">
                        </div>
                    </div>
                    <div class="form-group" style="margin-bottom: 15px;">
                        <label for="editDesc">Description</label>
                        <input type="text" id="editDesc" name="description" class="form-control" maxlength="500" value="<c:out value='${editStock.description}'/>">
                    </div>
                    <div>
                        <button type="submit" class="btn btn-primary">Update Stock</button>
                        <a href="${pageContext.request.contextPath}/admin/stocks" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <h3>Add New Stock</h3>
                <form action="${pageContext.request.contextPath}/admin/stocks" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="addSymbol">Symbol * (e.g. TCS)</label>
                            <input type="text" id="addSymbol" name="symbol" class="form-control" required maxlength="15" placeholder="A-Z, 0-9, &, -">
                        </div>
                        <div class="form-group">
                            <label for="addName">Company Name *</label>
                            <input type="text" id="addName" name="companyName" class="form-control" required maxlength="100" placeholder="Full legal company name">
                        </div>
                        <div class="form-group">
                            <label for="addSector">Sector</label>
                            <input type="text" id="addSector" name="sector" class="form-control" maxlength="50" placeholder="e.g. IT, Banking">
                        </div>
                        <div class="form-group">
                            <label for="addPrice">Initial Price (₹) *</label>
                            <input type="number" id="addPrice" name="price" class="form-control" step="0.01" min="0.01" max="9999999.99" required placeholder="0.00">
                        </div>
                    </div>
                    <div class="form-group" style="margin-bottom: 15px;">
                        <label for="addDesc">Description</label>
                        <input type="text" id="addDesc" name="description" class="form-control" maxlength="500" placeholder="Brief company summary">
                    </div>
                    <div>
                        <button type="submit" class="btn btn-success">Add Stock</button>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- STOCKS LIST TABLE -->
    <div class="card">
        <h3>All Registered Stocks (${stocks.size()})</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Symbol</th>
                    <th>Company Name</th>
                    <th>Sector</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="stock" items="${stocks}">
                    <tr>
                        <td><c:out value="${stock.id}"/></td>
                        <td><strong><c:out value="${stock.symbol}"/></strong></td>
                        <td><c:out value="${stock.companyName}"/></td>
                        <td><c:out value="${stock.sector != null ? stock.sector : '-'}"/></td>
                        <td>₹<fmt:formatNumber value="${stock.price}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${stock.active}">
                                    <span class="badge badge-active">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-inactive">Deactivated</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div style="display: flex; gap: 8px;">
                                <a href="${pageContext.request.contextPath}/admin/stocks?edit=${stock.id}" class="btn btn-primary" style="padding: 4px 10px; font-size: 13px;">Edit</a>
                                <form action="${pageContext.request.contextPath}/admin/stocks" method="post" style="display: inline; margin: 0;">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="id" value="${stock.id}">
                                    <input type="hidden" name="active" value="${!stock.active}">
                                    <c:choose>
                                        <c:when test="${stock.active}">
                                            <button type="submit" class="btn btn-danger" style="padding: 4px 10px; font-size: 13px;" onclick="return confirm('Deactivate stock ${stock.symbol}? Users will not be able to buy it.');">Deactivate</button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="submit" class="btn btn-success" style="padding: 4px 10px; font-size: 13px;">Activate</button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
