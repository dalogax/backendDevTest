package com.zara.similarproducts.infrastructure.adapter.out.external;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.application.port.out.ProductRepository;
import com.zara.similarproducts.infrastructure.adapter.out.external.dto.ExternalProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ExternalProductRepositoryAdapter implements ProductRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalProductRepositoryAdapter.class);
    
    private final WebClient webClient;
    private final Duration timeout;
    private final ExternalProductMapper mapper;
    
    public ExternalProductRepositoryAdapter(
            WebClient webClient,
            @Value("${app.external-api.timeout.product-detail:8s}") Duration timeout,
            ExternalProductMapper mapper) {
        this.webClient = webClient;
        this.timeout = timeout;
        this.mapper = mapper;
    }
    
    @Override
    public Mono<Product> findById(ProductId productId) {
        logger.debug("Fetching product details for product: {}", productId.value());
        
        return webClient.get()
                .uri("/product/{productId}", productId.value())
                .retrieve()
                .bodyToMono(ExternalProductResponse.class)
                .timeout(timeout)
                .map(mapper::toDomain)
                .doOnNext(product -> logger.debug("Found product: {}", product.id().value()))
                .doOnError(WebClientResponseException.NotFound.class, 
                    error -> logger.debug("Product not found: {}", productId.value()))
                .doOnError(error -> !(error instanceof WebClientResponseException.NotFound), 
                    error -> logger.warn("Error fetching product {}: {}", productId.value(), error.getMessage()));
    }
}