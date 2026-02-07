package com.globant.interview.david.msdavidmobilephone.infrastructure.input.rest.controller;

import com.globant.interview.david.msdavidmobilephone.application.usecase.ReactiveGetSimilarProductsUseCase;
import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "product.repository.impl", havingValue = "webflux", matchIfMissing = false)
public class ReactiveProductController {

    private final ReactiveGetSimilarProductsUseCase reactiveGetSimilarProductsUseCase;

    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<List<Product>>> getSimilarProducts(@PathVariable String productId) {
        log.info("Received request for similar products of productId: {} using Reactive WebFlux", productId);
        return reactiveGetSimilarProductsUseCase.execute(productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }
}
