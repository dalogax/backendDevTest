package com.danivelasco.products.client;

import com.danivelasco.products.dto.ProductResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implementation of ProductApiClient that communicates with the external products API.
 * Uses Spring WebClient to perform non-blocking HTTP requests.
 */
@Component
public class ProductApiClientImpl implements ProductApiClient {

    private final WebClient productApiWebClient;

    /**
     * Constructor.
     *
     * @param productApiWebClient configured WebClient for external API calls
     */
    public ProductApiClientImpl(WebClient productApiWebClient) {
        this.productApiWebClient = productApiWebClient;
    }

    /**
     * Retrieves IDs of similar products for a given product.
     *
     * @param productId product identifier
     * @return Mono emitting a list of similar product IDs
     */
    @Override
    public Mono<List<String>> getSimilarProductIds(String productId) {
        return productApiWebClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves detailed information of a product.
     *
     * @param productId product identifier
     * @return Mono emitting product details
     */
    @Override
    public Mono<ProductResponse> getProductDetail(String productId) {
        return productApiWebClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }
}
