package com.inditex.myapp.domain.service.impl;

import com.inditex.myapp.domain.model.ProductDetail;
import com.inditex.myapp.domain.service.ExistingApiService;
import com.inditex.myapp.domain.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ExistingApiService existingApiService;

    public ProductServiceImpl(ExistingApiService existingApiService) {
        this.existingApiService = existingApiService;
    }

    @Override
    public List<ProductDetail> productSimilar(String productId) {
        List<String> similarProductIds = getSimilarProductIds(productId);
        return getProductDetail(similarProductIds);
    }

    private List<String> getSimilarProductIds(String productId) {
        return existingApiService.getSimilarProducts(productId);
    }

    private ProductDetail getProductDetail(String productId) {
        return existingApiService.getProduct(productId);
    }

    private List<ProductDetail> getProductDetail(List<String> productIdList) {
        return productIdList.stream()
                .map(this::getProductDetail)
                .collect(Collectors.toList());
    }
}
