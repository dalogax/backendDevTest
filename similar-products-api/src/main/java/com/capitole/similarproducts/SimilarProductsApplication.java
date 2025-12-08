package com.capitole.similarproducts;

import com.capitole.similarproducts.configuration.SimilarProductsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot application class for Similar Products API.
 */
@SpringBootApplication
@EnableConfigurationProperties(SimilarProductsProperties.class)
public class SimilarProductsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimilarProductsApplication.class, args);
    }
}
