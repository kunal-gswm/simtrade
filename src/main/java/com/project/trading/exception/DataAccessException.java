package com.project.trading.exception;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, SQLException cause) {
        super(message, cause);
    }
}
