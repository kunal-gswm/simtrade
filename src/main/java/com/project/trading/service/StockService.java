package com.project.trading.service;

import com.project.trading.dao.StockDAO;
import com.project.trading.exception.DataAccessException;
import com.project.trading.exception.StockNotFoundException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.Stock;
import com.project.trading.util.DBConnection;
import com.project.trading.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StockService {

    private final StockDAO stockDAO;

    public StockService() {
        this.stockDAO = new StockDAO();
    }

    public StockService(StockDAO stockDAO) {
        this.stockDAO = stockDAO;
    }

    public List<Stock> listActive() {
        try (Connection c = DBConnection.getConnection()) {
            return stockDAO.findAllActive(c);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch active stocks.", e);
        }
    }

    public List<Stock> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            return listActive();
        }
        String trimmed = q.trim();
        if (trimmed.length() > 50) {
            trimmed = trimmed.substring(0, 50);
        }
        try (Connection c = DBConnection.getConnection()) {
            return stockDAO.search(c, trimmed);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search stocks.", e);
        }
    }

    public Stock getById(long id) throws StockNotFoundException {
        try (Connection c = DBConnection.getConnection()) {
            Stock stock = stockDAO.findById(c, id);
            if (stock == null) {
                throw new StockNotFoundException("Stock not found with ID: " + id);
            }
            return stock;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch stock with ID: " + id, e);
        }
    }

    public List<Stock> listAll() {
        try (Connection c = DBConnection.getConnection()) {
            return stockDAO.findAll(c);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch all stocks.", e);
        }
    }

    public Stock add(String symbol, String name, String sector, String desc, BigDecimal price) throws ValidationException {
        Validator.requireNonBlank(symbol, "Stock symbol");
        Validator.requireNonBlank(name, "Company name");
        String cleanSymbol = symbol.trim().toUpperCase();
        Validator.matches(cleanSymbol, "^[A-Z0-9&-]{1,15}$", "Symbol must be 1-15 uppercase characters [A-Z0-9&-].");

        if (name.trim().length() > 100) {
            throw new ValidationException("Company name cannot exceed 100 characters.");
        }
        if (sector != null && sector.trim().length() > 50) {
            throw new ValidationException("Sector cannot exceed 50 characters.");
        }
        if (desc != null && desc.trim().length() > 500) {
            throw new ValidationException("Description cannot exceed 500 characters.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than zero.");
        }
        if (price.compareTo(new BigDecimal("9999999.99")) > 0) {
            throw new ValidationException("Price cannot exceed 9,999,999.99.");
        }

        BigDecimal scaledPrice = price.setScale(2, RoundingMode.HALF_UP);

        try (Connection c = DBConnection.getConnection()) {
            if (stockDAO.findBySymbol(c, cleanSymbol) != null) {
                throw new ValidationException("Stock with symbol " + cleanSymbol + " already exists.");
            }

            Stock stock = new Stock();
            stock.setSymbol(cleanSymbol);
            stock.setCompanyName(name.trim());
            stock.setSector(sector != null ? sector.trim() : null);
            stock.setDescription(desc != null ? desc.trim() : null);
            stock.setPrice(scaledPrice);
            stock.setPrevPrice(scaledPrice);
            stock.setActive(true);

            long id = stockDAO.insert(c, stock);
            stock.setId(id);
            return stock;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create stock: " + cleanSymbol, e);
        }
    }

    public void update(long id, String name, String sector, String desc) throws ValidationException, StockNotFoundException {
        Validator.requireNonBlank(name, "Company name");
        if (name.trim().length() > 100) {
            throw new ValidationException("Company name cannot exceed 100 characters.");
        }
        if (sector != null && sector.trim().length() > 50) {
            throw new ValidationException("Sector cannot exceed 50 characters.");
        }
        if (desc != null && desc.trim().length() > 500) {
            throw new ValidationException("Description cannot exceed 500 characters.");
        }

        try (Connection c = DBConnection.getConnection()) {
            Stock existing = stockDAO.findById(c, id);
            if (existing == null) {
                throw new StockNotFoundException("Stock not found with ID: " + id);
            }

            existing.setCompanyName(name.trim());
            existing.setSector(sector != null ? sector.trim() : null);
            existing.setDescription(desc != null ? desc.trim() : null);
            stockDAO.update(c, existing);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update stock with ID: " + id, e);
        }
    }

    public void setActive(long id, boolean active) throws StockNotFoundException {
        try (Connection c = DBConnection.getConnection()) {
            Stock existing = stockDAO.findById(c, id);
            if (existing == null) {
                throw new StockNotFoundException("Stock not found with ID: " + id);
            }
            stockDAO.setActive(c, id, active);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update active status for stock with ID: " + id, e);
        }
    }

    public void updatePrice(long id, BigDecimal price) throws ValidationException, StockNotFoundException {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than zero.");
        }
        if (price.compareTo(new BigDecimal("9999999.99")) > 0) {
            throw new ValidationException("Price cannot exceed 9,999,999.99.");
        }

        BigDecimal scaledPrice = price.setScale(2, RoundingMode.HALF_UP);

        try (Connection c = DBConnection.getConnection()) {
            Stock existing = stockDAO.findById(c, id);
            if (existing == null) {
                throw new StockNotFoundException("Stock not found with ID: " + id);
            }
            stockDAO.updatePrice(c, id, scaledPrice);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update price for stock with ID: " + id, e);
        }
    }
}
