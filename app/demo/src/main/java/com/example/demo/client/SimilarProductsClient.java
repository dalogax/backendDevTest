package com.example.demo.client;

import com.example.demo.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "SimilarProductsClient",
        url = "${similar-products.url}")
public interface SimilarProductsClient {

    @GetMapping("${similar-product-ids.path}")
    List<String> getSimilarProductIds(@PathVariable String productId);

    @GetMapping("${get-product-by-id.path}")
    Product getProductById(@PathVariable String productId);

}
