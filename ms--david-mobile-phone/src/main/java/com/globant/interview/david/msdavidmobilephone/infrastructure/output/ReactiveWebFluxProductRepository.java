package com.globant.interview.david.msdavidmobilephone.infrastructure.output;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.repository.ReactiveProductRepository;
import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.WebFluxProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "product.repository.impl", havingValue = "webflux", matchIfMissing = false)
public class ReactiveWebFluxProductRepository implements ReactiveProductRepository {

    private final WebFluxProductClient webFluxProductClient;

    @Override
    public Flux<String> getSimilarProductIds(String productId) {
        log.info("Fetching similar product ids for productId: {} using Reactive WebFlux", productId);
        return webFluxProductClient.getSimilarIds(productId)
                .doOnError(e -> log.warn("Error fetching similar ids for productId: {}", productId, e))
                .onErrorResume(e -> Flux.empty());
    }

    @Override
    public Mono<Product> getProductDetail(String productId) {
        log.info("Fetching product detail for productId: {} using Reactive WebFlux", productId);
        return webFluxProductClient.getProductDetail(productId)
                .map(response -> new Product(
                        response.id(),
                        response.name(),
                        response.price(),
                        response.availability()
                ))
                .doOnError(e -> log.warn("Product not found for productId: {}", productId))
                .onErrorResume(e -> Mono.empty());
    }
}
