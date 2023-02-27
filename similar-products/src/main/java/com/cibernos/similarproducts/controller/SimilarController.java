package com.cibernos.similarproducts.controller;

import com.cibernos.similarproducts.dto.ProductInfoDto;
import com.cibernos.similarproducts.exception.SimilarProductException;
import com.cibernos.similarproducts.service.SimilarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SimilarController {
    Logger log = LoggerFactory.getLogger(SimilarController.class);

    @Autowired
    private SimilarService similarService;

    @GetMapping("/{productId}/similar")
    public ResponseEntity<?> getSimilarProducts(@PathVariable String productId) {
        log.info("getSimilarProductsController(): {}", productId);
        try {
            List<ProductInfoDto> similarProducts = similarService.getSimilarProducts(productId);
            log.info("List of similar product info(): {}", similarProducts);
            return new ResponseEntity<>(similarProducts, HttpStatus.OK);
        } catch (SimilarProductException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }
}
