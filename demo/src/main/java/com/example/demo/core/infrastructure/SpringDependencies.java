package com.example.demo.core.infrastructure;

import com.example.demo.core.domain.ProductRepository;
import com.example.demo.core.main.Dependencies;
import org.springframework.stereotype.Service;

@Service
public class SpringDependencies implements Dependencies {

    ProductRepository productRepository;

    public SpringDependencies(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductRepository productRepository() {
        return productRepository;
    }
}
