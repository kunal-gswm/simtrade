<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<div class="card" style="margin: 40px auto; max-width: 600px; text-align: center; padding: 40px;">
    <h1 style="color: var(--tv-bear); font-size: 4rem; margin-bottom: 8px;">Oops!</h1>
    <h2 style="margin-bottom: 24px; color: var(--tv-text-heading);">Something went wrong.</h2>
    
    <c:choose>
        <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 404}">
            <p style="color: var(--tv-text-secondary); margin-bottom: 32px; font-size: 1.1rem;">The page you are looking for was not found (404).</p>
        </c:when>
        <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 403}">
            <p style="color: var(--tv-text-secondary); margin-bottom: 32px; font-size: 1.1rem;">You don't have permission to access this page (403).</p>
        </c:when>
        <c:when test="${not empty requestScope.errorMessage}">
            <p style="color: var(--tv-text-secondary); margin-bottom: 32px; font-size: 1.1rem;"><c:out value="${requestScope.errorMessage}"/></p>
        </c:when>
        <c:otherwise>
            <p style="color: var(--tv-text-secondary); margin-bottom: 32px; font-size: 1.1rem;">An unexpected server error occurred.</p>
        </c:otherwise>
    </c:choose>

    <a href="${pageContext.request.contextPath}/" class="tv-trade-btn" style="padding: 10px 24px; font-size: 1rem;">Return Home</a>
</div>
<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
