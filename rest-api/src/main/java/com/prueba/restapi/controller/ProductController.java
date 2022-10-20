package com.prueba.restapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prueba.restapi.dto.ProductDto;
import com.prueba.restapi.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<Integer>> getProductSimilarIds(@PathVariable int productId) {
        return ResponseEntity.ok().body(productService.getProductSimilarIds(productId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int productId) {
        return ResponseEntity.ok().body(productService.getProductById(productId));
    }

}
