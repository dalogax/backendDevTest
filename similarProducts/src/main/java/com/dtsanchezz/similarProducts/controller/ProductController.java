package com.dtsanchezz.similarProducts.controller;

import com.dtsanchezz.similarProducts.model.ProductDetail;
import com.dtsanchezz.similarProducts.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(
            @PathVariable final String productId
    ) {

        final List<ProductDetail> productDetailsList = this.productService.getSimilarProducts(productId);
        return ResponseEntity.ok().body(productDetailsList);
    }
}
