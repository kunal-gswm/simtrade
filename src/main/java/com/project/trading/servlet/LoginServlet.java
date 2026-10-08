package com.project.trading.servlet;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Role;
import com.project.trading.model.User;
import com.project.trading.service.UserService;
import com.project.trading.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService;

    public LoginServlet() {
        this.userService = new UserService();
    }

    public LoginServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser auth = currentUser(req);
        if (auth != null) {
            if (auth.getRole() == Role.ADMIN) {
                redirect(req, resp, "/admin/dashboard");
            } else {
                redirect(req, resp, "/app/dashboard");
            }
            return;
        }
        forward(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = userService.login(username, password);
            AuthUser authUser = new AuthUser(user.getId(), user.getUsername(), user.getRole());
            SessionUtil.login(req, authUser);

            if (user.getRole() == Role.ADMIN) {
                redirect(req, resp, "/admin/dashboard");
            } else {
                redirect(req, resp, "/app/dashboard");
            }
        } catch (AuthenticationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            forward(req, resp, "login.jsp");
        }
    }
}
