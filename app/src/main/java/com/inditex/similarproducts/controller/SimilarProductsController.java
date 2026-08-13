package com.inditex.similarproducts.controller;

import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import com.inditex.similarproducts.service.SimilarProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class SimilarProductsController {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductsController.class);

    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/product/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDetail>>> getSimilarProducts(@PathVariable String productId) {
        long startedAt = System.nanoTime();
        return similarProductsService.getSimilarProducts(productId)
                .collectList()
                .doOnNext(products -> logResolved(productId, products.size(), startedAt))
                .map(ResponseEntity::ok)
                .onErrorResume(ProductNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()))
                // Placed after the 404 mapping, so only genuine failures reach it. A request that
                // gets this far returns a 5xx to the client, which is always worth a line.
                .doOnError(error -> log.warn("Failed to resolve similar products for product {}: {}",
                        productId, error.toString()));
    }

    private static void logResolved(String productId, int resolved, long startedAt) {
        if (log.isDebugEnabled()) {
            log.debug("Resolved {} similar products for product {} in {}ms",
                    resolved, productId, (System.nanoTime() - startedAt) / 1_000_000);
        }
    }
}
