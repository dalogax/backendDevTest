package org.challenge.products.domain.exception;

import org.slf4j.helpers.MessageFormatter;

public class ProductNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Product with id %s not found. Response body: %s";

    public ProductNotFoundException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public ProductNotFoundException(String id, String responseBody) {
        super(ERROR_MESSAGE.formatted(id, responseBody));
    }


}