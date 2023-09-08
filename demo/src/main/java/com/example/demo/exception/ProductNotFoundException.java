package com.example.demo.exception;

public class ProductNotFoundException extends RuntimeException {

    private static final String PRODUCT_NOT_FOUND = "Product Not found";

    public ProductNotFoundException() {
        super(PRODUCT_NOT_FOUND);
    }
}
