package com.inditex.similarproducts.catalog;

import com.inditex.similarproducts.product.ProductDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CatalogClient {
    private final WebClient webClient;
    private final CatalogProperties properties;

    public CatalogClient(WebClient catalogWebClient, CatalogProperties properties) {
        this.webClient = catalogWebClient;
        this.properties = properties;
    }

    public Mono<String[]> similarIds(String productId) {
        // Decode the whole array before fan-out: malformed lists must not produce partial results.
        // String decoding also accepts the numeric IDs used by the supplied mocks.
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .bodyToMono(String[].class)
                .switchIfEmpty(Mono.error(new InvalidCatalogResponseException("Catalog returned an empty body")))
                .map(ids -> {
                    for (String id : ids) {
                        if (id == null || id.isBlank()) {
                            throw new InvalidCatalogResponseException("Catalog returned an invalid product ID");
                        }
                    }
                    return ids;
                })
                .timeout(properties.requestTimeout());
    }

    public Mono<ProductDetail> product(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .timeout(properties.requestTimeout());
    }
}
