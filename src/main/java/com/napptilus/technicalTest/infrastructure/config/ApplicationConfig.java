package com.napptilus.technicalTest.infrastructure.config;

import com.napptilus.technicalTest.application.services.ProductService;
import com.napptilus.technicalTest.domain.repositories.ProductRepository;
import com.napptilus.technicalTest.infrastructure.adapters.ProductRepositoryAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ProductRepository productRepository(@Value("${application.simulado.host}") String host) {
        return new ProductRepositoryAdapter(host);
    }

    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductService(productRepository);
    }


}
