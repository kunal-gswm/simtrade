package com.project.trading.servlet;

import com.project.trading.model.PlatformStats;
import com.project.trading.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService;

    public AdminDashboardServlet() {
        this.adminService = new AdminService();
    }

    public AdminDashboardServlet(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PlatformStats stats = adminService.getPlatformStats();
        req.setAttribute("stats", stats);
        forward(req, resp, "admin/dashboard.jsp");
    }
}
