package com.inditex.myapp.infrastructure.controller;

import com.inditex.myapp.controller.ProductApi;
import com.inditex.myapp.model.ProductDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class MyAppController implements ProductApi {

    @Override
    public ResponseEntity<Set<ProductDetail>> getProductSimilar(String productId) {
        return ProductApi.super.getProductSimilar(productId);
    }
}
