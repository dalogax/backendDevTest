package dev.molaya.tests;

import dev.molaya.tests.application.config.SimilarProductsConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SimilarProductsConfigurationProperties.class)
public class BackProductsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackProductsApplication.class, args);
    }
}
