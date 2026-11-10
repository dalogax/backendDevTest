package com.inditex.similarproducts.catalog;

public class InvalidCatalogResponseException extends RuntimeException {
    public InvalidCatalogResponseException(String message) {
        super(message);
    }
}
