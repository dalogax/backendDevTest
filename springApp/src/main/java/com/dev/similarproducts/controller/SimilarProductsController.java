package com.dev.similarproducts.controller;

import com.dev.similarproducts.dto.ProductDetailDto;
import com.dev.similarproducts.exception.ExternalApiException;
import com.dev.similarproducts.service.SimilarProductsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {
    private final SimilarProductsServiceImpl similarProductsServiceImpl;

    @Autowired
    public SimilarProductsController(SimilarProductsServiceImpl similarProductsServiceImpl) {
        this.similarProductsServiceImpl = similarProductsServiceImpl;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetailDto>> getSimilarProducts(@PathVariable String productId) {
        try {
            List<ProductDetailDto> similarProducts = similarProductsServiceImpl.getSimilarProducts(productId);
            return new ResponseEntity<>(similarProducts, HttpStatus.OK);
        } catch (ExternalApiException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }
}

