package com.inditex.similarproducts.product;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SimilarProductsController {
    private final SimilarProductsService service;

    public SimilarProductsController(SimilarProductsService service) {
        this.service = service;
    }

    @GetMapping(value = "/product/{productId}/similar", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ProductDetail>> similarProducts(@PathVariable String productId) {
        return service.findSimilar(productId);
    }
}
