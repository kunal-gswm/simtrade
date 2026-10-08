package com.project.trading.servlet;

import com.project.trading.exception.UserNotFoundException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.User;
import com.project.trading.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService;

    public AdminUsersServlet() {
        this.userService = new UserService();
    }

    public AdminUsersServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userService.listAll();
        req.setAttribute("users", users);
        forward(req, resp, "admin/users.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser admin = currentUser(req);
        if (admin == null) {
            redirect(req, resp, "/login");
            return;
        }

        long targetId = parseLongParam(req, "userId");
        boolean blocked = Boolean.parseBoolean(req.getParameter("blocked"));

        try {
            userService.setBlocked(admin.getId(), targetId, blocked);
            flashSuccess(req, blocked ? "User has been blocked successfully." : "User has been unblocked successfully.");
        } catch (ValidationException | UserNotFoundException e) {
            flashError(req, e.getMessage());
        }

        redirect(req, resp, "/admin/users");
    }
}
