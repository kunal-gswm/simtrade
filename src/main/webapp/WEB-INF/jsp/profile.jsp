<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <main class="container">
        <div class="page-header">
            <div>
                <h1 class="page-title">User Profile</h1>
                <p class="page-subtitle">Manage your personal account settings and security</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <span>❌ <c:out value="${error}" /></span>
            </div>
        </c:if>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(340px, 1fr)); gap: 24px;">
            <!-- Profile Information Card -->
            <div class="card">
                <h2 class="card-title">Account Details</h2>
                <form action="${pageContext.request.contextPath}/app/profile" method="POST" class="form-grid">
                    <input type="hidden" name="action" value="profile">

                    <div class="form-group">
                        <label class="form-label">Username</label>
                        <input type="text" class="form-control" value="<c:out value='${user.username}' />" readonly disabled>
                        <small style="color: var(--text-muted); font-size: 0.75rem;">Username cannot be changed.</small>
                    </div>

                    <div class="form-group">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input type="text" id="fullName" name="fullName" class="form-control" 
                               value="<c:out value='${user.fullName}' />" required>
                    </div>

                    <div class="form-group">
                        <label for="email" class="form-label">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" 
                               value="<c:out value='${user.email}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Virtual Cash Balance</label>
                        <input type="text" class="form-control" 
                               value="₹ <fmt:formatNumber value='${user.cashBalance}' type='number' minFractionDigits='2' maxFractionDigits='2' groupingUsed='true'/>" readonly disabled>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Save Profile</button>
                    </div>
                </form>
            </div>

            <!-- Password Change Card -->
            <div class="card">
                <h2 class="card-title">Change Password</h2>
                <form action="${pageContext.request.contextPath}/app/profile" method="POST" class="form-grid">
                    <input type="hidden" name="action" value="password">

                    <div class="form-group">
                        <label for="oldPassword" class="form-label">Current Password</label>
                        <input type="password" id="oldPassword" name="oldPassword" class="form-control" 
                               required placeholder="Enter current password">
                    </div>

                    <div class="form-group">
                        <label for="newPassword" class="form-label">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" class="form-control" 
                               required placeholder="Minimum 8 characters (1 letter &amp; 1 digit)">
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword" class="form-label">Confirm New Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                               required placeholder="Confirm new password">
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-warning">Update Password</button>
                    </div>
                </form>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
