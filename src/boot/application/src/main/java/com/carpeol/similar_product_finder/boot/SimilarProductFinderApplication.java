package com.carpeol.similar_product_finder.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.carpeol.similar_product_finder")
public class SimilarProductFinderApplication {

    static void main(String[] args) {
        SpringApplication.run(SimilarProductFinderApplication.class, args);
    }
}
