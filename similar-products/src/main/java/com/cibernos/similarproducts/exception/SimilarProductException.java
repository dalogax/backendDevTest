package com.cibernos.similarproducts.exception;

import org.springframework.http.HttpStatus;

public class SimilarProductException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus status;

    public SimilarProductException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
