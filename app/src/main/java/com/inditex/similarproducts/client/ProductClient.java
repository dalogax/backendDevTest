package com.inditex.similarproducts.client;

import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class ProductClient {

    private final WebClient webClient;
    private final Duration detailTimeout;
    private final Duration similarIdsTimeout;

    public ProductClient(
            WebClient productWebClient,
            @Value("${product-api.detail-timeout-ms:2000}") long detailTimeoutMs,
            @Value("${product-api.similar-ids-timeout-ms:2000}") long similarIdsTimeoutMs) {
        this.webClient = productWebClient;
        this.detailTimeout = Duration.ofMillis(detailTimeoutMs);
        this.similarIdsTimeout = Duration.ofMillis(similarIdsTimeoutMs);
    }

    public Mono<List<String>> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody()
                                .then(Mono.error(new ProductNotFoundException(productId)));
                    }
                    if (response.statusCode().isError()) {
                        return response.releaseBody()
                                .then(Mono.error(new RuntimeException("Upstream error fetching similar IDs for product " + productId)));
                    }
                    return response.bodyToMono(new ParameterizedTypeReference<List<Object>>() {})
                            .map(ids -> ids.stream().map(Object::toString).toList());
                })
                // Bound the entry-point call so a stalled upstream can't hang the whole request.
                .timeout(similarIdsTimeout);
    }

    // Non-2xx responses and timeouts resolve to Mono.empty() — the product is silently skipped.
    public Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ProductDetail.class);
                    }
                    return response.releaseBody().then(Mono.empty());
                })
                .timeout(detailTimeout)
                .onErrorResume(e -> Mono.empty());
    }
}
