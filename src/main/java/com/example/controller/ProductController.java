package com.example.controller;

import com.example.model.ProductDetail;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/{productId}/similar")
    public List<ProductDetail> getSimilarProducts(@PathVariable String productId) {
        return productService.getSimilarProducts(productId);
    }
}
