package com.napptilus.technicalTest.infrastructure.controllers;

import com.napptilus.technicalTest.application.services.ProductService;
import com.napptilus.technicalTest.domain.models.Product;
import com.napptilus.technicalTest.infrastructure.data.dto.ProductDto;
import com.napptilus.technicalTest.infrastructure.data.mappers.ProductMapper;
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

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDto>> getRelatedProducts(@PathVariable Integer productId) {
        List<Product> products = this.productService.findRelatedProducts(productId);
        List<ProductDto> productsDto = products.stream().map(ProductMapper::fromDomain).toList();

        return new ResponseEntity<>(productsDto, HttpStatus.OK);
    }

}
