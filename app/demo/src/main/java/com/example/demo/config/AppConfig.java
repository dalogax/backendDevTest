package com.example.demo.config;

import com.example.demo.client.SimilarProductsClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {
        SimilarProductsClient.class
})
public class AppConfig {
}
