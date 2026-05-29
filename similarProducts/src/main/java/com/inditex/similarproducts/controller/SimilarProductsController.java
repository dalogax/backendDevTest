package com.inditex.similarproducts.controller;

import com.inditex.similarproducts.model.ProductDetail;
import com.inditex.similarproducts.service.SimilarProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Tag(name = "Similar Products", description = "Operations for retrieving similar products")
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/product/{productId}/similar")
    @Operation(
            summary = "Get similar products",
            description = "Returns the detail of products similar to a given one, ordered by similarity"
    )
    @ApiResponse(responseCode = "200", description = "List of similar products (may be empty)")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "502", description = "External service unavailable")
    public Mono<List<ProductDetail>> getSimilarProducts(
            @Parameter(description = "The product identifier", example = "1")
            @PathVariable String productId) {
        return similarProductsService.getSimilarProducts(productId);
    }
}
