package com.example.demo.service;

import com.example.demo.client.SimilarProductsClient;
import com.example.demo.model.Product;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final SimilarProductsClient similarProductsClient;

    public ProductService(SimilarProductsClient similarProductsClient) {
        this.similarProductsClient = similarProductsClient;
    }

    public Set<Product> getSimilarProducts(String productId) {
        return similarProductsClient.getSimilarProductIDs(productId)
                .stream()
                .map(similarProductsClient::getProduct)
                .collect(Collectors.toSet());
    }
}
