package com.inditex.myapp.infrastructure.controller;

import com.inditex.myapp.application.service.ProductApplicationService;

import com.inditex.myapp.infrastructure.controller.model.ProductDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class MyAppController implements ProductApi {

    @Autowired
    private ProductApplicationService productApplicationService;

    @Override
    public ResponseEntity<Set<ProductDetailDto>> getProductSimilar(String productId) {
        return productApplicationService.getProductSimilar(productId);
    }
}
