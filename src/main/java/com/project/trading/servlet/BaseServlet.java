package com.project.trading.servlet;

import com.project.trading.model.AuthUser;
import com.project.trading.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected AuthUser currentUser(HttpServletRequest req) {
        return SessionUtil.getAuthUser(req);
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String jsp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/" + jsp).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected long parseLongParam(HttpServletRequest req, String name) {
        String val = req.getParameter(name);
        if (val == null || val.trim().isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    protected void flashSuccess(HttpServletRequest req, String message) {
        SessionUtil.flashSuccess(req, message);
    }

    protected void flashError(HttpServletRequest req, String message) {
        SessionUtil.flashError(req, message);
    }
}
