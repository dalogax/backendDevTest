package com.sngular.similarproducts.infrastructure.inbound;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sngular.similarproducts.application.DetailProductsUseCase;
import com.sngular.similarproducts.domain.ProductDetail;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProductController {

    /*
     * Note: To maintain a pure hexagonal architecture, an interface should be used
     * here; however, we are using the implementation for simplicity
     */
    private final DetailProductsUseCase productsService;

    public ProductController(DetailProductsUseCase productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/{productId}/similar")
    public Set<ProductDetail> getProductSimilar(@PathVariable String productId) {
        log.info("GET /product/{}/similar", productId);

        return productsService.getSimilarProducts(productId);
    }
}