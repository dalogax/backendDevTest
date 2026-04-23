package org.challenge.products.application.exception;

public class ExternalServiceException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Error occurred while calling external service: %s. Response body: %s";

    public ExternalServiceException(String message, String responseBody) {
        super(ERROR_MESSAGE.formatted(message, responseBody));
    }

}
