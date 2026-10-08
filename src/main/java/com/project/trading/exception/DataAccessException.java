package com.project.trading.exception;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, SQLException cause) {
        super(message, cause);
    }
}
