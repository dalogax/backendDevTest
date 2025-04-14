package com.nunegal.backendDevTest.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super("Producto no encontrado con ID: " + productId);
    }
}
