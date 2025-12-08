package com.capitole.similarproducts.adapter.in.rest;

import com.capitole.similarproducts.adapter.in.rest.dto.ProductDto;
import com.capitole.similarproducts.adapter.in.rest.mapper.ProductMapper;
import com.capitole.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for similar products endpoint.
 */
@RestController
@RequestMapping("/product")
@Tag(name = "Products", description = "Similar Products API")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductMapper productMapper;

    public ProductController(GetSimilarProductsUseCase getSimilarProductsUseCase, ProductMapper productMapper) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
        this.productMapper = productMapper;
    }

    /**
     * Retrieves similar products for a given product ID.
     * Returns list of product details with parallel non-blocking API calls.
     *
     * @param productId the product ID to find similar products for
     * @return Mono containing list of similar products
     */
    @GetMapping(value = "/{productId}/similar", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get similar products",
            description = "Retrieves a list of similar products for a given product ID with their detailed information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved similar products",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable - external service error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    public Mono<@NonNull ResponseEntity<@NonNull List<ProductDto>>> getSimilarProducts(
            @Parameter(description = "Product ID to find similar products for", required = true)
            @PathVariable String productId) {

        LOGGER.info("Received request for similar products of productId: {}", productId);

        return getSimilarProductsUseCase.getSimilarProducts(productId)
                .map(productMapper::toDtoList)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> LOGGER.info("Successfully processed request for productId: {}", productId))
                .doOnError(error -> LOGGER.error("Error processing request for productId: {}", productId, error));
    }
}
