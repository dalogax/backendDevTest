package com.product.controller;

import com.product.dto.SimilarProductDTO;
import com.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @GetMapping("/product/{productId}/similar")
    public ResponseEntity<SimilarProductDTO> getProductsById(@PathVariable String productId){
        return ResponseEntity.status(HttpStatus.OK).body(productService.findSimilarProducts(productId));
    }
    
    
}
