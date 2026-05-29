package com.inditex.similarproducts;

import com.inditex.similarproducts.config.ProductApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProductApiProperties.class)
public class SimilarProductsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimilarProductsApplication.class, args);
    }
}
