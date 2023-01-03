package com.prueba.products.controller;

import com.prueba.products.model.Product;
import com.prueba.products.service.SimilarProductsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
@Slf4j
@RestController
@AllArgsConstructor
public class SimilarProductsController {

    @Autowired
    SimilarProductsService similarProductsService;

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    @GetMapping("/product/{productId}/similar")
    public Object getSimilarProducts(@PathVariable String productId) {
        log.debug("Similar products Controller is calling with productId: {}", productId);
        List<Product> products = similarProductsService.getSimilarProducts(productId);

        if(products == null) {
            log.error("Not similar products found with productId: {}", productId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not Found");
        }
        return ResponseEntity.ok(products);
    }
}
