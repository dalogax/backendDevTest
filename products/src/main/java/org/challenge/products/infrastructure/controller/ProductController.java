package org.challenge.products.infrastructure.controller;

import org.challenge.products.application.port.in.GetSimilarProductsUseCase;
import org.challenge.products.infrastructure.dto.ProductResponseDto;
import org.challenge.products.infrastructure.mapper.ProductMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductMapper productMapper;

    public ProductController(GetSimilarProductsUseCase getSimilarProductsUseCase, ProductMapper productMapper) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
        this.productMapper = productMapper;
    }

    @GetMapping("/{productId}/similar")
    public List<ProductResponseDto> getSimilarProducts(@PathVariable String productId) {
        return getSimilarProductsUseCase.getSimilarProducts(productId)
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

}
