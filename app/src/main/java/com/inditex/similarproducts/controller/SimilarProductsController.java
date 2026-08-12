package com.inditex.similarproducts.controller;

import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import com.inditex.similarproducts.service.SimilarProductsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/product/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDetail>>> getSimilarProducts(@PathVariable String productId) {
        return similarProductsService.getSimilarProducts(productId)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(ProductNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
