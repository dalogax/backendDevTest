package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{productId}/similar")
    public ResponseEntity<Set<Product>> getSimilarProducts(@PathVariable String productId) {
        return ResponseEntity.ok(
                productService.getSimilarProducts(productId)
        );
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleFeignException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Product not found");
    }
}
