package com.example.demo.core.infrastructure.repository;

import com.example.demo.core.domain.Product;
import com.example.demo.core.domain.ProductRepository;
import com.example.demo.core.infrastructure.client.ProductClient;
import com.example.demo.exception.HttpCallException;
import com.example.demo.exception.ProductNotFoundException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository("productRepository")
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductClient productClient;

    public ProductRepositoryAdapter(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public Set<String> getSimilarProductIDs(String productId) {
        Set<String> similarProductIDs = new HashSet<>();
        try {
            similarProductIDs = productClient.getSimilarProductIDs(productId);
        } catch (FeignException feignClientException) {
            handleFeignException(feignClientException);
        }
        return similarProductIDs;
    }

    @Override
    public Product getProduct(String productId) {
        Product product = Product.builder().build();
        try {
            product = productClient.getProduct(productId);
        } catch (FeignException feignException) {
            handleFeignException(feignException);
        }
        return product;
    }

    private static void handleFeignException(FeignException feignException) {
        if (HttpStatus.NOT_FOUND.value() == feignException.status()) {
            throw new ProductNotFoundException();
        }
        throw new HttpCallException(feignException.status(), feignException.getMessage());
    }
}
