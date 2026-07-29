package com.example.TechnicalAssessment.controller;

import com.example.TechnicalAssessment.model.ProductDetail;
import com.example.TechnicalAssessment.service.SimilarProductService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final SimilarProductService similarProductService;

    public ProductController(SimilarProductService similarProductService) {
        this.similarProductService = similarProductService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        return ResponseEntity.ok(similarProductService.getSimilarProducts(productId));
    }
}
