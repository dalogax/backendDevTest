package com.zara.similarproducts.infrastructure.adapter.out.external;

import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.application.port.out.SimilarProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
public class ExternalSimilarProductsRepositoryAdapter implements SimilarProductsRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalSimilarProductsRepositoryAdapter.class);
    
    private final WebClient webClient;
    private final Duration timeout;
    
    public ExternalSimilarProductsRepositoryAdapter(
            WebClient webClient,
            @Value("${app.external-api.timeout.similar-ids:2s}") Duration timeout) {
        this.webClient = webClient;
        this.timeout = timeout;
    }
    
    @Override
    public Flux<ProductId> findSimilarProductIds(ProductId productId) {
        logger.debug("Fetching similar product IDs for product: {}", productId.value());
        
        return webClient.get()
                .uri("/product/{productId}/similarids", productId.value())
                .retrieve()
                .bodyToMono(String[].class)
                .timeout(timeout)
                .flatMapMany(ids -> Flux.fromArray(ids))
                .map(ProductId::of)
                .doOnNext(id -> logger.debug("Found similar product ID: {}", id.value()))
                .doOnError(error -> logger.warn("Error fetching similar product IDs for product {}: {}", 
                    productId.value(), error.getMessage()))
                .onErrorResume(org.springframework.web.reactive.function.client.WebClientResponseException.NotFound.class,
                    ex -> {
                        logger.debug("Product {} not found, returning empty similar products", productId.value());
                        return Flux.empty();
                    });
    }
}