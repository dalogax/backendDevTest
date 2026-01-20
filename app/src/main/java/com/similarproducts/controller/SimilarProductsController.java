package com.similarproducts.controller;

import com.similarproducts.model.ProductDetail;
import com.similarproducts.service.SimilarProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {

    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsController.class);

    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(
            @PathVariable String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
            }

            List<ProductDetail> similarProducts = similarProductsService.getSimilarProducts(productId);

            return ResponseEntity.ok(similarProducts);

        } catch (Exception e) {
            logger.error("Unexpected error while fetching similar products for productId: {}. Error: {}", productId,
                    e.getMessage(), e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
