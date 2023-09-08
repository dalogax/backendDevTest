package com.example.demo.exception;

import lombok.Getter;

@Getter
public class HttpCallException extends RuntimeException {

    private final int statusCode;

    private static final String PRODUCT_INVALID_FORMAT = "Error in the external service call: %s";

    public HttpCallException(int statusCode, String message) {
        super(String.format(PRODUCT_INVALID_FORMAT, message));
        this.statusCode = statusCode;
    }

}
