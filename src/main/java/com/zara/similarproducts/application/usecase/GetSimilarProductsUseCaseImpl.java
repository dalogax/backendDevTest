package com.zara.similarproducts.application.usecase;

import com.zara.similarproducts.application.port.in.GetSimilarProductsUseCase;
import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.domain.model.SimilarProducts;
import com.zara.similarproducts.domain.service.SimilarProductsDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GetSimilarProductsUseCaseImpl implements GetSimilarProductsUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GetSimilarProductsUseCaseImpl.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    
    private final SimilarProductsDomainService domainService;
    
    public GetSimilarProductsUseCaseImpl(SimilarProductsDomainService domainService) {
        this.domainService = domainService;
    }
    
    @Override
    public Mono<SimilarProducts> execute(ProductId productId) {
        logger.info("Executing GetSimilarProducts use case for product: {}", productId.value());
        
        return domainService.findSimilarProducts(productId)
                .timeout(TIMEOUT)
                .doOnSuccess(result -> logger.info("Found {} similar products for product: {}", 
                    result.size(), productId.value()))
                .doOnError(error -> logger.error("Error finding similar products for product: {}", 
                    productId.value(), error));
    }
}