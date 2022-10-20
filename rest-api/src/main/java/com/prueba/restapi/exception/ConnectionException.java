package com.prueba.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class ConnectionException extends RuntimeException {

    public ConnectionException(String message) {
        super(message);
    }
}
