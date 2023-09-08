package com.example.demo.http.dto;

import com.example.demo.core.domain.Product;
import com.example.demo.core.use_case.GetSimilarProducts;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class SimilarProductsResponse {

    private final Set<ProductResponse> similarProducts;

    public SimilarProductsResponse(GetSimilarProducts.Response response) {
        similarProducts = response.getSimilarProducts()
                .stream()
                .map(SimilarProductsResponse::apply)
                .collect(Collectors.toSet());
    }

    private static ProductResponse apply(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .name(product.getName())
                .availability(product.isAvailability())
                .build();
    }
}
