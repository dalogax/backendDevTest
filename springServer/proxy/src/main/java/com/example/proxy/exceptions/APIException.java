package com.example.proxy.exceptions;

import lombok.Data;

public class APIException extends RuntimeException {
    private Integer status;

    public APIException(String message, Integer status) {
        super(message);
        this.status = status;
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

    public Integer getStatus() {
        return status;
    }
}
