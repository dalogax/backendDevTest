package com.sngular.similarproducts.infrastructure.outbound;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.sngular.similarproducts.application.outbound.ProductsPort;
import com.sngular.similarproducts.domain.ProductDetail;
import com.sngular.similarproducts.domain.exception.ProductNotFoundException;
import com.sngular.similarproducts.domain.exception.SimilarProductsNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductsClientAdapter implements ProductsPort {

    private static final ParameterizedTypeReference<List<String>> STRING_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;

    public ProductsClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${app.product-service.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Set<String> getSimilarProductIds(String productId) {
        log.debug("Calling GET /product/{}/similarids", productId);
        try {
            List<String> ids = restClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .body(STRING_LIST_TYPE);

            return ids == null ? Set.of() : new LinkedHashSet<>(ids);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Similar IDs not found for productId {}", productId, ex);
            throw new SimilarProductsNotFoundException(productId, ex);
        }
    }

    @Override
    public ProductDetail getProduct(String productId) {
        log.debug("Calling GET /product/{}", productId);
        try {
            return restClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .body(ProductDetail.class);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Product detail not found for productId {}", productId, ex);
            throw new ProductNotFoundException(productId, ex);
        }
    }

}