package com.napptilus.technicalTest.infrastructure.config;

import com.napptilus.technicalTest.application.services.ProductService;
import com.napptilus.technicalTest.domain.repositories.ProductRepository;
import com.napptilus.technicalTest.infrastructure.adapters.ProductRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductService(productRepository);
    }

    @Bean
    public ProductRepository productRepository(ProductRepositoryAdapter productRepositoryAdapter) {
        return productRepositoryAdapter;
    }
}
