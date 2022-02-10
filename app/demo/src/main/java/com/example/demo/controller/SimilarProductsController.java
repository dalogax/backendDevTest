package com.example.demo.controller;

import com.example.demo.client.SimilarProductsClient;
import com.example.demo.model.Product;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SimilarProductsController {

    private final SimilarProductsClient similarProductsClient;

    @GetMapping("/product/{productId}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(@PathVariable String productId) {
        return ResponseEntity.ok(
                similarProductsClient.getSimilarProductIds(productId)
                        .stream()
                        .map(similarProductsClient::getProductById)
                        .collect(Collectors.toList())
        );
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> notFound() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Product Not found");
    }
}
