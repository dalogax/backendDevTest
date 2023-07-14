package com.alejandro.api.service.impl;

import com.alejandro.api.dto.ProductDetailDTO;
import com.alejandro.api.exception.ProductNotFoundException;
import com.alejandro.api.service.IProductsService;
import com.alejandro.api.service.ISimilarProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimilarProductsServiceImpl implements ISimilarProductsService {

    private final IProductsService productsService;

    @Override
    public List<ProductDetailDTO> getSimilarProducts(String productId) {
        return getSimilarProductIds(productId)
                .stream()
                .map(this::getProductDetail)
                .collect(Collectors.toList());
    }

    private List<String> getSimilarProductIds(String productId) {
        return productsService
                .getSimilarProductIds(productId)
                .orElseThrow(ProductNotFoundException::new);
    }

    private ProductDetailDTO getProductDetail(String productId) {
        return productsService
                .getProductDetail(productId)
                .orElseThrow(ProductNotFoundException::new);
    }
}