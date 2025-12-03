package com.zara.similarproducts.infrastructure.config;

import com.zara.similarproducts.application.port.out.ProductRepository;
import com.zara.similarproducts.application.port.out.SimilarProductsRepository;
import com.zara.similarproducts.domain.service.SimilarProductsDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {
    
    @Bean
    public SimilarProductsDomainService similarProductsDomainService(
            SimilarProductsRepository similarProductsRepository,
            ProductRepository productRepository) {
        return new SimilarProductsDomainService(similarProductsRepository, productRepository);
    }
}