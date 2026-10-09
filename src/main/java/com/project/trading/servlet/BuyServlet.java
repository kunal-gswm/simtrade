package com.project.trading.servlet;

import com.project.trading.exception.AppException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.model.AuthUser;
import com.project.trading.model.Trade;
import com.project.trading.service.TradingService;
import com.project.trading.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DecimalFormat;

@WebServlet("/app/buy")
public class BuyServlet extends BaseServlet {
    private final TradingService tradingService = new TradingService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/login");
            return;
        }

        long stockId = parseLongParam(req, "stockId");
        String qtyParam = req.getParameter("quantity");

        try {
            int qty = Validator.parsePositiveInt(qtyParam, "quantity");
            Trade trade = tradingService.buy(user.getId(), stockId, qty);

            DecimalFormat df = new DecimalFormat("#,##0.00");
            flashSuccess(req, "Bought " + trade.getQuantity() + " " + trade.getStockSymbol() + " @ ₹" + df.format(trade.getPrice()));
            redirect(req, resp, "/app/portfolio");
            
        } catch (AppException e) {
            flashError(req, e.getMessage());
            redirect(req, resp, "/app/stock?id=" + stockId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            flashError(req, "Trade failed. No changes were saved.");
            redirect(req, resp, "/app/stock?id=" + stockId);
        }
    }
}
