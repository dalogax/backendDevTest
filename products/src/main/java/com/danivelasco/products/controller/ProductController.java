package com.danivelasco.products.controller;

import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.dto.ProductResponse;
import com.danivelasco.products.service.SimilarProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final SimilarProductService similarProductService;

    public ProductController(SimilarProductService similarProductService) {
        this.similarProductService = similarProductService;
    }

    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<ProductDetailsResponse>> getSimilarProducts(@PathVariable String productId) {
        return this.similarProductService.getSimilarProducts(productId)
                .map(ResponseEntity::ok);
    }
}
