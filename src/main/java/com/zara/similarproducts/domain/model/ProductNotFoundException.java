package com.zara.similarproducts.domain.model;

public class ProductNotFoundException extends DomainException {
    
    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";
    private final ProductId productId;
    
    public ProductNotFoundException(ProductId productId) {
        super("Product not found: " + productId.value());
        this.productId = productId;
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}