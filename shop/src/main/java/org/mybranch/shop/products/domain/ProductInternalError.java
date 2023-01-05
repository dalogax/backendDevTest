package org.mybranch.shop.products.domain;

public final class ProductInternalError extends RuntimeException {

    public ProductInternalError(String message) {
        super(message);
    }

    public ProductInternalError(String message, Throwable innerException) {
        super(message, innerException);
    }


}
