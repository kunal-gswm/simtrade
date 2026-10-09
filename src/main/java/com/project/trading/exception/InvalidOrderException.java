package com.project.trading.exception;

public class InvalidOrderException extends AppException {
    private static final long serialVersionUID = 1L;
    public InvalidOrderException(String message) {
        super(message);
    }
}
