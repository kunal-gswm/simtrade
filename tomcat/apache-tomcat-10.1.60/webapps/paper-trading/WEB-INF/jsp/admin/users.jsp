<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - SimTrade Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <main class="container">
        <div class="page-header">
            <div>
                <h1 class="page-title">User Management</h1>
                <p class="page-subtitle">View registered accounts, monitor cash balances, and manage access status</p>
            </div>
        </div>

        <div class="card">
            <h2 class="card-title">Registered Accounts</h2>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Cash Balance</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>#<c:out value="${u.id}" /></td>
                                <td><strong><c:out value="${u.username}" /></strong></td>
                                <td><c:out value="${u.fullName}" /></td>
                                <td><c:out value="${u.email}" /></td>
                                <td>
                                    <span class="role-tag ${u.role == 'ADMIN' ? 'role-admin' : 'role-user'}">
                                        <c:out value="${u.role}" />
                                    </span>
                                </td>
                                <td>
                                    ₹ <fmt:formatNumber value="${u.cashBalance}" type="number" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.status == 'ACTIVE'}">
                                            <span class="badge badge-success">ACTIVE</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">BLOCKED</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:formatDate value="${u.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.role == 'ADMIN' or u.id == sessionScope.AuthUser.id}">
                                            <span style="color: var(--text-muted); font-size: 0.8rem;">Protected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/admin/users" method="POST" style="display:inline;">
                                                <input type="hidden" name="userId" value="${u.id}">
                                                <c:choose>
                                                    <c:when test="${u.status == 'ACTIVE'}">
                                                        <input type="hidden" name="blocked" value="true">
                                                        <button type="submit" class="btn btn-danger btn-sm"
                                                                onclick="return confirm('Are you sure you want to block user &quot;${u.username}&quot;?');">
                                                            Block User
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="hidden" name="blocked" value="false">
                                                        <button type="submit" class="btn btn-primary btn-sm"
                                                                onclick="return confirm('Are you sure you want to unblock user &quot;${u.username}&quot;?');">
                                                            Unblock User
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
