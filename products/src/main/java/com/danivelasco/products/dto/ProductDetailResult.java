package com.danivelasco.products.dto;

public record ProductDetailResult(
        ProductResponse product,
        ProductDetailFailure failure
) {
    public static ProductDetailResult success(ProductResponse product) {
        return new ProductDetailResult(product, null);
    }

    public static ProductDetailResult failure(ProductDetailFailure failure) {
        return new ProductDetailResult(null, failure);
    }

    public boolean isSuccess() {
        return product != null;
    }
}
