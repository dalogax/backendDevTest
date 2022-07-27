package com.abracho.app.exception;

public class ProductException extends RuntimeException {

    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductException(String message) {
        super(message);
    }

    public ProductException(int code, String message) {
        super(String.valueOf(code) + " - " + message);
    }

    public ProductException(String code, String message) {
        super(code + " - " + message);
    }

    public ProductException(String message, Exception e) {
        super(message, e);
    }

}
