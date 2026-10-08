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

    private final TradeDAO tradeDAO;

    public AdminTradesServlet() {
        this.tradeDAO = new TradeDAO();
    }

    public AdminTradesServlet(TradeDAO tradeDAO) {
        this.tradeDAO = tradeDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection c = DBConnection.getConnection()) {
            List<Trade> trades = tradeDAO.findRecent(c, 200);
            req.setAttribute("trades", trades);
            forward(req, resp, "admin/trades.jsp");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load platform trade history.", e);
        }
    }
}
