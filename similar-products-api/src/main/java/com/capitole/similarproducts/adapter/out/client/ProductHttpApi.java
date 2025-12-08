package com.capitole.similarproducts.adapter.out.client;

import com.capitole.similarproducts.generated.model.ProductDetail;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Declarative HTTP client for Product API using OpenAPI-generated models.
 * This interface uses Spring's HTTP Interface to define API operations.
 */
@HttpExchange
public interface ProductHttpApi {

    /**
     * Gets similar product IDs for a given product.
     *
     * @param productId the product ID
     * @return Mono containing list of similar product IDs
     */
    @GetExchange("/product/{productId}/similarids")
    Mono<List<String>> getSimilarProductIds(@PathVariable String productId);

    /**
     * Gets product detail for a given product ID.
     *
     * @param productId the product ID
     * @return Mono containing product detail
     */
    @GetExchange("/product/{productId}")
    Mono<ProductDetail> getProductDetail(@PathVariable String productId);
}
