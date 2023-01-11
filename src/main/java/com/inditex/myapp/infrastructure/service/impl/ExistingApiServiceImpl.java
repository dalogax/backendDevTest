package com.inditex.myapp.infrastructure.service.impl;

import com.inditex.myapp.application.model.ProductDetailResponse;
import com.inditex.myapp.domain.model.ProductDetail;
import com.inditex.myapp.domain.service.ExistingApiService;

import com.inditex.myapp.infrastructure.mapper.InputProductDetailMapper;
import com.inditex.myapp.infrastructure.rest.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ExistingApiServiceImpl implements ExistingApiService {

    @Autowired
    private DefaultApi defaultApi;

    @Autowired
    private InputProductDetailMapper inputProductDetailMapper;

    @Override
    public ProductDetail getProduct(String productId) {
        ProductDetailResponse productDetailResponse = defaultApi.getProductProductId(productId);
        return inputProductDetailMapper.map(productDetailResponse);
    }

    @Override
    public List<String> getSimilarProducts(String productId) {
        Set<String> productSimilaridSet = defaultApi.getProductSimilarids(productId);
        return productSimilaridSet.stream().toList();
    }
}
