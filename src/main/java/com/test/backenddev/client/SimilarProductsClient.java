package com.test.backenddev.client;

import com.test.backenddev.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "SimilarProductsClient", url = "localhost:3001")
public interface SimilarProductsClient {

    @GetMapping(value = "/product/{productId}")
    Product getProductById(@PathVariable String productId);

    @GetMapping(value = "/product/{productId}/similarids")
    List<String> getSimilarProductIds(@PathVariable String productId);

}