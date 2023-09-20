package com.zara.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ProductServerException extends RuntimeException{
    private String message;

    public ProductServerException(String message) {
        super(message);
        this.message = message;
    }
}
