package com.globant.interview.david.msdavidmobilephone.infrastructure.input.rest.controller;

import com.globant.interview.david.msdavidmobilephone.application.usecase.GetSimilarProductsUseCase;
import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.model.SimilarProducts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "product.repository.impl", havingValue = "feign", matchIfMissing = true)
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(@PathVariable String productId) {
        log.info("Received request for similar products of productId: {}", productId);
        SimilarProducts similarProducts = getSimilarProductsUseCase.execute(productId);
        return ResponseEntity.ok(similarProducts.products());
    }
}
