package com.danivelasco.products.controller;

import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.service.SimilarProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller that exposes product-related endpoints
 * Provides operations to retrieve similar products based on a given product ID
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final SimilarProductService similarProductService;

    /**
     * Constructor for ProductController
     *
     * @param similarProductService service responsible for retrieving similar products
     */
    public ProductController(SimilarProductService similarProductService) {
        this.similarProductService = similarProductService;
    }

    /**
     * Retrieves similar products for a given product ID
     *
     * @param productId the identifier of the product
     * @return a Mono containing the response with similar product details
     */
    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<ProductDetailsResponse>> getSimilarProducts(@PathVariable String productId) {
        return this.similarProductService.getSimilarProducts(productId)
                .map(ResponseEntity::ok);
    }
}
