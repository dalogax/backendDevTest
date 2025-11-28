package dev.molaya.tests.domain.exceptions;

public class IntegrationException extends RuntimeException {
    public IntegrationException(String message, Throwable e) {
        super(message, e);
    }
}
