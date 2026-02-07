package com.globant.interview.david.msdavidmobilephone.application.usecase;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.repository.ReactiveProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "product.repository.impl", havingValue = "webflux", matchIfMissing = false)
public class ReactiveGetSimilarProductsUseCase {

    private final ReactiveProductRepository reactiveProductRepository;

    @Cacheable(value = "similarProducts", key = "#productId")
    public Mono<List<Product>> execute(String productId) {
        log.info("Getting similar products for productId: {} using Reactive WebFlux", productId);

        return reactiveProductRepository.getSimilarProductIds(productId)
                .collectList()
                .filter(ids -> !ids.isEmpty())
                .flatMapMany(ids -> {
                    log.info("Found {} similar ids for productId: {}", ids.size(), productId);
                    // Fetch product details in parallel with concurrency of 16
                    return Flux.fromIterable(ids)
                            .flatMap(reactiveProductRepository::getProductDetail, 16);
                })
                .collectList()
                .doOnNext(products -> log.info("Found {} similar products for productId: {}", products.size(), productId));
    }
}
