package com.abracho.app.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(int code, String message) {
        super(code + " - " + message);
    }

    public ProductNotFoundException(String message, Exception e) {
        super(message, e);
    }

}
