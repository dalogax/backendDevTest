package com.danivelasco.products.client;

import com.danivelasco.products.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ProductApiClientImpl implements ProductApiClient {

    private static final Logger log = LoggerFactory.getLogger(ProductApiClientImpl.class);

    private final WebClient productApiWebClient;

    public ProductApiClientImpl(WebClient productApiWebClient) {
        this.productApiWebClient = productApiWebClient;
    }

    @Override
    public Mono<List<String>> getSimilarProductIds(String productId) {
        return productApiWebClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                });
    }

    @Override
    public Mono<ProductResponse> getProductDetail(String productId) {
        return productApiWebClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }
}
