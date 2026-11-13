package com.project.trading.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * CSRF protection filter for standard Servlet POST endpoints.
 * Validates CSRF synchronizer tokens against the user session.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = {"/app/*", "/admin/*", "/legacy/admin/*", "/trade", "/logout"})
public class CsrfFilter implements Filter {

    public static final String CSRF_TOKEN_ATTR = "csrfToken";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    public static final String CSRF_HEADER_NAME = "X-CSRF-Token";

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Vaadin Flow handles its own native UIDL and push CSRF token protection
        if (req.getParameter("v-r") != null) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (sessionToken == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            sessionToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_TOKEN_ATTR, sessionToken);
        }

        // Expose token to request scope so JSPs can easily render ${csrfToken}
        req.setAttribute(CSRF_TOKEN_ATTR, sessionToken);

        String method = req.getMethod();
        if (SAFE_METHODS.contains(method.toUpperCase())) {
            chain.doFilter(request, response);
            return;
        }

        // Validate token on mutating requests (POST, PUT, DELETE, etc.)
        String requestToken = req.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.trim().isEmpty()) {
            requestToken = req.getHeader(CSRF_HEADER_NAME);
        }

        if (requestToken == null || !requestToken.equals(sessionToken)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
