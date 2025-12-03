package com.zara.similarproducts.infrastructure.adapter.in.rest.mapper;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.SimilarProducts;
import com.zara.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimilarProductsMapper {
    
    public List<ProductDetailResponse> toResponse(SimilarProducts similarProducts) {
        return similarProducts.products().stream()
                .map(this::toProductDetailResponse)
                .toList();
    }
    
    private ProductDetailResponse toProductDetailResponse(Product product) {
        return new ProductDetailResponse(
                product.id().value(),
                product.name(),
                product.price(),
                product.availability()
        );
    }
}