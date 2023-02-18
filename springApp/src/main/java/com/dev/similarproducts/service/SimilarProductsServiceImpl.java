package com.dev.similarproducts.service;

import com.dev.similarproducts.api.ExternalApiClient;
import com.dev.similarproducts.dto.ProductDetailDto;
import com.dev.similarproducts.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimilarProductsServiceImpl implements SimilarProductsService {
    private final ExternalApiClient externalApiClient;

    @Autowired
    public SimilarProductsServiceImpl(ExternalApiClient externalApiClient) {
        this.externalApiClient = externalApiClient;
    }

    @Override
    public List<ProductDetailDto> getSimilarProducts(String productId) throws ExternalApiException {
        List<String> similarProductIds = externalApiClient.getSimilarProductIds(productId);
        return similarProductIds.stream().map(externalApiClient::getProductDetail).collect(Collectors.toList());
    }
}

