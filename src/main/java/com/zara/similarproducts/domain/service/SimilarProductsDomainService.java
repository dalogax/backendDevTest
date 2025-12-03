package com.zara.similarproducts.domain.service;

import com.zara.similarproducts.domain.model.ExternalServiceException;
import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.domain.model.ProductNotFoundException;
import com.zara.similarproducts.domain.model.SimilarProducts;
import com.zara.similarproducts.application.port.out.ProductRepository;
import com.zara.similarproducts.application.port.out.SimilarProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

public class SimilarProductsDomainService {
    
    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsDomainService.class);
    
    private final SimilarProductsRepository similarProductsRepository;
    private final ProductRepository productRepository;
    
    public SimilarProductsDomainService(
            SimilarProductsRepository similarProductsRepository,
            ProductRepository productRepository) {
        this.similarProductsRepository = similarProductsRepository;
        this.productRepository = productRepository;
    }
    
    public Mono<SimilarProducts> findSimilarProducts(ProductId productId) {
        logger.debug("Finding similar products for: {}", productId.value());
        
        return similarProductsRepository.findSimilarProductIds(productId)
                .collectList()
                .onErrorMap(TimeoutException.class, ex -> 
                    new ExternalServiceException("Timeout retrieving similar product IDs", ex))
                .onErrorMap(Exception.class, ex -> 
                    new ExternalServiceException("Error retrieving similar product IDs", ex))
                .flatMap(ids -> {
                    if (ids.isEmpty()) {
                        logger.debug("No similar products found for: {}", productId.value());
                        return Mono.error(new ProductNotFoundException(productId));
                    }
                    
                    logger.debug("Found {} similar product IDs for: {}", ids.size(), productId.value());
                    return Flux.fromIterable(ids)
                            .flatMap(this::findProductWithErrorHandling)
                            .collectList()
                            .map(SimilarProducts::of);
                });
    }
    
    private Mono<Product> findProductWithErrorHandling(ProductId productId) {
        return productRepository.findById(productId)
                .onErrorResume(TimeoutException.class, ex -> {
                    logger.warn("Timeout retrieving product details for: {}", productId.value());
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.warn("Error retrieving product details for: {}", productId.value(), ex);
                    return Mono.empty();
                });
    }
}