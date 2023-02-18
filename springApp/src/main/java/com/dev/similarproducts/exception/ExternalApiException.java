package com.dev.similarproducts.exception;

import org.springframework.http.HttpStatus;

public class ExternalApiException extends RuntimeException {

    private final HttpStatus statusCode;

    public ExternalApiException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}