package com.example.proxy.controller;

import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(path = "/{productId}/similar")
    public ResponseEntity<List<ProductDetailDTO>> getSimilarProducts(@PathVariable(value = "productId") String productId) {
        return new ResponseEntity<>(productService.getSimilarProducts(productId), HttpStatus.OK);
    }
}
