package com.pablolizarraga.solution.application.exception;

import lombok.Getter;

@Getter
public class ErrorMessage {

    private final String exception;
    private final String message;
    private final String path;

    public ErrorMessage(Exception exception, String path) {
        this.exception = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.path = path;
    }
}
