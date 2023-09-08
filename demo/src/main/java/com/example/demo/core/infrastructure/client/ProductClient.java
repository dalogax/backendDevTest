package com.example.demo.core.infrastructure.client;

import com.example.demo.core.domain.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "ProductClient", url = "localhost:3001")
public interface ProductClient {

    @GetMapping("/product/{productId}/similarids")
    Set<String> getSimilarProductIDs(@PathVariable String productId);

    @GetMapping("/product/{productId}")
    Product getProduct(@PathVariable String productId);
}