package com.inditex.myapp.application.service;

import com.inditex.myapp.infrastructure.controller.model.ProductDetailDto;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ProductApplicationService {

    ResponseEntity<Set<ProductDetailDto>> getProductSimilar(String productId);

}
