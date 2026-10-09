<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - SimTrade</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

    <div class="auth-wrapper">
        <div class="auth-card" style="text-align: center; max-width: 500px;">
            <div style="font-size: 3.5rem; margin-bottom: 12px;">⚠️</div>
            <h1 style="font-size: 1.6rem; font-weight: 700; margin-bottom: 10px;">
                <c:choose>
                    <c:when test="${pageContext.errorData.statusCode == 403}">
                        403 - Access Forbidden
                    </c:when>
                    <c:when test="${pageContext.errorData.statusCode == 404}">
                        404 - Page Not Found
                    </c:when>
                    <c:otherwise>
                        Something Went Wrong
                    </c:otherwise>
                </c:choose>
            </h1>
            <p style="color: var(--text-secondary); margin-bottom: 24px; font-size: 0.95rem;">
                <c:choose>
                    <c:when test="${pageContext.errorData.statusCode == 403}">
                        You do not have permission to access the requested resource.
                    </c:when>
                    <c:when test="${pageContext.errorData.statusCode == 404}">
                        The page or resource you are looking for does not exist or has been moved.
                    </c:when>
                    <c:otherwise>
                        An unexpected error occurred while processing your request. Please try again.
                    </c:otherwise>
                </c:choose>
            </p>

            <a href="${pageContext.request.contextPath}/" class="btn btn-primary" style="display: inline-block;">
                Return to Home
            </a>
        </div>
    </div>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
