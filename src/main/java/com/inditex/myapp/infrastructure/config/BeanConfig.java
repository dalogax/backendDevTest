package com.inditex.myapp.infrastructure.config;

import com.inditex.myapp.domain.service.ProductService;
import com.inditex.myapp.domain.service.impl.ProductServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ProductService productService() {
        return new ProductServiceImpl();
    }
}
