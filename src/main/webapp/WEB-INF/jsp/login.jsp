<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="brand-header">
                <h1>Sign In to SimTrade</h1>
                <p>Enter your credentials to access your virtual trading account</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <span><c:out value="${error}" /></span>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST" class="form-grid">
                <div class="form-group">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" id="username" name="username" class="form-control" 
                           value="<c:out value='${username}' />" required autofocus placeholder="Enter your username">
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           required placeholder="Enter your password">
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">
                    Sign In
                </button>
            </form>

            <div class="auth-footer">
                Don't have an account? 
                <a href="${pageContext.request.contextPath}/register">Create an account</a>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
