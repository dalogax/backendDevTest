package com.backendtest.similar.api;

import com.backendtest.similar.application.port.in.SimilarProductsUseCase;
import com.backendtest.similar.dto.ProductDetail;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimilarController {

    private final SimilarProductsUseCase service;

    public SimilarController(SimilarProductsUseCase service) {
        this.service = service;
    }

    @GetMapping("/product/{productId}/similar")
    public List<ProductDetail> getSimilar(@PathVariable String productId) {
        return service.getSimilarProducts(productId);
    }
}
