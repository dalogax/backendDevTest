package com.capitole.similarproducts.domain.exception;

/**
 * Domain exception thrown when a product is not found.
 */
public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super(String.format("Product not found with ID: %s", productId));
        this.productId = productId;
    }

    public ProductNotFoundException(String productId, Throwable cause) {
        super(String.format("Product not found with ID: %s", productId), cause);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
