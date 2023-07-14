package com.alejandro.api.repository.impl;

import com.alejandro.api.model.ProductDetailEntity;
import com.alejandro.api.repository.IProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductsRepositoryImpl implements IProductsRepository {

    private static final String SIMILAR_PRODUCT_IDS_URL = "http://localhost:3001/product/{productId}/similarids";
    private static final String PRODUCT_DETAIL_URL = "http://localhost:3001/product/{productId}";

    private final RestTemplate productsRestTemplate;

    @Override
    public Optional<List<String>> getSimilarProductIds(String productId) {
        try {
            ResponseEntity<List<String>> response = productsRestTemplate.exchange(
                    SIMILAR_PRODUCT_IDS_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() { },
                    productId
            );

            return Optional.ofNullable(response.getBody());
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ProductDetailEntity> getProductDetail(String productId) {
        try {
            ResponseEntity<ProductDetailEntity> response = productsRestTemplate.exchange(
                    PRODUCT_DETAIL_URL,
                    HttpMethod.GET,
                    null,
                    ProductDetailEntity.class,
                    productId
            );

            return Optional.ofNullable(response.getBody());
        } catch (RuntimeException notFoundException) {
            return Optional.empty();
        }
    }
}
