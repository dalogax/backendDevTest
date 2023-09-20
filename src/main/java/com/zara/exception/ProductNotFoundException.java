package com.zara.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException{
    private String message;

    public ProductNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
