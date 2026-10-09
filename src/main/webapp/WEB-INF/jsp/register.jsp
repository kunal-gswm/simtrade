<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <div class="auth-wrapper">
        <div class="auth-card" style="max-width: 480px;">
            <div class="brand-header">
                <h1>Create Your Account</h1>
                <p>Start simulated trading with ₹100,000 in virtual funds</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <span><c:out value="${error}" /></span>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="POST" class="form-grid">
                <div class="form-group">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" id="username" name="username" class="form-control" 
                           value="<c:out value='${username}' />" required placeholder="3-30 chars (letters, digits, _)" autofocus>
                </div>

                <div class="form-group">
                    <label for="email" class="form-label">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" 
                           value="<c:out value='${email}' />" required placeholder="name@example.com">
                </div>

                <div class="form-group">
                    <label for="fullName" class="form-label">Full Name</label>
                    <input type="text" id="fullName" name="fullName" class="form-control" 
                           value="<c:out value='${fullName}' />" required placeholder="e.g. Rahul Sharma">
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           required placeholder="At least 8 characters (1 letter &amp; 1 digit)">
                </div>

                <div class="form-group">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                           required placeholder="Re-enter your password">
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">
                    Register Account
                </button>
            </form>

            <div class="auth-footer">
                Already have an account? 
                <a href="${pageContext.request.contextPath}/login">Sign In</a>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
