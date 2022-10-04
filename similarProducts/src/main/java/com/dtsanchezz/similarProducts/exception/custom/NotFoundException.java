package com.dtsanchezz.similarProducts.exception.custom;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Product not found");
    }

}
