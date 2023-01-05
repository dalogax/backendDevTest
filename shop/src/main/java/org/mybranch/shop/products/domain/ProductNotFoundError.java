package org.mybranch.shop.products.domain;

public final class ProductNotFoundError extends RuntimeException {

    public ProductNotFoundError(String message) {
        super(message);
    }

    public ProductNotFoundError(String message, Throwable innerException) {
        super(message, innerException);
    }
}
