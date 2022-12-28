package com.test.backenddev;

import com.test.backenddev.client.SimilarProductsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {SimilarProductsClient.class})
public class BackendDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendDevApplication.class, args);
    }

}
