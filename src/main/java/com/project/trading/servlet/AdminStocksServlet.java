package com.project.trading.servlet;

import com.project.trading.exception.StockNotFoundException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.Stock;
import com.project.trading.service.StockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/stocks")
public class AdminStocksServlet extends BaseServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long editId = parseLongParam(req, "edit");
        Stock editStock = null;
        if (editId > 0) {
            try {
                editStock = stockService.getById(editId);
            } catch (StockNotFoundException e) {
                flashError(req, "Stock not found for editing.");
            }
        }

        List<Stock> stocks = stockService.listAll();
        req.setAttribute("stocks", stocks);
        req.setAttribute("editStock", editStock);
        forward(req, resp, "admin/stocks.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            flashError(req, "Missing action parameter.");
            redirect(req, resp, "/admin/stocks");
            return;
        }

        try {
            switch (action) {
                case "add": {
                    String symbol = req.getParameter("symbol");
                    String companyName = req.getParameter("companyName");
                    String sector = req.getParameter("sector");
                    String description = req.getParameter("description");
                    String priceStr = req.getParameter("price");

                    if (priceStr == null || priceStr.trim().isEmpty()) {
                        throw new ValidationException("Price is required.");
                    }
                    BigDecimal price;
                    try {
                        price = new BigDecimal(priceStr.trim());
                    } catch (NumberFormatException e) {
                        throw new ValidationException("Price must be a valid number.");
                    }

                    Stock created = stockService.add(symbol, companyName, sector, description, price);
                    flashSuccess(req, "Stock " + created.getSymbol() + " added successfully.");
                    break;
                }
                case "edit": {
                    long id = parseLongParam(req, "id");
                    if (id <= 0) {
                        throw new ValidationException("Invalid stock ID.");
                    }
                    String companyName = req.getParameter("companyName");
                    String sector = req.getParameter("sector");
                    String description = req.getParameter("description");

                    stockService.update(id, companyName, sector, description);
                    flashSuccess(req, "Stock updated successfully.");
                    break;
                }
                case "toggle": {
                    long id = parseLongParam(req, "id");
                    if (id <= 0) {
                        throw new ValidationException("Invalid stock ID.");
                    }
                    boolean active = Boolean.parseBoolean(req.getParameter("active"));
                    stockService.setActive(id, active);
                    flashSuccess(req, "Stock " + (active ? "activated" : "deactivated") + " successfully.");
                    break;
                }
                default:
                    flashError(req, "Unknown action: " + action);
            }
        } catch (ValidationException | StockNotFoundException e) {
            flashError(req, e.getMessage());
        } catch (Exception e) {
            flashError(req, "Operation failed: " + e.getMessage());
        }

        redirect(req, resp, "/admin/stocks");
    }
}
