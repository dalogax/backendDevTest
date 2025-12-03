package com.zara.similarproducts.infrastructure.adapter.in.rest.controller;

import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.application.port.in.GetSimilarProductsUseCase;
import com.zara.similarproducts.domain.model.ProductNotFoundException;
import com.zara.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import com.zara.similarproducts.infrastructure.adapter.in.rest.mapper.SimilarProductsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {
    
    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsController.class);
    
    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final SimilarProductsMapper mapper;
    
    public SimilarProductsController(
            GetSimilarProductsUseCase getSimilarProductsUseCase,
            SimilarProductsMapper mapper) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
        this.mapper = mapper;
    }
    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDetailResponse>>> getSimilarProducts(
            @PathVariable String productId) {

        logger.info("Received request for similar products of product: {}", productId);

        return getSimilarProductsUseCase.execute(ProductId.of(productId))
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> logger.info("Returning {} similar products for product: {}",
                        response.getBody().size(), productId))
                .doOnError(ex -> logger.error("Error fetching similar products for {}", productId, ex))
                .onErrorResume(ProductNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.notFound().build())
                );
    }

}