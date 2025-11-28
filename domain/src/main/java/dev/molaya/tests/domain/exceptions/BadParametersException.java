package dev.molaya.tests.domain.exceptions;

public class BadParametersException extends RuntimeException {
    public BadParametersException(String message) {
        super(message);
    }
}
