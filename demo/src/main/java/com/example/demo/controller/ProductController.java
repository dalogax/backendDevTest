package com.example.demo.controller;

import com.example.demo.service.ProductService; 
import com.example.demo.model.Product;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    private final ProductService productService;
    
    // Constructor injection - Spring lo inyecta automáticamente
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/{productId}/similar")
    public List<Product> getSimilarProducts(@PathVariable String productId) {
        return productService.getSimilarProducts(productId);
    }
}