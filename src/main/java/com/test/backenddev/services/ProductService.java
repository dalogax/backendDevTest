package com.test.backenddev.services;

import com.test.backenddev.client.SimilarProductsClient;
import com.test.backenddev.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final SimilarProductsClient similarProductsClient;

    public ProductService(SimilarProductsClient similarProductsClient) {
        this.similarProductsClient = similarProductsClient;
    }

    public List<Product> getSimilarProducts(String productId) {
        return similarProductsClient.getSimilarProductIds(productId)
                .stream()
                .map(similarProductsClient::getProductById)
                .collect(Collectors.toList());
    }

}
