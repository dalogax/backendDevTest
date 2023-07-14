package com.alejandro.api.controller;

import com.alejandro.api.dto.ProductDetailDTO;
import com.alejandro.api.service.ISimilarProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name = "Similar products")
@RequiredArgsConstructor
public class SimilarProductsController {

    private final ISimilarProductsService similarProductsService;

    @GetMapping("/{productId}/similar")
    @Operation(summary = "Get similar products based on a product id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Product/s not found.", content = @Content)
    })
    public ResponseEntity<List<ProductDetailDTO>> getSimilarProducts(@PathVariable String productId) {
        return ResponseEntity.ok().body(similarProductsService.getSimilarProducts(productId));
    }
}

