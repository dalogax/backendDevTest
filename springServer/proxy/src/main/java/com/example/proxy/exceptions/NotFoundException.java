package com.example.proxy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends APIException {
    public NotFoundException(String description) {
        super(description, HttpStatus.NOT_FOUND.value());
    }
}
