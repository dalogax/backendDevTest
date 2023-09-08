package com.example.demo;

import com.example.demo.core.infrastructure.SpringDependencies;
import com.example.demo.core.main.Provider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.demo.core.infrastructure.client")
public class SimilarProductsApplication {

    private final SpringDependencies dependencies;

    public SimilarProductsApplication(SpringDependencies dependencies) {
        this.dependencies = dependencies;
    }

    public static void main(String[] args) {
        SpringApplication.run(SimilarProductsApplication.class, args);
    }

    @Bean
    public Provider provider() {
        return new Provider(dependencies);
    }
}
