package com.project.trading.servlet;

import com.project.trading.dao.HoldingDAO;
import com.project.trading.exception.StockNotFoundException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Holding;
import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import com.project.trading.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/app/stock")
public class StockDetailServlet extends BaseServlet {

    private final StockService stockService = new StockService();
    private final HoldingDAO holdingDAO = new HoldingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long stockId = parseLongParam(req, "id");
        if (stockId <= 0) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid stock ID");
            return;
        }

        Stock stock;
        try {
            stock = stockService.getById(stockId);
        } catch (StockNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Stock not found");
            return;
        }

        AuthUser authUser = currentUser(req);
        int heldQty = 0;
        BigDecimal cash = BigDecimal.ZERO;

        if (authUser != null) {
            try (Connection c = DBConnection.getConnection()) {
                Holding h = holdingDAO.find(c, authUser.getId(), stockId);
                if (h != null) {
                    heldQty = h.getQuantity();
                }

                String sql = "SELECT cash_balance FROM users WHERE id = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setLong(1, authUser.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            cash = rs.getBigDecimal("cash_balance");
                        }
                    }
                }
            } catch (SQLException e) {
                // If query fails, default to zero and proceed to render stock details
            }
        }

        req.setAttribute("stock", stock);
        req.setAttribute("heldQty", heldQty);
        req.setAttribute("cash", cash);
        forward(req, resp, "stock-detail.jsp");
    }
}
