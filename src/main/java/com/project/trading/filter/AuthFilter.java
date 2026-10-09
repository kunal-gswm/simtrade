package com.project.trading.filter;

import com.project.trading.model.AuthUser;
import com.project.trading.model.Role;
import com.project.trading.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/app/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        AuthUser authUser = SessionUtil.getAuthUser(req);
        String servletPath = req.getServletPath();

        // 1. Unauthenticated users are redirected to login
        if (authUser == null) {
            SessionUtil.flashError(req, "Session expired. Please log in again.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 2. Admin realm protection
        if (servletPath != null && servletPath.startsWith("/admin")) {
            if (authUser.getRole() != Role.ADMIN) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Administrator privileges required.");
                return;
            }
        }

        // 3. Trader realm access for admins
        if (servletPath != null && servletPath.startsWith("/app")) {
            if (authUser.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
