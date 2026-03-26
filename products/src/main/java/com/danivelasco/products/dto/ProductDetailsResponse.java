package com.danivelasco.products.dto;

import java.util.List;

public record ProductDetailsResponse(
        List<ProductResponse> products,
        List<ProductDetailFailure> failures) {
}
