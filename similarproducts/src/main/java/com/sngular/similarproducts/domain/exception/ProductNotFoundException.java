package com.sngular.similarproducts.domain.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("Product not found for productId: %s".formatted(productId));
    }

    public ProductNotFoundException(String productId, Throwable cause) {
        super("Product not found for productId: %s".formatted(productId), cause);
    }
}
