package org.mybranch.shop.products.domain;

public final class ProductBadRequestError extends RuntimeException {

    public ProductBadRequestError(String message) {
        super(message);
    }

    public ProductBadRequestError(String message, Throwable innerException) {
        super(message, innerException);
    }

}
