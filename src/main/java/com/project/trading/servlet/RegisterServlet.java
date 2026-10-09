package com.project.trading.servlet;

import com.project.trading.exception.ValidationException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Role;
import com.project.trading.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService;

    public RegisterServlet() {
        this.userService = new UserService();
    }

    public RegisterServlet(UserService userService) {
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
        forward(req, resp, "register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        try {
            if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
                throw new ValidationException("Passwords do not match.");
            }

            userService.register(username, email, fullName, password);
            flashSuccess(req, "Account created. Log in.");
            redirect(req, resp, "/login");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            req.setAttribute("fullName", fullName);
            forward(req, resp, "register.jsp");
        }
    }
}
