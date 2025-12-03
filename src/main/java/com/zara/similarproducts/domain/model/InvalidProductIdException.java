package com.zara.similarproducts.domain.model;

public class InvalidProductIdException extends DomainException {
    
    private static final String ERROR_CODE = "INVALID_PRODUCT_ID";
    private final String productId;
    
    public InvalidProductIdException(String productId) {
        super("Invalid product ID format: " + productId);
        this.productId = productId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}