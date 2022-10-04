package com.dtsanchezz.similarProducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SimilarProductsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SimilarProductsApplication.class, args);
    }

}
