package com.project.trading.exception;

public class StockNotFoundException extends AppException {
    private static final long serialVersionUID = 1L;
    public StockNotFoundException(String message) {
        super(message);
    }
}
