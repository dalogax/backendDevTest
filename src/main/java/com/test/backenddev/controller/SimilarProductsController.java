package com.test.backenddev.controller;

import com.test.backenddev.model.Product;
import com.test.backenddev.services.ProductService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class SimilarProductsController {
    @Autowired
    private final ProductService productService;

    @GetMapping("/product/{productId}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getSimilarProducts(productId));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> exceptionHandlerNotFound() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Product not Found");
    }
}