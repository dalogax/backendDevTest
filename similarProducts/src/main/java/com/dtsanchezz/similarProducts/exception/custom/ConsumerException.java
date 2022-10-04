package com.dtsanchezz.similarProducts.exception.custom;

public class ConsumerException extends RuntimeException {

    public ConsumerException() {
        super("An unexpected error was thrown by the consumer");
    }

    public ConsumerException(final String message) {
        super(message);
    }
}
