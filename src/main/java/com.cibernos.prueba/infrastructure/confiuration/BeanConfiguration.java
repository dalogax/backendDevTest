package com.cibernos.prueba.infrastructure.configuration;

import com.cibernos.prueba.application.repository.ProductRepository;
import com.cibernos.prueba.application.services.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public ProductService productService (ProductRepository productRepository){
        return new ProductService(productRepository);
    }

}
