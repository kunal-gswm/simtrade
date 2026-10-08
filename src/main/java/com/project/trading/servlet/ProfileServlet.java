package com.project.trading.servlet;

import com.project.trading.exception.AppException;
import com.project.trading.exception.AuthenticationException;
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

@WebServlet("/app/profile")
public class ProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService;

    public ProfileServlet() {
        this.userService = new UserService();
    }

    public ProfileServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser auth = currentUser(req);
        if (auth == null) {
            redirect(req, resp, "/login");
            return;
        }

        try {
            User user = userService.getById(auth.getId());
            req.setAttribute("user", user);
            forward(req, resp, "profile.jsp");
        } catch (UserNotFoundException e) {
            flashError(req, "User profile not found.");
            redirect(req, resp, "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser auth = currentUser(req);
        if (auth == null) {
            redirect(req, resp, "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("password".equalsIgnoreCase(action)) {
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                String confirmPassword = req.getParameter("confirmPassword");

                if (newPassword == null || !newPassword.equals(confirmPassword)) {
                    throw new ValidationException("New passwords do not match.");
                }

                userService.changePassword(auth.getId(), oldPassword, newPassword);
                flashSuccess(req, "Password changed successfully.");
                redirect(req, resp, "/app/profile");
            } else {
                // Default to profile info update
                String fullName = req.getParameter("fullName");
                String email = req.getParameter("email");

                userService.updateProfile(auth.getId(), fullName, email);
                flashSuccess(req, "Profile updated successfully.");
                redirect(req, resp, "/app/profile");
            }
        } catch (ValidationException | AuthenticationException | UserNotFoundException e) {
            req.setAttribute("error", e.getMessage());
            try {
                User user = userService.getById(auth.getId());
                req.setAttribute("user", user);
            } catch (UserNotFoundException ignored) {
            }
            forward(req, resp, "profile.jsp");
        }
    }
}
