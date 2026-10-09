package com.project.trading.servlet;

import com.project.trading.dao.TradeDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.Trade;
import com.project.trading.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/trades")
public class AdminTradesServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final com.project.trading.service.AdminService adminService;

    public AdminTradesServlet() {
        this.adminService = new com.project.trading.service.AdminService();
    }

    public AdminTradesServlet(com.project.trading.service.AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Trade> trades = adminService.getRecentTrades(200);
        req.setAttribute("trades", trades);
        forward(req, resp, "admin/trades.jsp");
    }
}
