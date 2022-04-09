package com.example.demo.client;

import com.example.demo.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "SimilarProductsClient", url = "localhost:3001")
public interface SimilarProductsClient {

    @GetMapping("/product/{productId}/similarids")
    Set<String> getSimilarProductIDs(@PathVariable String productId);

    @GetMapping("/product/{productId}")
    Product getProduct(@PathVariable String productId);
}
